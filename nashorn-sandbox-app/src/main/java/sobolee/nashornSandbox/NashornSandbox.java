package sobolee.nashornSandbox;

import org.springframework.beans.factory.annotation.Autowired;
import sobolee.nashornSandbox.remote.JvmInstance;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

public class NashornSandbox implements Sandbox {
    private int memoryLimit = 200;
    private int cpuLimit = 0;
    private final NashornEvaluator evaluator = new NashornEvaluator(1, memoryLimit);

    @Autowired
    private SandboxClassFilter sandboxClassFilter = new SandboxClassFilter();

    @Autowired
    private SandboxPermissions sandboxPermissions = new SandboxPermissions();

    private NashornSandbox() {
    }

    /**
     * Safely evaluates script. Result of the last line in the script will be returned as a result.
     *
     * @param script Script to evaluate.
     * @param args   Actual argument objects with referring identifiers.
     * @return Result of evaluation of the last line in the script.
     */
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
    public void allowClass(Class<?> aClass) {
        sandboxClassFilter.add(aClass.getName());
        evaluator.applyFilter(sandboxClassFilter);
    }

    @Override
    public void disallowClass(Class<?> aClass) {
        sandboxClassFilter.remove(aClass.getName());
        evaluator.applyFilter(sandboxClassFilter);
    }

    @Override
    public void allowAction(SandboxPermissions.Action action) {
        sandboxPermissions.allowAction(action);
        evaluator.applyPermissions(sandboxPermissions);
    }

    @Override
    public void disallowAction(SandboxPermissions.Action action) {
        sandboxPermissions.disallowAction(action);
        evaluator.applyPermissions(sandboxPermissions);
    }

    @Override
    public void setInactiveTimeout(int seconds) {
        JvmInstance.setPossibleInactivityTime(seconds);
    }

    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    @Override
    public void setCpuLimit(int cpuLimit){
        this.cpuLimit = cpuLimit;
        evaluator.setCpuLimit(cpuLimit);
    }

    public static class NashornSandboxBuilder implements SandboxBuilder {
        private final NashornSandbox sandbox = new NashornSandbox();

        @Override
        public SandboxBuilder withMemoryLimit(int memoryLimit) {
            sandbox.setMemoryLimit(memoryLimit);
            return this;
        }

        @Override
        public SandboxBuilder withInactiveTimeout(int seconds) {
            sandbox.setInactiveTimeout(seconds);
            return this;
        }

        @Override
        public SandboxBuilder withCpuLimit(int cpuLimit) {
            sandbox.setCpuLimit(cpuLimit);
            return this;
        }

        @Override
        public Sandbox build() {
            return sandbox;
        }
    }
}
