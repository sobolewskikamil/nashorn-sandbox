package sobolee.nashornSandbox;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface Sandbox {

    CompletableFuture<Object> evaluate(String script, Map<String, Object> args);

    CompletableFuture<Object> invokeFunction(String function, String script, List<Object> args);

    void allowClass(Class<?> aClass);

    void disallowClass(Class<?> aClass);

    void allowAction(SandboxPermissions.Action action);

    void disallowAction(SandboxPermissions.Action action);

    interface SandboxBuilder {

        Sandbox build();
    }
}
