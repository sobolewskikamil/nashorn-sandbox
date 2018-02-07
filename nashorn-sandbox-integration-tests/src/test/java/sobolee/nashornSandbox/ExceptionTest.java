package sobolee.nashornSandbox;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sobolee.nashornSandbox.exceptions.CpuTimeAbuseException;

import java.util.concurrent.ExecutionException;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ExceptionTest {
    private static Sandbox sandbox;

    @BeforeAll
    static void setUpEnvironment() {
        sandbox = new NashornSandbox.NashornSandboxBuildingFacade()
                .withInactiveTimeout(1)
                .withMemoryLimit(500)
                .build();
    }

    @Test
    void shouldThrowOutOfMemoryErrorWhenAllocatingMemoryInInfiniteLoop() throws InterruptedException, ExecutionException{
        // given
        String script = "var array = [];" +
                "while(true) {" +
                "    array.push(1);" +
                "}";

        // when / then
        assertThatThrownBy(() -> sandbox.evaluate(script, emptyMap()).get())
                .hasRootCauseInstanceOf(OutOfMemoryError.class);
    }

    @Test
    void shouldThrowExceptionWhenExceededCpuTime() throws InterruptedException, ExecutionException {
        // given
        String script = "while(true) { }\n" +
                "print(\"Script finished\");";

        // when
        sandbox.setCpuLimit(500);

        // then
        assertThatThrownBy(() -> sandbox.evaluate(script, emptyMap()).get())
                .hasRootCauseInstanceOf(CpuTimeAbuseException.class);
    }

    @Test
    void shouldThrowExceptionWhenUsingDisabledClass() {
        // given
        String script = "var ArrayList = Java.type(\"java.util.ArrayList\");\n" +
                "var defaultSizeArrayList = new ArrayList;\n" +
                "defaultSizeArrayList";

        // when / then
        assertThatThrownBy(() -> sandbox.evaluate(script, emptyMap()).get())
                .hasRootCauseInstanceOf(ClassNotFoundException.class);
    }
}
