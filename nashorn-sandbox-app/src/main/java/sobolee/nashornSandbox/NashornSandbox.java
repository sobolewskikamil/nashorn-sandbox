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
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private long memoryLimit = 200;
    private final NashornEvaluator evaluator = new NashornEvaluator(1, memoryLimit);

    @Autowired
    private SandboxClassFilter sandboxClassFilter;

    @Autowired
    private SandboxPermissions sandboxPermissions;

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

    public void allow(Class<?> aClass) {
        sandboxClassFilter.add(aClass.getName());
    }

    public void disallow(Class<?> aClass) {
        sandboxClassFilter.add(aClass.getName());
    }

    public void allowAction(SandboxPermissions.Action action) {
        sandboxPermissions.allowAction(action);
    }

    public void disallowAction(SandboxPermissions.Action action) {
        sandboxPermissions.disallowAction(action);
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
