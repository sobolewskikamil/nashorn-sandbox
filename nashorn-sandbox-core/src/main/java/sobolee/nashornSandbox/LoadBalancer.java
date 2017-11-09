package sobolee.nashornSandbox;

import sobolee.nashornSandbox.exceptions.EnvironmentSetupException;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;

public class LoadBalancer {
    private List<EvaluationUnit> evaluationUnits = new ArrayList<>();
    private long memoryPerInstance;
    private int numberOfInstances;

    public LoadBalancer(int numberOfInstances, long memoryPerInstance) {
        this.memoryPerInstance = memoryPerInstance;
        this.numberOfInstances = numberOfInstances;
    }

    public void start() {
        tryToCreateRegistry();
        for (int i = 0; i < numberOfInstances; i++) {
            String id = UUID.randomUUID().toString();
            Process process = startProcess(id);
            evaluationUnits.add(new EvaluationUnit(id, process));
        }
    }

    public Object evaluate(String script, Map<String, Object> args) {
        String id = loadBalance();
        NashornExecutor executor = getExecutor(id);
        try {
            return executor.execute(script, args);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Object invokeFunction(String function, String script, Map<String, Object> args) {
        String id = loadBalance();
        NashornExecutor executor = getExecutor(id);
        try {
            return executor.invokeFunction(function, script, args);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryToCreateRegistry() {
        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException ignored) {
        }
    }

    private NashornExecutor getExecutor(String id) {
        boolean isBound = false;
        NashornExecutor np = null;
        while (!isBound) {
            try {
                np = (NashornExecutor) LocateRegistry.getRegistry(1099).lookup(id);
                isBound = true;
            } catch (RemoteException | NotBoundException e) {
            }
        }
        return np;
    }

    private String loadBalance() {
        return evaluationUnits.get(0).getId();
    }

    private Process startProcess(String id) {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator +
                "bin" + File.separator +
                "java";
        String classpath = System.getProperty("java.class.path");
        String className = (NashornExecutorImpl.class).getCanonicalName();
        String heapSize = format("-Xmx%sm", memoryPerInstance);

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, heapSize, "-cp", classpath, className, id);

        try {
            return builder.start();
        } catch (IOException e) {
            throw new EnvironmentSetupException("Unable to start JVM");
        }
    }
}
