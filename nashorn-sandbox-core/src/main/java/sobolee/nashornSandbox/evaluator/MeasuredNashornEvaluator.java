package sobolee.nashornSandbox.evaluator;

import sobolee.nashornSandbox.SandboxClassFilter;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.net.MalformedURLException;

import static java.lang.String.format;

public class MeasuredNashornEvaluator extends AbstractNashornEvaluatorDecorator {
    private Logger logger = LogManager.getLogger();

    public MeasuredNashornEvaluator(NashornEvaluator evaluator){
        super(evaluator);
    }

    @Override
    public Object evaluate(ScriptEvaluationRequest evaluationRequest) {
        long start = System.currentTimeMillis();
        Object result = evaluator.evaluate(evaluationRequest);
        long stop = System.currentTimeMillis();
        logger.info(format("Evaluation time: %d ms", stop-start));
        return result;
    }

    @Override
    public Object evaluate(FunctionEvaluationRequest evaluationRequest) {
        long start = System.currentTimeMillis();
        Object result = evaluator.evaluate(evaluationRequest);
        long stop = System.currentTimeMillis();
        logger.info(format("Evaluation time: %d ms", stop-start));
        return result;
    }

    @Override
    public void applyFilter(SandboxClassFilter filter) {
        evaluator.applyFilter(filter);
    }

    @Override
    public void setCpuLimit(int limit) {
        evaluator.setCpuLimit(limit);
    }
}
