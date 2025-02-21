import java.rmi.*;
import java.rmi.server.*;
import java.sql.*;
import java.util.Map;
import java.util.HashMap;

public class Result extends UnicastRemoteObject implements ResultInterface {
    private Map<String, Integer> voteResults;
    private Connection connection;

    public Result(Connection connection) throws RemoteException {
        super();
		this.connection=connection;
        voteResults = new HashMap<>();
    }

    private void fetchVoteResultsFromDB() {
        String query = "SELECT candidate_name, votes FROM vote_results";  
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String candidateName = resultSet.getString("candidate_name"); 
                int votes = resultSet.getInt("votes");
                voteResults.put(candidateName, votes);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing the query or processing the results", e);
        }
    }

    @Override
    public Map<String, Integer> getResult() throws RemoteException {
		fetchVoteResultsFromDB(); 
        return voteResults;
    }


}
