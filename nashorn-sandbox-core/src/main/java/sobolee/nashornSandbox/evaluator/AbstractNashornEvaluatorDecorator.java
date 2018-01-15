package sobolee.nashornSandbox.evaluator;

public abstract class AbstractNashornEvaluatorDecorator implements NashornEvaluator {
    protected NashornEvaluator evaluator;

    public AbstractNashornEvaluatorDecorator(NashornEvaluator evaluator){
        this.evaluator = evaluator;
    }
}
