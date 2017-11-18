package sobolee.nashornSandbox;

public class EvaluationUnit {
    private final String id;
    private final Process process;
    private boolean evaluating;

    public EvaluationUnit(String id, Process process) {
        this.id = id;
        this.process = process;
    }

    public EvaluationUnit(String id, Process process, boolean evaluating) {
        this(id, process);
        this.evaluating = evaluating;
    }

    public String getId() {
        return id;
    }

    public Process getProcess() {
        return process;
    }

    public boolean isEvaluating() {
        return evaluating;
    }

    public void setEvaluating(boolean evaluating) {
        this.evaluating = evaluating;
    }
}
