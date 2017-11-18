package sobolee.nashornSandbox.requests;

import java.io.Serializable;
import java.util.Map;

public class EvaluationRequest implements Serializable {
    private final String script;
    private final Map<String, Object> args;

    public EvaluationRequest(String script, Map<String, Object> args) {
        this.script = script;
        this.args = args;
    }

    public String getScript() {
        return script;
    }

    public Map<String, Object> getArgs() {
        return args;
    }
}
