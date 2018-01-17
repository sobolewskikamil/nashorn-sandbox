package sobolee.nashornSandbox;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface Sandbox {

    CompletableFuture<Object> evaluate(String script, Map<String, Object> args);

    CompletableFuture<Object> invokeFunction(String function, String script, List<Object> args);

    void allowClasses(Collection<Class<?>> classes);

    void disallowClasses(Collection<Class<?>> classes);

    void setInactiveTimeout(int seconds);

    void setCpuLimit(int cpuLimit);

    void setMemoryLimit(int memoryLimit);
}
