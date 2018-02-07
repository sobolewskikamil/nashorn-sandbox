package sobolee.nashornSandbox;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class JavaScriptExecutionTest {
    private static Sandbox sandbox;

    @BeforeAll
    static void setUpEnvironment() {
        sandbox = new NashornSandbox.NashornSandboxBuildingFacade()
                .withInactiveTimeout(1)
                .withTimeMeasure()
                .build();
    }

    @Test
    void shouldProperlyEvaluateJavaScript() throws ExecutionException, InterruptedException {
        // given
        String script = "result = \"test\";";

        // when
        CompletableFuture<Object> result = sandbox.evaluate(script, emptyMap());

        // then
        assertThat(result.get()).isEqualTo("test");
    }

    @Test
    void shouldProperlyInvokeFunction() throws ExecutionException, InterruptedException {
        // given
        String script = "" +
                "function f() {" +
                "   return \"test\"; " +
                "}";

        // when
        CompletableFuture<Object> result = sandbox.invokeFunction("f", script, emptyList());

        // then
        assertThat(result.get()).isEqualTo("test");
    }

    @Test
    void shouldAccessJavaClass() throws ExecutionException, InterruptedException {
        // given
        String script = "" +
                "var ArrayList = Java.type(\"java.util.ArrayList\");\n" +
                "var defaultSizeArrayList = new ArrayList;\n" +
                "defaultSizeArrayList";
        ArrayList output = new ArrayList();

        // when
        sandbox.allowClasses(singletonList(java.util.ArrayList.class));
        CompletableFuture<Object> result = sandbox.evaluate(script, emptyMap());

        // then
        assertThat(result.get()).isEqualTo(output);
    }
}
