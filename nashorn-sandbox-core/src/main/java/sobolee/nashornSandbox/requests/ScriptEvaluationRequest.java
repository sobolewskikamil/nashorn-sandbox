package sobolee.nashornSandbox.requests;

import java.io.Serializable;
import java.util.Map;

public class ScriptEvaluationRequest implements EvaluationRequest, Serializable {
    private final String script;
    private final Map<String, Object> args;

    public ScriptEvaluationRequest(String script, Map<String, Object> args) {
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
