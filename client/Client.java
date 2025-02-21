import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Map;

public class Client extends Application {

    private AuthInterface auth;
    private ResultInterface result;
    private VoteInterface vote;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // RMI connections
            auth = (AuthInterface) Naming.lookup("rmi://localhost:1090/Auth");
            result = (ResultInterface) Naming.lookup("rmi://localhost:1090/Result");
            vote = (VoteInterface) Naming.lookup("rmi://localhost:1090/Vote");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Connection Error", "Failed to connect to the RMI server!");
            return;
        }

        VBox loginLayout = createLoginLayout(primaryStage);
        Scene loginScene = new Scene(loginLayout, 400, 300);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("JavaFX Login & Signup");
        primaryStage.show();
    }

    private VBox createLoginLayout(Stage stage) {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText(), stage));

        Button signupButton = new Button("Sign Up");
        signupButton.setOnAction(e -> stage.setScene(new Scene(createSignupLayout(stage), 400, 300)));

        VBox loginLayout = new VBox(15, usernameField, passwordField, loginButton, signupButton);
        loginLayout.setStyle("-fx-padding: 30px; -fx-alignment: center;");
        return loginLayout;
    }

    private VBox createSignupLayout(Stage stage) {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        Button signupButton = new Button("Sign Up");
        signupButton.setOnAction(e -> handleSignup(usernameField.getText(), passwordField.getText(), confirmPasswordField.getText()));

        Button loginButton = new Button("Back to Login");
        loginButton.setOnAction(e -> stage.setScene(new Scene(createLoginLayout(stage), 400, 300)));

        VBox signupLayout = new VBox(15, usernameField, passwordField, confirmPasswordField, signupButton, loginButton);
        signupLayout.setStyle("-fx-padding: 30px; -fx-alignment: center;");
        return signupLayout;
    }

    private void handleLogin(String username, String password, Stage stage) {
        try {
            boolean loggedIn = auth.login(username, password);
            if (loggedIn) {
                showAlert(Alert.AlertType.INFORMATION, "Login Success", "You are logged in!");
                stage.setScene(new Scene(createMainLayout(stage), 500, 500));
                stage.setTitle("Voting System");
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials!");
            }
        } catch (RemoteException e) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Error communicating with the server.");
        }
    }

    private VBox createMainLayout(Stage stage) {
    Label titleLabel = new Label("Vote for Your Candidate");
    titleLabel.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");

    VBox candidatesBox = new VBox(15);
    candidatesBox.setStyle("-fx-padding: 20px;");

    try {
        // Fetch the candidates dynamically from the RMI Result interface
        Map<String, Integer> candidates = result.getResult();

        for (Map.Entry<String, Integer> entry : candidates.entrySet()) {
            String candidate = entry.getKey();
            // Remove the vote count part
            HBox candidateRow = new HBox(15);
            candidateRow.setStyle("-fx-alignment: center-left;");

            Label nameLabel = new Label(candidate);
            nameLabel.setStyle("-fx-font-size: 16px;");

            Button voteButton = new Button("Vote");
            voteButton.setOnAction(e -> handleVote(candidate));

            candidateRow.getChildren().addAll(nameLabel, voteButton);
            candidatesBox.getChildren().add(candidateRow);
        }

    } catch (RemoteException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch candidates.");
    }

    Button resultsButton = new Button("Results");
    resultsButton.setOnAction(e -> handleResults());

    Button logoutButton = new Button("Logout");
    logoutButton.setOnAction(e -> handleLogout(stage));

    VBox layout = new VBox(20, titleLabel, candidatesBox, resultsButton, logoutButton);
    layout.setStyle("-fx-padding: 30px; -fx-alignment: center;");
    return layout;
}



private void handleVote(String candidate) {
    try {
        String username = auth.getCurrentUser();  

        if (username == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user is logged in.");
            return;
        }

        boolean hasVoted = auth.hasUserVoted();
        if (hasVoted) {
            showAlert(Alert.AlertType.ERROR, "Voting Error", "You have already voted!");
            return;
        }

        boolean success = vote.SubmitVote(username, candidate);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Vote Success", "You voted for: " + candidate);
            boolean voteMarked = auth.markUserAsVoted();
            if (!voteMarked) {
                showAlert(Alert.AlertType.WARNING, "Vote Status", "Failed to mark your vote status.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Vote Failed", "Failed to cast your vote.");
        }
    } catch (RemoteException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Error communicating with the server.");
    }
}



    private void handleSignup(String username, String password, String confirmPassword) {
        try {
            if (!password.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Signup Failed", "Passwords do not match!");
                return;
            }

            boolean registered = auth.register(username, password);
            if (registered) {
                showAlert(Alert.AlertType.INFORMATION, "Signup Success", "You have successfully signed up!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Signup Failed", "User may already exist or registration failed.");
            }
        } catch (RemoteException e) {
            showAlert(Alert.AlertType.ERROR, "Signup Failed", "Error communicating with the server.");
        }
    }

    private void handleLogout(Stage stage) {
        showAlert(Alert.AlertType.INFORMATION, "Logout", "You have successfully logged out.");
        stage.setScene(new Scene(createLoginLayout(stage), 400, 300));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleResults() {
    try {
        // Fetch the results from the server
        Map<String, Integer> results = result.getResult();

        // Create a new window to display the results
        Stage resultsStage = new Stage();
        VBox resultsLayout = new VBox(15);
        resultsLayout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        // Add results to the layout
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            String candidate = entry.getKey();
            int voteCount = entry.getValue();

            HBox resultRow = new HBox(15);
            resultRow.setStyle("-fx-alignment: center-left;");

            Label resultLabel = new Label(candidate + " - Votes: " + voteCount);
            resultLabel.setStyle("-fx-font-size: 16px;");

            resultRow.getChildren().add(resultLabel);
            resultsLayout.getChildren().add(resultRow);
        }

        Scene resultsScene = new Scene(resultsLayout, 400, 300);
        resultsStage.setScene(resultsScene);
        resultsStage.setTitle("Voting Results");
        resultsStage.show();
    } catch (RemoteException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch results.");
    }
}

}
