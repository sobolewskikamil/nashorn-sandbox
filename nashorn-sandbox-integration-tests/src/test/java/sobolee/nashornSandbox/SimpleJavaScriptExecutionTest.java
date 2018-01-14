package sobolee.nashornSandbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

class SimpleJavaScriptExecutionTest {
    private Sandbox sandbox;

    @BeforeEach
    public void setUpEnvironment() {
        sandbox = new NashornSandbox.NashornSandboxBuilder()
                .createNew()
                .withInactiveTimeout(1)
                .get();
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

    @Test
    public void shouldAccessJavaClass() throws ExecutionException, InterruptedException {
        // given
        String script = "var ArrayList = Java.type(\"java.util.ArrayList\");\n" +
                "var defaultSizeArrayList = new ArrayList;\n" +
                "defaultSizeArrayList";
        ArrayList output = new ArrayList();

        // when
        sandbox.allowClass(java.util.ArrayList.class);
        CompletableFuture<Object> result = sandbox.evaluate(script, emptyMap());

        // then
        assertThat(result.get()).isEqualTo(output);
    }

}
