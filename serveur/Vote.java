import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Vote extends UnicastRemoteObject implements VoteInterface {
    private Map<String, Integer> voteResults;
    private Connection connection;

    public Vote(Connection connection) throws RemoteException {
        super();
		this.connection = connection;
        voteResults = new HashMap<>();
		fetchCandidatesFromDb();
    }

    private void fetchCandidatesFromDb() {
        String query = "SELECT candidate_name FROM vote_results";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String candidateName = resultSet.getString("candidate_name");
                voteResults.put(candidateName, 0); // Initialize vote count to 0
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing the query or processing the results", e);
        }
    }

    @Override
    public Map<String, Integer> candidatlist() throws RemoteException {
        return voteResults;
    }

    @Override
    public boolean SubmitVote(String username, String candidate) throws RemoteException {
        if (voteResults.containsKey(candidate)) {
            // Check if the user has already voted
            if (hasUserVoted(username)) {
                System.out.println("User has already voted.");
                return false;
            }

            // Update vote count in memory
            int currentVotes = voteResults.get(candidate);
            voteResults.put(candidate, currentVotes + 1);

            // Persist the vote to the database
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE users SET has_voted = 1 WHERE username = ?")) {
                statement.setString(1, username);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    System.out.println("No rows updated for user: " + username);
                    return false; // No rows updated, something went wrong
                }
            } catch (SQLException e) {
                System.out.println("Error while updating user vote status: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

            // Update candidate's vote count in the database
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE vote_results SET votes = ? WHERE candidate_name = ?")) {
                statement.setInt(1, currentVotes + 1);
                statement.setString(2, candidate);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    System.out.println("No rows updated for candidate: " + candidate);
                    return false; // No rows updated, something went wrong
                }
            } catch (SQLException e) {
                System.out.println("Error while updating vote count: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

            return true;
        } else {
            System.out.println("Candidate not found in voteResults: " + candidate);
        }
        return false;
    }

    private boolean hasUserVoted(String username) {
    String query = "SELECT has_voted FROM users WHERE username = ?";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setString(1, username);
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("has_voted") == 1; // If has_voted is 1, the user has voted
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();  // Log the error or handle it accordingly
    }
    return false;
}

}
