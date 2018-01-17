package sobolee.nashornSandbox.remote;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class ThreadMonitor implements Runnable{
    private Thread monitoredThread;
    private long cpuLimit;
    private long startCpuTime;
    private final ThreadMXBean bean = ManagementFactory.getThreadMXBean();

    private boolean hasStopped;
    private SafeEvaluator safeEvaluator;

    @Override
    public void run() {
        long threadId = monitoredThread.getId();
        long threadTime;

        while(!hasStopped){
            threadTime = bean.getThreadCpuTime(threadId)-startCpuTime;

            if(threadTime > cpuLimit){
                safeEvaluator.notifyDead();
                stopMonitoring();
                monitoredThread.stop();
            }
        }

    }

    public void startMonitoring(){
        hasStopped = false;
        startCpuTime = bean.getThreadCpuTime(monitoredThread.getId());
        new Thread(this).start();
    }

    public void stopMonitoring(){
        hasStopped = true;
    }

    public void setMonitoredThread(Thread monitoredThread) {
        this.monitoredThread = monitoredThread;
    }

    public void setCpuLimit(int cpuLimit) {
        this.cpuLimit = cpuLimit*1000000;
    }

    public void setSubject(SafeEvaluator safeEvaluator) {
        this.safeEvaluator = safeEvaluator;
    }
}
