package sobolee.nashornSandbox.remote;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.joda.time.DateTime;
import sobolee.nashornSandbox.SandboxClassFilter;
import sobolee.nashornSandbox.requests.EvaluationRequest;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;

import javax.script.ScriptEngine;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.joda.time.DateTime.now;

public class NashornExecutorImpl extends UnicastRemoteObject implements NashornExecutor {
    private static ScriptEngine engine;
    private static final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
    private DateTime timeOfLastRequest;
    private int cpuLimit;
    private SandboxClassFilter filter = new SandboxClassFilter();
    private final Lock LOCK = new ReentrantLock();

    public NashornExecutorImpl() throws RemoteException {
        super();
        engine = factory.getScriptEngine(filter);
    }

    @Override
    public Object execute(FunctionEvaluationRequest evaluationRequest) throws RemoteException {
        return safeEval(evaluationRequest);
    }

    @Override
    public Object execute(ScriptEvaluationRequest evaluationRequest) throws RemoteException {
        return safeEval(evaluationRequest);
    }

    @Override
    public DateTime getTimeOfLastRequest() {
        return timeOfLastRequest;
    }

    @Override
    public void applyFilter(SandboxClassFilter filter) throws RemoteException {
        engine = factory.getScriptEngine(filter);
        this.filter = filter;
    }

    @Override
    public void setCpuLimit(int limit) throws RemoteException {
        cpuLimit = limit;
    }

    private Object safeEval(EvaluationRequest request) throws RemoteException {
        SafeEvaluator safeEvaluator = new SafeEvaluator(engine, request, cpuLimit);

        Object res;
        synchronized (LOCK) {
            safeEvaluator.evaluate();
            res = safeEvaluator.getResult();
            timeOfLastRequest = now();
            resetEngine();
        }

        return res;
    }

    private void resetEngine(){
        if(filter != null){
            engine = factory.getScriptEngine(filter);
        }
        else {
            engine = factory.getScriptEngine();
        }
    }
}
