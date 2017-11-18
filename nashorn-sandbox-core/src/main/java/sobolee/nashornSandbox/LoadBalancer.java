package sobolee.nashornSandbox;

import sobolee.nashornSandbox.exceptions.EnvironmentSetupException;
import sobolee.nashornSandbox.requests.EvaluationRequest;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

public class LoadBalancer {
    private List<EvaluationUnit> evaluationUnits = new ArrayList<>();
    private long memoryPerInstance;
    private int numberOfInstances;
    private static Registry registry;

    static {
        try {
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public LoadBalancer(int numberOfInstances, long memoryPerInstance) {
        this.memoryPerInstance = memoryPerInstance;
        this.numberOfInstances = numberOfInstances;

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }


    public Object evaluate(EvaluationRequest evaluationRequest) {
        EvaluationUnit evaluationUnit = loadBalance();
        NashornExecutor executor = getExecutor(evaluationUnit.getId());
        try {
            evaluationUnit.setEvaluating(true);
            Object result = executor.execute(evaluationRequest);
            evaluationUnit.setEvaluating(false);
            return result;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private NashornExecutor getExecutor(String id) {
        boolean isBound = false;
        NashornExecutor np = null;
        while (!isBound) {
            try {
                np = (NashornExecutor) LocateRegistry.getRegistry(1099).lookup(id);
                isBound = true;
            } catch (RemoteException | NotBoundException ignored) {
            }
        }
        return np;
    }

    private EvaluationUnit loadBalance() {
        if (evaluationUnits.size() == numberOfInstances) {
            return waitForAvailableEvaluationUnit();
        }
        for (EvaluationUnit evaluationUnit : evaluationUnits) {
            if (!evaluationUnit.isEvaluating()) {
                return evaluationUnit;
            }
        }
        return startProcess();
    }

    private EvaluationUnit waitForAvailableEvaluationUnit() {
        while (true) {
            for (EvaluationUnit evaluationUnit : evaluationUnits) {
                if (!evaluationUnit.isEvaluating()) {
                    return evaluationUnit;
                }
            }
        }
    }

    private EvaluationUnit startProcess() {
        String id = UUID.randomUUID().toString();
        Process process = startProcessWithId(id);
        EvaluationUnit evaluationUnit = new EvaluationUnit(id, process, false);
        evaluationUnits.add(evaluationUnit);
        return evaluationUnit;
    }

    private Process startProcessWithId(String id) {
        String javaBin = getJavaBin();
        String classpath = System.getProperty("java.class.path");
        String className = (JvmInstance.class).getCanonicalName();
        String heapSize = format("-Xmx%sm", memoryPerInstance);

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, heapSize, "-cp", classpath, className, id);

        builder.inheritIO();
        try {
            return builder.start();
        } catch (IOException e) {
            throw new EnvironmentSetupException("Unable to start JVM");
        }
    }

    private String getJavaBin() {
        String javaHome = System.getProperty("java.home");
        return javaHome + File.separator +
                "bin" + File.separator +
                "java";
    }

    private void close() {
        for (EvaluationUnit eu : evaluationUnits) {
            Process process = eu.getProcess();
            process.destroyForcibly();
        }
        try {
            UnicastRemoteObject.unexportObject(registry, true);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
