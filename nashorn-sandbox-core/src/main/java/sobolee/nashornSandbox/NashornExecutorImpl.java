package sobolee.nashornSandbox;

import org.joda.time.DateTime;

import javax.script.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

import static org.joda.time.DateTime.now;

public class NashornExecutorImpl extends UnicastRemoteObject implements NashornExecutor {
    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private DateTime timeOfLastRequest;

    NashornExecutorImpl() throws RemoteException {
        super();
    }

    @Override
    public Object execute(String script, Map<String, Object> args) throws RemoteException {
        Bindings bindings = new SimpleBindings();
        bindings.putAll(args);
        //System.out.println("Evaluation process: " + ProcessHandle.current().pid());
        try {
            Object result = engine.eval(script, bindings);
            timeOfLastRequest = now();
            return result;
        } catch (ScriptException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public Object invokeFunction(String function, String script, Map<String, Object> args) throws RemoteException {
        try {
            engine.eval(script);
            Invocable invocable = (Invocable) engine;
            Object result = invocable.invokeFunction(function, args);
            timeOfLastRequest = now();
            return result;
        } catch (ScriptException | NoSuchMethodException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public DateTime getTimeOfLastRequest() {
        return timeOfLastRequest;
    }
}
