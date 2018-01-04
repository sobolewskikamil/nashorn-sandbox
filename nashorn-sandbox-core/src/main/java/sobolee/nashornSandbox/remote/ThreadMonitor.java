package sobolee.nashornSandbox.remote;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadMonitor implements Runnable{
    private static ThreadMonitor threadMonitor;
    private static final Lock LOCK = new ReentrantLock();
    private Thread monitoredThread;
    private long cpuLimit;
    private long startCpuTime;
    private final ThreadMXBean bean = ManagementFactory.getThreadMXBean();

    private boolean hasStopped;
    private SafeEvaluator safeEvaluator;


    public static ThreadMonitor get(){
        synchronized (LOCK) {
            if (threadMonitor == null) {
                threadMonitor = new ThreadMonitor();
            }
        }
        return threadMonitor;
    }


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
        new Thread(threadMonitor).start();
    }

    public void stopMonitoring(){
        hasStopped = true;
    }

    public void setMonitoredThread(Thread monitoredThread) {
        synchronized (LOCK) {
            this.monitoredThread = monitoredThread;
        }
    }

    public void setCpuLimit(int cpuLimit) {
        this.cpuLimit = cpuLimit*1000000;
    }

    public void setSubject(SafeEvaluator safeEvaluator) {
        this.safeEvaluator = safeEvaluator;
    }
}
