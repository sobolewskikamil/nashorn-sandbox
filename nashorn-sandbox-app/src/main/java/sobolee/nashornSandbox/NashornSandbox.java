package sobolee.nashornSandbox;

import org.springframework.beans.factory.annotation.Autowired;
import sobolee.nashornSandbox.requests.EvaluationRequest;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;

import java.util.Map;

public class NashornSandbox implements Sandbox {
    private long memoryLimit = 200;
    private final LoadBalancer loadBalancer = new LoadBalancer(1, memoryLimit);

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
    public Object evaluate(final String script, final Map<String, Object> args) {
        EvaluationRequest evaluationRequest = new EvaluationRequest(script, args);
        return loadBalancer.evaluate(evaluationRequest);
    }

    @Override
    public Object invokeFunction(String function, String script, Map<String, Object> args) {
        EvaluationRequest evaluationRequest = new FunctionEvaluationRequest(function, script, args);
        return loadBalancer.evaluate(evaluationRequest);
    }


    public void allow(final Class<?> aClass) {
        sandboxClassFilter.add(aClass.getName());
    }

    public void disallow(final Class<?> aClass) {
        sandboxClassFilter.add(aClass.getName());
    }

    public void allowAction(final sobolee.nashornSandbox.SandboxPermissions.Action action) {
        sandboxPermissions.allowAction(action);
    }

    public void disallowAction(final sobolee.nashornSandbox.SandboxPermissions.Action action) {
        sandboxPermissions.disallowAction(action);
    }

    private void setInactiveTimeout(final int seconds) {
        JvmInstance.setPossibleInactivityTime(seconds);
    }

    private void setMemoryLimit(final long memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public static class NashornSandboxBuilder implements SandboxBuilder {
        private final NashornSandbox sandbox = new NashornSandbox();

        public SandboxBuilder withMemoryLimit(final long memoryLimit) {
            sandbox.setMemoryLimit(memoryLimit);
            return this;
        }

        public SandboxBuilder withInactiveTimeout(final int seconds) {
            sandbox.setInactiveTimeout(seconds);
            return this;
        }

        @Override
        public Sandbox build() {
            return sandbox;
        }
    }
}
