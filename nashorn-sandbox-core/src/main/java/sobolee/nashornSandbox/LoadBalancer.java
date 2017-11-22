package sobolee.nashornSandbox;

import java.util.List;

public class LoadBalancer {
    private final JvmManager jvmManager;
    private final int numberOfInstances;

    public LoadBalancer(int numberOfInstances, long memoryPerInstance) {
        this(new JvmManager(memoryPerInstance), numberOfInstances);
    }

    public LoadBalancer(JvmManager jvmManager, int numberOfInstances) {
        this.jvmManager = jvmManager;
        this.numberOfInstances = numberOfInstances;
    }

    public EvaluationUnit get() {
        List<EvaluationUnit> evaluationUnits = jvmManager.getEvaluationUnits();
        if (evaluationUnits.size() == numberOfInstances) {
            return waitForAvailableEvaluationUnit(evaluationUnits);
        }
        for (EvaluationUnit evaluationUnit : evaluationUnits) {
            if (!evaluationUnit.isEvaluating()) {
                return evaluationUnit;
            }
        }
        return jvmManager.start();
    }

    private EvaluationUnit waitForAvailableEvaluationUnit(List<EvaluationUnit> evaluationUnits) {
        while (true) {
            for (EvaluationUnit evaluationUnit : evaluationUnits) {
                if (!evaluationUnit.isEvaluating()) {
                    return evaluationUnit;
                }
            }
        }
    }
}
