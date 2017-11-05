package sobolee.nashornSandbox;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface NashornExecutor extends Remote {

    Object executeJs(String script, Map<String, Object> args) throws RemoteException;

    void close() throws RemoteException;
}
