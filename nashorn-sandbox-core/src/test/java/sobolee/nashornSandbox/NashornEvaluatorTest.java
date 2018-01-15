package sobolee.nashornSandbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sobolee.nashornSandbox.evaluator.SimpleNashornEvaluator;
import sobolee.nashornSandbox.exceptions.CpuTimeAbuseException;
import sobolee.nashornSandbox.exceptions.JavaClassAccessException;
import sobolee.nashornSandbox.loadbalancing.LoadBalancer;
import sobolee.nashornSandbox.remote.NashornExecutorImpl;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NashornEvaluatorTest {
    private SimpleNashornEvaluator nashornEvaluator;

    @BeforeEach
    public void setUp() throws RemoteException {
        LoadBalancer balancerMock = mock(LoadBalancer.class);
        EvaluationUnit evaluationUnit = new EvaluationUnit("test", null, false, balancerMock);
        when(balancerMock.get()).thenReturn(evaluationUnit);
        List<EvaluationUnit> evaluationUnits = new ArrayList<>();
        evaluationUnits.add(evaluationUnit);
        when(balancerMock.getAllUnits()).thenReturn(evaluationUnits);

        RmiManager rmiManagerMock = mock(RmiManager.class);
        when(rmiManagerMock.getExecutor("test")).thenReturn(new NashornExecutorImpl());

        nashornEvaluator = new SimpleNashornEvaluator(balancerMock, rmiManagerMock);
    }

    @Test
    public void shouldEvaluateScript() throws RemoteException {
        // given
        String script = "\"test\";";
        ScriptEvaluationRequest evaluationRequest = new ScriptEvaluationRequest(script, emptyMap());

        // when
        Object result = nashornEvaluator.evaluate(evaluationRequest);

        // then
        assertThat(result).isEqualTo("test");
    }

    @Test
    public void shouldEvaluateFunction() throws RemoteException {
        // given
        String script = "function f(a, b) {" +
                "   return a + b; " +
                "}";
        List<Object> args = List.of(1.0, 2.0);
        FunctionEvaluationRequest evaluationRequest = new FunctionEvaluationRequest("f", script, args);

        // when
        Object result = nashornEvaluator.evaluate(evaluationRequest);

        // then
        assertThat(result).isEqualTo(3.0);
    }

    @Test
    public void shouldThrowExceptionWhenUsingDisabledClass(){
        // given
        String script = "var MyJavaClass = Java.type('sobolee.nashornSandbox.sobolee.nashornSandbox.evaluator.NashornEvaluator');\n" +
                "var result = MyJavaClass.evaluate('test');";
        ScriptEvaluationRequest evaluationRequest = new ScriptEvaluationRequest(script, emptyMap());
        SandboxClassFilter filter = new SandboxClassFilter();

        // when
        nashornEvaluator.applyFilter(filter);

        // then
        assertThatThrownBy(() -> nashornEvaluator.evaluate(evaluationRequest))
                .hasRootCauseInstanceOf(ClassNotFoundException.class);
    }

    @Test
    public void shouldThrowExceptionWhenExceededCpuTimeOnScript(){
        // given
        String script = "while(true) { }\n" +
                "print(\"Script finished\");";
        ScriptEvaluationRequest evaluationRequest = new ScriptEvaluationRequest(script, emptyMap());

        // when
        nashornEvaluator.setCpuLimit(500);

        // then
        assertThatThrownBy(() -> nashornEvaluator.evaluate(evaluationRequest))
                .hasRootCauseInstanceOf(CpuTimeAbuseException.class);
    }

    @Test
    public void shouldThrowExceptionWhenExceededCpuTimeOnFunction() throws RemoteException {
        // given
        String script = "function f(a, b){\n" +
                "\twhile(true) { }\n" +
                "\treturn a + b;\n" +
                "}";
        List<Object> args = List.of(1.0, 2.0);
        FunctionEvaluationRequest evaluationRequest = new FunctionEvaluationRequest("f", script, args);

        // when
        nashornEvaluator.setCpuLimit(500);

        // then
        assertThatThrownBy(() -> nashornEvaluator.evaluate(evaluationRequest))
                .hasRootCauseInstanceOf(CpuTimeAbuseException.class);
    }
}
