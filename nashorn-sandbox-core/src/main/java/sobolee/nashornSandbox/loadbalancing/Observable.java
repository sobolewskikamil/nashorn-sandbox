package sobolee.nashornSandbox.loadbalancing;

public abstract class Observable {
    private Observer observer;

    public abstract boolean isEvaluating();
    public abstract void setEvaluating(boolean evaluating);

    public void registerObserver(Observer obj){
        observer = obj;
    }
    public void notifyObserver(){
        observer.notifyFreeJvm();
    }

}
