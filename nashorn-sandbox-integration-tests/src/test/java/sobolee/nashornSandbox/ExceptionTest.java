package sobolee.nashornSandbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;

import java.util.concurrent.ExecutionException;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
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

    /*@Test
    public void shouldThrowExceptionWhenExceededCpuTime(){
        // given
        String script = "while(true) { }\n" +
                "print(\"Script finished\");";

        sandbox = new NashornSandbox.NashornSandboxBuilder()
                .withCpuLimit(500)
                .build();

        try {
            sandbox.evaluate(script, emptyMap()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertThat(true);
    }*/

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
