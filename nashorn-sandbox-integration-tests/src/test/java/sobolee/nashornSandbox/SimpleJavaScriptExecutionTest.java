package sobolee.nashornSandbox;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
class SimpleJavaScriptExecutionTest {
    private static Sandbox sandbox;

    @BeforeAll
    public static void setUpEnvironment() {
        sandbox = new NashornSandbox.NashornSandboxBuilder()
                .withInactiveTimeout(1)
                .build();
    }

    @Test
    public void shouldProperlyEvaluateJavaScript() throws ExecutionException, InterruptedException {
        // given
        String script = "result = \"test\";";

        // when
        CompletableFuture<Object> result = sandbox.evaluate(script, emptyMap());

        // then
        assertThat(result.get()).isEqualTo("test");
    }

    @Test
    public void shouldProperlyInvokeFunction() throws ExecutionException, InterruptedException {
        // given
        String script = "function f() {" +
                "   return \"test\"; " +
                "}";

        // when
        CompletableFuture<Object> result = sandbox.invokeFunction("f", script, emptyList());

        // then
        assertThat(result.get()).isEqualTo("test");
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
