package sobolee.nashornSandbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ExceptionTest {
    private Sandbox sandbox;

    @BeforeEach
    public void setUpEnvironment() {
        sandbox = new NashornSandbox.NashornSandboxBuilder()
                .withInactiveTimeout(1)
                .build();
    }

    @Test
    public void shouldThrowOutOfMemoryErrorWhenAllocatingMemoryInInfiniteLoop() {
        // given
        String script = "var array = [];" +
                "while(true) {" +
                "    array.push(1);" +
                "}";

        // when / then
        assertThatThrownBy(() -> sandbox.evaluate(script, emptyMap()).get())
                .hasRootCauseInstanceOf(OutOfMemoryError.class);
    }

    @Configuration
    static class ContextConfiguration {

        SandboxClassFilter sandboxClassFilter() {
            return new SandboxClassFilter();
        }

        SandboxPermissions sandboxPermissions() {
            return new SandboxPermissions();
        }
    }
}
