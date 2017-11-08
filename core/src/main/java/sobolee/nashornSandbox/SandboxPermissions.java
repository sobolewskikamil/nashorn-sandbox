package sobolee.nashornSandbox;

import java.util.HashSet;
import java.util.Set;

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
