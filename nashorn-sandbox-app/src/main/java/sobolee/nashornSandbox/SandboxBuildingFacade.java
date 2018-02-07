package sobolee.nashornSandbox;

import java.util.List;

public interface SandboxBuildingFacade {

    SandboxBuildingFacade withInactiveTimeout(int seconds);

    SandboxBuildingFacade withMemoryLimit(int memoryLimit);

    SandboxBuildingFacade withCpuLimit(int cpuLimit);

    SandboxBuildingFacade withAllowedClasses(List<Class<?>> classes);

    SandboxBuildingFacade withDisallowedClasses(List<Class<?>> classes);

    SandboxBuildingFacade withTimeMeasure();

    SandboxBuildingFacade withMaxNumberOfInstances(int number);

    Sandbox build();
}
