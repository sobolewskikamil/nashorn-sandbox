package sobolee.nashornSandbox.requests;

import java.io.Serializable;
import java.util.List;

public class FunctionEvaluationRequest implements Serializable {
    private final String function;
    private final String script;
    private final List<Object> args;

    public FunctionEvaluationRequest(String function, String script, List<Object> args) {
        this.function = function;
        this.script = script;
        this.args = args;
    }

    public String getScript() {
        return script;
    }

    public List<Object> getArgs() {
        return args;
    }

    public String getFunction() {
        return function;
    }
}
