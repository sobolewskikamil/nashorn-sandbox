package sobolee.nashornSandbox;

import java.util.Map;

public class NashornSandbox implements Sandbox {

    private long memoryLimit = 200;
    private final SandboxClassFilter sandboxClassFilter = new SandboxClassFilter();
    private final SandboxPermissions sandboxPermissions = new SandboxPermissions();
    private final LoadBalancer loadBalancer = new LoadBalancer(1, memoryLimit);

    private NashornSandbox() {
    }

    @Override
    public Object evaluate(final String script, final Map<String, Object> args) {
        Object result = loadBalancer.evaluate(script, args);
        loadBalancer.stop();
        return result;
    }

    @Override
    public Object invokeFunction(String script, Map<String, Object> args) {
        return null;
    }

    @Override
    public void finalize() {

    }

    public void allow(final Class<?> aClass) {
        sandboxClassFilter.add(aClass.getName());
    }

    public void disallow(final Class<?> aClass) {
        sandboxClassFilter.add(aClass.getName());
    }

    public void allowAction(final SandboxPermissions.Action action) {
        sandboxPermissions.allowAction(action);
    }

    public void disallowAction(final SandboxPermissions.Action action) {
        sandboxPermissions.disallowAction(action);
    }

    private void setMemoryLimit(final long memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public static class NashornSandboxBuilder implements SandboxBuilder {

        private NashornSandbox sandbox = new NashornSandbox();

        public SandboxBuilder withMemoryLimit(final long memoryLimit) {
            sandbox.setMemoryLimit(memoryLimit);
            return this;
        }

        @Override
        public Sandbox build() {
            sandbox.loadBalancer.start();
            return sandbox;
        }
    }
}
