package sobolee.nashornSandbox;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SandboxPermissions {
    private final Set<Action> allowedActions = new HashSet<>();

    public void allowAction(Action action) {
        allowedActions.add(action);
    }

    public void disallowAction(Action action) {
        allowedActions.remove(action);
    }

    public boolean isAllowed(Action action) {
        return allowedActions.contains(action);
    }

    public enum Action {
        PRINT_FUNCTIONS,
        READ_FUNCTIONS,
        LOAD_FUNCTIONS,
        EXIT_FUNCTIONS,
        GLOBALS_OBJECTS
    }
}
