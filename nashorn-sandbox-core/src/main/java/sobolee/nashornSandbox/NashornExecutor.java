package sobolee.nashornSandbox;

import org.joda.time.DateTime;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface NashornExecutor extends Remote {

    Object execute(String script, Map<String, Object> args) throws RemoteException;

    Object invokeFunction(String function, String script, Map<String, Object> args) throws RemoteException;

    DateTime getTimeOfLastRequest() throws RemoteException;
}
