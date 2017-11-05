package sobolee.nashornSandbox;

public class EvaluationUnit {

    private final String id;
    private final Process process;

    public EvaluationUnit(String id, Process process) {
        this.id = id;
        this.process = process;
    }

    public String getId() {
        return id;
    }

    public void stop() {
        process.destroy();
    }
}
