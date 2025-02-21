import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthInterface extends Remote {
    boolean register(String username, String password) throws RemoteException;
    boolean login(String username, String password) throws RemoteException;
    boolean hasUserVoted() throws RemoteException;
    boolean markUserAsVoted() throws RemoteException;
	String getCurrentUser() throws RemoteException;
    void logout() throws RemoteException;
}
