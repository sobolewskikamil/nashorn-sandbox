package sobolee.nashornSandbox.loadbalancing;

import sobolee.nashornSandbox.EvaluationUnit;
import sobolee.nashornSandbox.JvmManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LoadBalancer implements Observer{
    private final JvmManager jvmManager;
    private final int numberOfInstances;
    private Queue<Thread> threadQueue = new LinkedList<Thread>();

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
                evaluationUnit.setEvaluating(true);
                return evaluationUnit;
            }
        }
        return jvmManager.start(this);
    }

    private EvaluationUnit waitForAvailableEvaluationUnit(List<EvaluationUnit> evaluationUnits) {
        while (true) {
            for (EvaluationUnit evaluationUnit : evaluationUnits) {
                if (!evaluationUnit.isEvaluating()) {
                    evaluationUnit.setEvaluating(true);
                    return evaluationUnit;
                }
            }
            try {
                threadQueue.add(Thread.currentThread());
                Thread.currentThread().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void notifyFreeJvm() {
        Thread thread = threadQueue.poll();
        if(thread != null){
            thread.notify();
        }
    }
}
