package sobolee.nashornSandbox;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class NashornProcessor extends UnicastRemoteObject implements NashornProcessorInterface {
    private ScriptEngine engine;
    private boolean interrupt = false;

    static Registry reg;
    static NashornProcessor np;

    public static void main(String[] args){
        System.setProperty("java.security.policy","./nashorn.policy");

        try {
            np = new NashornProcessor();
            //reg = LocateRegistry.createRegistry(1099);
            reg = LocateRegistry.getRegistry(1099);
            reg.rebind("NashornProcessor", np);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        while(!np.isInterrupted()){}

        try {
            reg.unbind("NashornProcessor");
        } catch (RemoteException|NotBoundException e) {
            e.printStackTrace();
        }
    }

    public NashornProcessor() throws RemoteException {
        super();
        engine = new ScriptEngineManager().getEngineByName("nashorn");
    }
    public void executeJs(String js) throws RemoteException{
        try {
            engine.eval(js);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public Object invokeFunction(String function, String arg) throws RemoteException{
        Invocable inv = (Invocable) engine;
        Object result = null;
        try {
            result = inv.invokeFunction(function, arg);
        } catch (ScriptException|NoSuchMethodException e) {
            e.printStackTrace();
        }
        return result;
    }

    public synchronized boolean isInterrupted(){
        return interrupt;
    }

    public synchronized void close() throws RemoteException{
        interrupt = true;
    }
}
