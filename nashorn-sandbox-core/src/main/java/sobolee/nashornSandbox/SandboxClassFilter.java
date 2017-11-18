package sobolee.nashornSandbox;

import jdk.nashorn.api.scripting.ClassFilter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SandboxClassFilter implements ClassFilter {
    private final Set<String> allowedClasses = new HashSet<>();

    @Override
    public boolean exposeToScripts(String className) {
        return allowedClasses.contains(className);
    }

    public void add(String className) {
        allowedClasses.add(className);
    }

    public void remove(String className) {
        allowedClasses.remove(className);
    }
}
