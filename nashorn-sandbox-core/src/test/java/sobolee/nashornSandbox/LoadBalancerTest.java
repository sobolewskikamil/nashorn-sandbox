package sobolee.nashornSandbox;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoadBalancerTest {

    private LoadBalancer getLoadBalancer(List<EvaluationUnit> evaluationUnits, int numberOfInstances) {
        JvmManager jvmManagerMock = mock(JvmManager.class);
        when(jvmManagerMock.getEvaluationUnits()).thenReturn(evaluationUnits);
        return new LoadBalancer(jvmManagerMock, numberOfInstances);
    }

    @Test
    public void shouldReturnFirstNotEvaluatingEvaluationUnit() {
        // given
        EvaluationUnit evalUnit = new EvaluationUnit("id", null, false);
        LoadBalancer loadBalancer = getLoadBalancer(singletonList(evalUnit), 1);

        // when
        EvaluationUnit actual = loadBalancer.get();

        // then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(evalUnit);
    }
}
