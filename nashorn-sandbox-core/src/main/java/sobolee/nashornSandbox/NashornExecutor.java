package sobolee.nashornSandbox;

import org.joda.time.DateTime;
import sobolee.nashornSandbox.requests.EvaluationRequest;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NashornExecutor extends Remote {

    Object execute(EvaluationRequest evaluationRequest) throws RemoteException;

    Object execute(FunctionEvaluationRequest evaluationRequest) throws RemoteException;

    DateTime getTimeOfLastRequest() throws RemoteException;
}
