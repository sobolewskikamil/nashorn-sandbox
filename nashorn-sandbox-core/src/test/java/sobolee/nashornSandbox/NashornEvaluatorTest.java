package sobolee.nashornSandbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sobolee.nashornSandbox.loadbalancing.LoadBalancer;
import sobolee.nashornSandbox.remote.NashornExecutorImpl;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;

import java.rmi.RemoteException;
import java.util.List;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NashornEvaluatorTest {
    private NashornEvaluator nashornEvaluator;

    @BeforeEach
    public void setUp() throws RemoteException {
        LoadBalancer balancerMock = mock(LoadBalancer.class);
        when(balancerMock.get()).thenReturn(new EvaluationUnit("test", null));

        RmiManager rmiManagerMock = mock(RmiManager.class);
        when(rmiManagerMock.getExecutor("test")).thenReturn(new NashornExecutorImpl());

        nashornEvaluator = new NashornEvaluator(balancerMock, rmiManagerMock);
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
}
