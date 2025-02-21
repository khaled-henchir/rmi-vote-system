import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface ResultInterface extends Remote {
    Map<String, Integer> getResult() throws RemoteException;
}