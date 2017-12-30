package sobolee.nashornSandbox.remote;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.joda.time.DateTime;
import sobolee.nashornSandbox.SandboxClassFilter;
import sobolee.nashornSandbox.SandboxPermissions;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;

import javax.script.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.joda.time.DateTime.now;

public class NashornExecutorImpl extends UnicastRemoteObject implements NashornExecutor {
    private static ScriptEngine engine;
    private static final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
    private DateTime timeOfLastRequest;
    private final Lock LOCK = new ReentrantLock();

    public NashornExecutorImpl() throws RemoteException {
        super();
        engine = factory.getScriptEngine();
    }

    @Override
    public Object execute(FunctionEvaluationRequest evaluationRequest) throws RemoteException {
        String function = evaluationRequest.getFunction();
        String script = evaluationRequest.getScript();
        List<Object> args = evaluationRequest.getArgs();
        try {
            Object result;
            synchronized (LOCK) {
                engine.eval(script);
                Invocable invocable = (Invocable) engine;
                result = invocable.invokeFunction(function, args.toArray());
            }
            timeOfLastRequest = now();
            return result;
        } catch (ScriptException | NoSuchMethodException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public Object execute(ScriptEvaluationRequest evaluationRequest) throws RemoteException {
        String script = evaluationRequest.getScript();
        Map<String, Object> args = evaluationRequest.getArgs();
        Bindings bindings = new SimpleBindings();
        bindings.putAll(args);
        try {
            Object result;
            synchronized (LOCK) {
                result = engine.eval(script, bindings);
            }
            timeOfLastRequest = now();
            return result;
        } catch (ScriptException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public DateTime getTimeOfLastRequest() {
        return timeOfLastRequest;
    }

    @Override
    public void applyFilter(SandboxClassFilter filter) throws RemoteException {
        engine = factory.getScriptEngine(filter);
    }

    @Override
    public void applyPermissions(SandboxPermissions permissions) throws RemoteException {
        final StringBuilder sb = new StringBuilder();
        if(!permissions.isAllowed(SandboxPermissions.Action.PRINT_FUNCTIONS)){
            sb.append("print=function(){};echo=function(){};");
        }
        if(!permissions.isAllowed(SandboxPermissions.Action.READ_FUNCTIONS)){
            sb.append("readFully=function(){};").append("readLine=function(){};");
        }
        if(!permissions.isAllowed(SandboxPermissions.Action.LOAD_FUNCTIONS)){
            sb.append("load=function(){};loadWithNewGlobal=function(){};");
        }
        if(!permissions.isAllowed(SandboxPermissions.Action.EXIT_FUNCTIONS)){
            sb.append("quit=function(){};exit=function(){};");
        }
        if(!permissions.isAllowed(SandboxPermissions.Action.GLOBALS_OBJECTS)){
            sb.append("$ARG=null;$ENV=null;$EXEC=null;");
            sb.append("$OPTIONS=null;$OUT=null;$ERR=null;$EXIT=null;");
        }
        try {
            synchronized (LOCK) {
                engine.eval(sb.toString());
            }
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }
}
