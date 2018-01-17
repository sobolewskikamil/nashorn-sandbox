package sobolee.nashornSandbox;

import java.util.List;

public interface SandboxBuilder {

    SandboxBuilder withInactiveTimeout(int seconds);

    SandboxBuilder withMemoryLimit(int memoryLimit);

    SandboxBuilder withCpuLimit(int cpuLimit);

    SandboxBuilder withAllowedClasses(List<Class<?>> classes);

    SandboxBuilder withDisallowedClasses(List<Class<?>> classes);

    Sandbox build();
}
