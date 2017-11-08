package sobolee.nashornSandbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ExceptionTest {

    private Sandbox sandbox;

    @BeforeEach
    public void setUpEnvironment() {
        sandbox = new NashornSandbox.NashornSandboxBuilder().build();
    }

    @Test
    public void shouldThrowOutOfMemoryErrorWhenAllocatingMemoryInInfiniteLoop() {
        // given
        String script = "var array = [];" +
                "while(true) {" +
                "array.push(1);" +
                "}";

        //when / then
        assertThatThrownBy(() -> sandbox.evaluate(script, emptyMap()))
                .hasRootCauseInstanceOf(OutOfMemoryError.class);
    }
}
