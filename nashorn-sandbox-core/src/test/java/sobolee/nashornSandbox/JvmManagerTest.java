package sobolee.nashornSandbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

class JvmManagerTest {
    private JvmManager jvmManager;

    @BeforeEach
    public void setUp() {
        jvmManager = new JvmManager(50);
    }

    @Test
    public void shouldProperlyStartProcess() {
        // when
        jvmManager.start();

        // then
        List<EvaluationUnit> evaluationUnits = jvmManager.getEvaluationUnits();
        assertThat(evaluationUnits).hasSize(1);

        EvaluationUnit evaluationUnit = evaluationUnits.get(0);
        assertThat(evaluationUnit.isEvaluating()).isFalse();

        Process process = evaluationUnit.getProcess();
        assertThat(process.isAlive()).isTrue();
        String commandLine = process.info().commandLine().get();
        assertThat(commandLine).contains(format("-Xmx%sm", 50));
        assertThat(commandLine).contains(evaluationUnit.getId());
    }
}
