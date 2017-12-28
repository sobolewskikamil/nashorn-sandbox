package sobolee.nashornSandbox.remote;

import org.joda.time.DateTime;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;

import javax.script.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

import static org.joda.time.DateTime.now;

public class NashornExecutorImpl extends UnicastRemoteObject implements NashornExecutor {
    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private DateTime timeOfLastRequest;

    NashornExecutorImpl() throws RemoteException {
        super();
    }

    @Override
    public Object execute(FunctionEvaluationRequest evaluationRequest) throws RemoteException {
        String function = evaluationRequest.getFunction();
        String script = evaluationRequest.getScript();
        List<Object> args = evaluationRequest.getArgs();
        try {
            engine.eval(script);
            Invocable invocable = (Invocable) engine;
            Object result = invocable.invokeFunction(function, args.toArray());
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
            Object result = engine.eval(script, bindings);
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
}
