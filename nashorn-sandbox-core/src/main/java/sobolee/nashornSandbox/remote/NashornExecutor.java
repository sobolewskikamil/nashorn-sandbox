package sobolee.nashornSandbox.remote;

import org.joda.time.DateTime;
import sobolee.nashornSandbox.SandboxClassFilter;
import sobolee.nashornSandbox.SandboxPermissions;
import sobolee.nashornSandbox.requests.FunctionEvaluationRequest;
import sobolee.nashornSandbox.requests.ScriptEvaluationRequest;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NashornExecutor extends Remote {

    Object execute(ScriptEvaluationRequest evaluationRequest) throws RemoteException;

    Object execute(FunctionEvaluationRequest evaluationRequest) throws RemoteException;

    DateTime getTimeOfLastRequest() throws RemoteException;

    void applyFilter(SandboxClassFilter filter) throws RemoteException;

    void applyPermissions(SandboxPermissions permissions) throws RemoteException;

    void setCpuLimit(int limit) throws RemoteException;

}
