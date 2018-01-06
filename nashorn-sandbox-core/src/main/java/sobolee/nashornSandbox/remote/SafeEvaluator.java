package sobolee.nashornSandbox.remote;

import sobolee.nashornSandbox.exceptions.CpuTimeAbuseException;
import sobolee.nashornSandbox.requests.EvaluationRequest;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;

import javax.script.*;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SafeEvaluator implements Runnable, Thread.UncaughtExceptionHandler{

    private final ScriptEngine engine;
    private final EvaluationRequest request;
    private final int cpuLimit;
    private BlockingQueue<Object> result = new ArrayBlockingQueue<Object>(1);
    private ThreadMonitor threadMonitor;

    public SafeEvaluator(ScriptEngine engine, EvaluationRequest request, int cpuLimit){
        this.engine = engine;
        this.request = request;
        this.cpuLimit = cpuLimit;
    }

    public Object getResult() throws RemoteException {
        Object r = null;
        try {
            r = result.take();
        } catch (InterruptedException ignored) {}

        if(r instanceof Throwable){
            Throwable e = (Throwable)r;
            throw new RemoteException(e.getMessage(), e);
        }

        return r;
    }

    public void evaluate(){
        Thread thread = new Thread(this);
        threadMonitor = ThreadMonitor.get();
        threadMonitor.setMonitoredThread(thread);
        threadMonitor.setCpuLimit(cpuLimit);
        threadMonitor.setSubject(this);
        thread.setUncaughtExceptionHandler(this);
        thread.start();
    }

    public void notifyDead(){
        Exception e = new CpuTimeAbuseException(String.format("CPU time exceeded: %d ms", cpuLimit));
        result.add(e);
    }

    /**
     * Uncaught exception handler is set to catch any exceptions that are not thrown by specific code,
     * but by the JVM e.g. OutOfMemoryError
     *
     * @param t Thread where the exception was thrown
     * @param e Thrown exception
     */

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        result.add(e);
    }

    /*
     All the methods below are executed on external, safe-to-kill thread
     */

    @Override
    public void run() {
        try {
            Object object = null;
            if(request instanceof FunctionEvaluationRequest){
                object = execute((FunctionEvaluationRequest)request);
            }
            else if(request instanceof ScriptEvaluationRequest){
                object = execute((ScriptEvaluationRequest)request);
            }
            result.add(object);
        } catch (Exception e) {
            result.add(e);
        }
    }

    public Object execute(FunctionEvaluationRequest evaluationRequest) throws ScriptException, NoSuchMethodException {
        String function = evaluationRequest.getFunction();
        String script = evaluationRequest.getScript();
        List<Object> args = evaluationRequest.getArgs();

        Object result;
        Invocable invocable = (Invocable) engine;

        if(cpuLimit > 0) {
            threadMonitor.startMonitoring();
        }
        engine.eval(script);
        result = invocable.invokeFunction(function, args.toArray());
        if(cpuLimit > 0) {
            threadMonitor.stopMonitoring();
        }
        return result;
    }

    public Object execute(ScriptEvaluationRequest evaluationRequest) throws ScriptException{
        String script = evaluationRequest.getScript();
        Map<String, Object> args = evaluationRequest.getArgs();
        Bindings bindings = new SimpleBindings();
        bindings.putAll(args);

        Object result;
        if(cpuLimit > 0) {
            threadMonitor.startMonitoring();
        }
        result = engine.eval(script, bindings);
        if(cpuLimit > 0) {
            threadMonitor.stopMonitoring();
        }
        return result;
    }

}
