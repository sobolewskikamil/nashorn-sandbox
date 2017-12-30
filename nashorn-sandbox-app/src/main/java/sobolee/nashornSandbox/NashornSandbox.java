package sobolee.nashornSandbox;

import org.springframework.beans.factory.annotation.Autowired;
import sobolee.nashornSandbox.remote.JvmInstance;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.requireNonNull;

public class NashornSandbox implements Sandbox {
    private long memoryLimit = 200;
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

    public void allowClass(Class<?> aClass) {
        sandboxClassFilter.add(aClass.getName());
        evaluator.applyFilter(sandboxClassFilter);
    }

    public void disallowClass(Class<?> aClass) {
        sandboxClassFilter.remove(aClass.getName());
        evaluator.applyFilter(sandboxClassFilter);
    }

    public void allowAction(SandboxPermissions.Action action) {
        sandboxPermissions.allowAction(action);
        evaluator.applyPermissions(sandboxPermissions);
    }

    public void disallowAction(SandboxPermissions.Action action) {
        sandboxPermissions.disallowAction(action);
        evaluator.applyPermissions(sandboxPermissions);
    }

    private void setInactiveTimeout(int seconds) {
        JvmInstance.setPossibleInactivityTime(seconds);
    }

    private void setMemoryLimit(long memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public static class NashornSandboxBuilder implements SandboxBuilder {
        private final NashornSandbox sandbox = new NashornSandbox();

        public SandboxBuilder withMemoryLimit(long memoryLimit) {
            sandbox.setMemoryLimit(memoryLimit);
            return this;
        }

        public SandboxBuilder withInactiveTimeout(int seconds) {
            sandbox.setInactiveTimeout(seconds);
            return this;
        }

        @Override
        public Sandbox build() {
            return sandbox;
        }
    }
}
