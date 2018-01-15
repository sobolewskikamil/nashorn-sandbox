package sobolee.nashornSandbox;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface Sandbox {

    CompletableFuture<Object> evaluate(String script, Map<String, Object> args);

    CompletableFuture<Object> invokeFunction(String function, String script, List<Object> args);

    void allowClass(Class<?> aClass);

    void disallowClass(Class<?> aClass);

    void setInactiveTimeout(int seconds);

    void setCpuLimit(int cpuLimit);

    interface SandboxBuilder {

        Sandbox get();
        SandboxBuilder createNew();
        SandboxBuilder withInactiveTimeout(int seconds);
        SandboxBuilder withMemoryLimit(int memoryLimit);
        SandboxBuilder withCpuLimit(int cpuLimit);
        SandboxBuilder withTimeMeasure();
    }
}
