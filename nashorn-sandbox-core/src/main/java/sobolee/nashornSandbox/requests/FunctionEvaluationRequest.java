package sobolee.nashornSandbox.requests;

import java.util.Map;

public class FunctionEvaluationRequest extends EvaluationRequest {
    private final String function;

    public FunctionEvaluationRequest(String function, String script, Map<String, Object> args) {
        super(script, args);
        this.function = function;
    }

    public String getFunction() {
        return function;
    }
}
