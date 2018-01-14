package sobolee.nashornSandbox;

import sobolee.nashornSandbox.loadbalancing.LoadBalancer;
import sobolee.nashornSandbox.remote.NashornExecutor;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;

import java.rmi.RemoteException;

public class NashornEvaluator {
    private final LoadBalancer loadBalancer;
    private final RmiManager rmiManager;

    private SandboxClassFilter classFilter;
    private int cpuLimit = 0;

    public NashornEvaluator(int maximumNumberOfInstances, long memoryPerInstance) {
        this(new LoadBalancer(maximumNumberOfInstances, memoryPerInstance), new RmiManager());
    }

    public NashornEvaluator(LoadBalancer loadBalancer, RmiManager rmiManager) {
        this.loadBalancer = loadBalancer;
        this.rmiManager = rmiManager;
    }

    public Object evaluate(ScriptEvaluationRequest evaluationRequest) {
        EvaluationUnit evaluationUnit = loadBalancer.get();
        NashornExecutor executor = getNashornExecutor(evaluationUnit);
        try {
            applyAll(executor);
            Object result = executor.execute(evaluationRequest);
            evaluationUnit.setEvaluating(false);
            return result;
        } catch (RemoteException e) {
            if(e.getCause().getClass() == OutOfMemoryError.class){
                loadBalancer.removeDeadUnit(evaluationUnit);
            }
            throw new RuntimeException(e);
        }
    }

    public Object evaluate(FunctionEvaluationRequest evaluationRequest) {
        EvaluationUnit evaluationUnit = loadBalancer.get();
        NashornExecutor executor = getNashornExecutor(evaluationUnit);
        try {
            applyAll(executor);
            Object result = executor.execute(evaluationRequest);
            evaluationUnit.setEvaluating(false);
            return result;
        } catch (RemoteException e) {
            if(e.getCause().getClass() == OutOfMemoryError.class){
                loadBalancer.removeDeadUnit(evaluationUnit);
            }
            throw new RuntimeException(e);
        }
    }

    private void applyAll(NashornExecutor executor) throws RemoteException{
        if(classFilter != null) {
            executor.applyFilter(classFilter);
        }

        executor.setCpuLimit(cpuLimit);
    }

    public void applyFilter(SandboxClassFilter filter){
        this.classFilter = filter;

        for(EvaluationUnit evaluationUnit : loadBalancer.getAllUnits()){
            NashornExecutor executor = getNashornExecutor(evaluationUnit);
            try {
                executor.applyFilter(filter);
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }
    }

    public void setCpuLimit(int limit){
        this.cpuLimit = limit;

        for(EvaluationUnit evaluationUnit : loadBalancer.getAllUnits()){
            NashornExecutor executor = getNashornExecutor(evaluationUnit);
            try {
                executor.setCpuLimit(limit);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private NashornExecutor getNashornExecutor(EvaluationUnit evaluationUnit) {
        String id = evaluationUnit.getId();
        return rmiManager.getExecutor(id);
    }
}
