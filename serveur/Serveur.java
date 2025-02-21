import java.rmi.*;
import java.rmi.registry.*;
import java.sql.*;

public class Serveur {
    public static void main(String[] args) {
        try {
            // Initialize the RMI registry
            LocateRegistry.createRegistry(1090); 
            System.out.println("Serveur : Construction de l’implémentation");

            // Establish the database connection
            Connection connection = DriverManager.getConnection("DATABASE_URL", "DATABASE_USERNAME", "DATABASE_PASSWORD");
			System.out.println("Connected to the MySQL database.");
            
            // Create Auth object and pass it to the database connection
            Auth auth = new Auth(connection);
            Naming.rebind("rmi://127.0.0.1:1090/Auth", auth);
            System.out.println("Auth lié dans RMIregistry");

            // Create Result object and pass it to the database connection
            Result result = new Result(connection);
            Naming.rebind("rmi://127.0.0.1:1090/Result", result);
            System.out.println("Result lié dans RMIregistry");

            // Create vote object and pass it to the database connection
            Vote vote = new Vote(connection);
            Naming.rebind("rmi://127.0.0.1:1090/Vote", vote);
            System.out.println("Vote lié dans RMIregistry");

            System.out.println("Attente des invocations des clients");

        } catch (Exception e) {
            System.out.println("Erreur de liaison de l'objet");
            e.printStackTrace();
        }
    }
}
