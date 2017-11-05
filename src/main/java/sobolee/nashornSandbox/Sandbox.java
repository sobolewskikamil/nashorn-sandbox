package sobolee.nashornSandbox;

import java.util.Map;

public interface Sandbox {

    Object evaluate(String script, Map<String, Object> args);

    interface SandboxBuilder {

        Sandbox build();
    }
}
