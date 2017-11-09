package sobolee.nashornSandbox;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import static java.rmi.registry.LocateRegistry.getRegistry;
import static org.joda.time.Seconds.seconds;

public class JvmInstance {
    private static NashornExecutor nashornExecutor;
    private static Seconds possibleInactivityTime = seconds(1000);
    private static Registry registry;

    public static void main(String[] args) {
        String nashornExecutorId = args[0];
        setupEnvironment(nashornExecutorId);
        processRequests();
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
            e.printStackTrace();
        }
    }

    private static void processRequests() {
        while (isActive()) {
        }
    }

    private static void unbindRegistry(String id) {
        try {
            registry.unbind(id);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean isActive() {
        Seconds timeDifference = Seconds.secondsBetween(nashornExecutor.getTimeOfLastRequest(), DateTime.now());
        return timeDifference.isLessThan(possibleInactivityTime);
    }
}
