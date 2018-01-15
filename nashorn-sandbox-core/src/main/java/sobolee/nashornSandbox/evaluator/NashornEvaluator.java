package sobolee.nashornSandbox.evaluator;

import sobolee.nashornSandbox.SandboxClassFilter;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;

public interface NashornEvaluator {
    Object evaluate(ScriptEvaluationRequest evaluationRequest);
    Object evaluate(FunctionEvaluationRequest evaluationRequest);
    void applyFilter(SandboxClassFilter filter);
    void setCpuLimit(int limit);
}
