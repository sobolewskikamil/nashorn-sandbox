package sobolee.nashornSandbox;

import sobolee.nashornSandbox.evaluator.MeasuredNashornEvaluator;
import sobolee.nashornSandbox.evaluator.NashornEvaluator;
import sobolee.nashornSandbox.evaluator.SimpleNashornEvaluator;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;
import static sobolee.nashornSandbox.remote.JvmInstance.setPossibleInactivityTime;

public class NashornSandbox implements Sandbox {
    private int memoryLimit = 200;
    private NashornEvaluator evaluator = new SimpleNashornEvaluator(1, memoryLimit);

    private SandboxClassFilter sandboxClassFilter = new SandboxClassFilter();

    @Override
    public CompletableFuture<Object> evaluate(String script, Map<String, Object> args) {
        requireNonNull(script, "script cannot be null");
        requireNonNull(args, "args cannot be null");

        ScriptEvaluationRequest evaluationRequest = new ScriptEvaluationRequest(script, args);
        return CompletableFuture.supplyAsync(() -> evaluator.evaluate(evaluationRequest));
    }

    @Override
    public CompletableFuture<Object> invokeFunction(String function, String script, List<Object> args) {
        requireNonNull(function, "function cannot be null");
        requireNonNull(script, "script cannot be null");
        requireNonNull(args, "args cannot be null");

        FunctionEvaluationRequest evaluationRequest = new FunctionEvaluationRequest(function, script, args);
        return CompletableFuture.supplyAsync(() -> evaluator.evaluate(evaluationRequest));
    }

    @Override
    public void allowClasses(Collection<Class<?>> classes) {
        classes.stream()
                .map(Class::getName)
                .forEach(c -> sandboxClassFilter.add(c));
        evaluator.applyFilter(sandboxClassFilter);
    }

    @Override
    public void disallowClasses(Collection<Class<?>> classes) {
        classes.stream()
                .map(Class::getName)
                .forEach(c -> sandboxClassFilter.remove(c));
        evaluator.applyFilter(sandboxClassFilter);
    }

    @Override
    public void setInactiveTimeout(int seconds) {
        setPossibleInactivityTime(seconds);
    }

    @Override
    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    @Override
    public void setCpuLimit(int cpuLimit) {
        evaluator.setCpuLimit(cpuLimit);
    }

    @Override
    public void setMaxNumberOfInstances(int number) {
        evaluator.setMaxNumberOfInstances(number);
    }

    public static class NashornSandboxBuildingFacade implements SandboxBuildingFacade {
        private final NashornSandbox sandbox = new NashornSandbox();

        @Override
        public SandboxBuildingFacade withMemoryLimit(int memoryLimit) {
            sandbox.setMemoryLimit(memoryLimit);
            return this;
        }

        @Override
        public SandboxBuildingFacade withInactiveTimeout(int seconds) {
            sandbox.setInactiveTimeout(seconds);
            return this;
        }

        @Override
        public SandboxBuildingFacade withCpuLimit(int cpuLimit) {
            sandbox.setCpuLimit(cpuLimit);
            return this;
        }

        @Override
        public SandboxBuildingFacade withAllowedClasses(List<Class<?>> classes) {
            sandbox.allowClasses(classes);
            return this;
        }

        @Override
        public SandboxBuildingFacade withDisallowedClasses(List<Class<?>> classes) {
            sandbox.disallowClasses(classes);
            return this;
        }

        @Override
        public SandboxBuildingFacade withTimeMeasure() {
            sandbox.evaluator = new MeasuredNashornEvaluator(sandbox.evaluator);
            return this;
        }

        @Override
        public SandboxBuildingFacade withMaxNumberOfInstances(int number) {
            sandbox.setMaxNumberOfInstances(number);
            return this;
        }

        @Override
        public Sandbox build() {
            return sandbox;
        }
    }
}
