package sobolee.nashornSandbox;

import org.joda.time.Seconds;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import static java.rmi.registry.LocateRegistry.getRegistry;
import static org.joda.time.DateTime.now;
import static org.joda.time.Seconds.seconds;

public class JvmInstance {
    private static NashornExecutor nashornExecutor;
    private static Seconds possibleInactivityTime = seconds(1);
    private static Registry registry;

    public static void main(String[] args) {
        String nashornExecutorId = args[0];
        setupEnvironment(nashornExecutorId);
        waitForRequests();
        unbindRegistry(nashornExecutorId);
    }

    public static void setPossibleInactivityTime(int seconds) {
        possibleInactivityTime = seconds(seconds);
    }

    private static void setupEnvironment(String id) {
        System.setProperty("java.security.policy", "./nashorn.policy");
        try {
            registry = getRegistry(1099);
            nashornExecutor = new NashornExecutorImpl();
            registry.rebind(id, nashornExecutor);
        } catch (RemoteException e) {
        }
    }

    private static void waitForRequests() {
        while (isActive()) ;
    }

    private static void unbindRegistry(String id) {
        try {
            registry.unbind(id);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean isActive() {
        try {
            if (nashornExecutor.getTimeOfLastRequest() == null) {
                return true;
            } else {
                Seconds timeDifference = Seconds.secondsBetween(nashornExecutor.getTimeOfLastRequest(), now());
                return timeDifference.isLessThan(possibleInactivityTime);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
