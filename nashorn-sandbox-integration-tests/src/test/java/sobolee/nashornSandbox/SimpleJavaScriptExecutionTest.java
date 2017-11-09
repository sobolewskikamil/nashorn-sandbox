package sobolee.nashornSandbox;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleJavaScriptExecutionTest {
    private static Sandbox sandbox;

    @BeforeAll
    public static void setUpEnvironment() {
        sandbox = new NashornSandbox.NashornSandboxBuilder()
                .withInactiveTimeout(1)
                .build();
    }

    @Test
    public void shouldProperlyEvaluateJavaScript() {
        // given
        String script = "result = \"test\";";

        // when
        Object result = sandbox.evaluate(script, Map.of());

        // then
        assertThat(result).isEqualTo("test");
    }

    @Test
    public void shouldWorkOnTheSameProcess() {
        // given
        String script = "result = \"test\";";

        // when
        Object result = sandbox.evaluate(script, Map.of());

        // then
        assertThat(result).isEqualTo("test");
    }
}
