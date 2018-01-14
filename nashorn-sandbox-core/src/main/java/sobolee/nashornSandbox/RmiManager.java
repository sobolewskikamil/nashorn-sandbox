package sobolee.nashornSandbox;

import sobolee.nashornSandbox.remote.NashornExecutor;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static java.lang.Runtime.getRuntime;
import static java.rmi.registry.LocateRegistry.createRegistry;
import static java.rmi.server.UnicastRemoteObject.unexportObject;

public class RmiManager {
    private final static Registry REGISTRY;
    private static final int PORT = 1099;

    public RmiManager() {
        getRuntime().addShutdownHook(new Thread(this::close));
    }

    static {
        try {
            REGISTRY = createRegistry(1099);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public NashornExecutor getExecutor(String id) {
        boolean isBound = false;
        NashornExecutor np = null;
        while (!isBound) {
            try {
                np = (NashornExecutor) LocateRegistry.getRegistry(PORT).lookup(id);
                isBound = true;
            } catch (RemoteException | NotBoundException ignored) {
            }
        }
        return np;
    }

    private void close() {
        try {
            unexportObject(REGISTRY, true);
        } catch (NoSuchObjectException ignored) {
        }
    }
}
