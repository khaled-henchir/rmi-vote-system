import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface VoteInterface extends Remote {
    Map<String, Integer> candidatlist() throws RemoteException;
    boolean SubmitVote(String username,String candidate) throws RemoteException;
}
