package sobolee.nashornSandbox;

import javax.script.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class NashornExecutorImpl extends UnicastRemoteObject implements NashornExecutor {

    private final ScriptEngine engine;
    private boolean interrupt = false;

    private static Registry reg;
    private static NashornExecutorImpl np;

    public NashornExecutorImpl() throws RemoteException {
        super();
        engine = new ScriptEngineManager().getEngineByName("nashorn");
    }

    @Override
    public Object execute(String script, Map<String, Object> args) throws RemoteException {
        Bindings bindings = new SimpleBindings();
        bindings.putAll(args);
        try {
            return engine.eval(script, bindings);
        } catch (ScriptException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public Object invokeFunction(String script, Map<String, Object> args) throws RemoteException {
        return null;
    }

    @Override
    public synchronized void close() throws RemoteException {
        interrupt = true;
    }

    public static void main(String[] args) {
        setupEnvironment(args[0]);
        while (!np.isInterrupted()) {
        }
        unbindRegistry(args[0]);
    }

    public synchronized boolean isInterrupted() {
        return interrupt;
    }

    private static void setupEnvironment(String id) {
        System.setProperty("java.security.policy", "./nashorn.policy");

        try {
            System.out.print("Binding... ");
            np = new NashornExecutorImpl();
            reg = LocateRegistry.getRegistry(1099);
            reg.rebind(id, np);
            System.out.println("Success!");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void unbindRegistry(String id) {
        try {
            System.out.println("Unbinding");
            reg.unbind(id);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}