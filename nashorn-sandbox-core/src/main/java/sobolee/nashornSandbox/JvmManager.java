package sobolee.nashornSandbox;

import sobolee.nashornSandbox.loadbalancing.LoadBalancer;
import sobolee.nashornSandbox.remote.JvmInstance;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;

public class JvmManager {
    private final List<EvaluationUnit> evaluationUnits = new ArrayList<>();
    private final long memoryPerInstance;

    public JvmManager(long memoryPerInstance) {
        this.memoryPerInstance = memoryPerInstance;

        getRuntime().addShutdownHook(new Thread(this::close));
    }

    public List<EvaluationUnit> getEvaluationUnits() {
        return evaluationUnits;
    }

    public EvaluationUnit start(LoadBalancer loadBalancer) {
        String id = randomUUID().toString();
        Process process = startProcessWithId(id);
        EvaluationUnit evaluationUnit = new EvaluationUnit(id, process, false);
        evaluationUnit.registerObserver(loadBalancer);
        evaluationUnits.add(evaluationUnit);
        return evaluationUnit;
    }

    private void close() {
        for (EvaluationUnit eu : evaluationUnits) {
            Process process = eu.getProcess();
            process.destroyForcibly();
        }
        evaluationUnits.clear();
    }

    private Process startProcessWithId(String id) {
        String javaBin = getJavaBin();
        String classpath = System.getProperty("java.class.path");
        String className = (JvmInstance.class).getCanonicalName();
        String heapSize = format("-Xmx%sm", memoryPerInstance);

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, heapSize, "-cp", classpath, className, id);

        try {
            return builder.start();
        } catch (IOException e) {
            throw new UncheckedIOException("IOException during JVM creation", e);
        }
    }

    private String getJavaBin() {
        String javaHome = System.getProperty("java.home");
        return javaHome + File.separator +
                "bin" + File.separator +
                "java";
    }

}
