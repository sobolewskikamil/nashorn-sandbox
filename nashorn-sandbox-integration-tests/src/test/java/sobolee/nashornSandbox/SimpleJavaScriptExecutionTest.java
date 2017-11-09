package sobolee.nashornSandbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleJavaScriptExecutionTest {
    private Sandbox sandbox;

    @BeforeEach
    public void setUpEnvironment() {
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

}