package sobolee.nashornSandbox.loadbalancing;

import sobolee.nashornSandbox.EvaluationUnit;
import sobolee.nashornSandbox.JvmManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoadBalancer implements Observer{
    private final JvmManager jvmManager;
    private final int numberOfInstances;
    private Queue<Thread> threadQueue = new LinkedList<Thread>();
    private final Lock LOCK = new ReentrantLock();

    public LoadBalancer(int numberOfInstances, long memoryPerInstance) {
        this(new JvmManager(memoryPerInstance), numberOfInstances);
    }

    public LoadBalancer(JvmManager jvmManager, int numberOfInstances) {
        this.jvmManager = jvmManager;
        this.numberOfInstances = numberOfInstances;

        if(numberOfInstances > 0) {
            jvmManager.start(this);
        }
    }

    public EvaluationUnit get() {
        List<EvaluationUnit> evaluationUnits = jvmManager.getEvaluationUnits();
        if (evaluationUnits.size() == numberOfInstances) {
            return waitForAvailableEvaluationUnit(evaluationUnits);
        }
        synchronized (LOCK) {
            for (EvaluationUnit evaluationUnit : evaluationUnits) {
                if (!evaluationUnit.isEvaluating()) {
                    evaluationUnit.setEvaluating(true);
                    return evaluationUnit;
                }
            }
        }
        return jvmManager.start(this);
    }

    public List<EvaluationUnit> getAllUnits(){
        return jvmManager.getEvaluationUnits();
    }

    private EvaluationUnit waitForAvailableEvaluationUnit(List<EvaluationUnit> evaluationUnits) {
        while (true) {
            synchronized (LOCK) {
                for (EvaluationUnit evaluationUnit : evaluationUnits) {
                    if (!evaluationUnit.isEvaluating()) {
                        evaluationUnit.setEvaluating(true);
                        threadQueue.remove(Thread.currentThread());
                        return evaluationUnit;
                    }
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
        synchronized (LOCK) {
            Thread thread = threadQueue.peek();
            if (thread != null) {
                thread.notify();
            }
        }
    }
}
