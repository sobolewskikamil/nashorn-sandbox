package sobolee.nashornSandbox;

import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;

import java.rmi.RemoteException;

public class NashornEvaluator {
    private final LoadBalancer loadBalancer;
    private final RmiManager rmiManager;

    public NashornEvaluator(int maximumNumberOfInstances, long memoryPerInstance) {
        this(new LoadBalancer(maximumNumberOfInstances, memoryPerInstance), new RmiManager());
    }

    public NashornEvaluator(LoadBalancer loadBalancer, RmiManager rmiManager) {
        this.loadBalancer = loadBalancer;
        this.rmiManager = rmiManager;
    }

    public Object evaluate(ScriptEvaluationRequest evaluationRequest) {
        NashornExecutor executor = getNashornExecutor();
        try {
            return executor.execute(evaluationRequest);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Object evaluate(FunctionEvaluationRequest evaluationRequest) {
        NashornExecutor executor = getNashornExecutor();
        try {
            return executor.execute(evaluationRequest);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private NashornExecutor getNashornExecutor() {
        EvaluationUnit evaluationUnit = loadBalancer.get();
        String id = evaluationUnit.getId();
        return rmiManager.getExecutor(id);
    }
}
