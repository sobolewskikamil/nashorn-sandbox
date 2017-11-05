package sobolee.nashornSandbox;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NashornSandboxTest {

    @Test
    public void shouldProperlyEvaluateJavaScript() {
        // given
        String script = "zmienna = \"test\";";
        Sandbox sandbox = new NashornSandbox.NashornSandboxBuilder().build();

        // when
        Object result = sandbox.evaluate(script, Map.of());

        // then
        assertThat(result).isEqualTo("test");
    }

}