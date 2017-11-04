package sobolee.nashornSandbox;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NashornProcessorInterface extends Remote {
    void executeJs(String js) throws RemoteException;
    Object invokeFunction(String function, String arg) throws RemoteException;
    void close() throws RemoteException;
}
