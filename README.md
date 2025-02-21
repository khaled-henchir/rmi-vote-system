# RMI Vote System

A simple **RMI-based voting system** using **JavaFX** for the client and server-side implementation. The system allows clients to vote remotely, with a server that handles authentication, vote processing, and results retrieval. The application integrates with a MySQL database to store votes and results.

## Features
- **RMI (Remote Method Invocation)** for client-server communication.
- **JavaFX** for the client-side GUI.
- **MySQL database** integration for storing votes and results.
- Client and server both interact through RMI to submit votes, authenticate users, and retrieve voting results.
  
## Technologies Used
- **Java** (JDK 1.1+)
- **RMI (Remote Method Invocation)**
- **JavaFX** (for client-side GUI)
- **MySQL** (for database management)

## Getting Started

### Prerequisites

Before running the project, make sure you have the following installed:

- Java 
- MySQL

### Setting Up the Project

1. Clone the repository:
    ```bash
    git clone https://github.com/your-username/rmi-vote-system.git
    cd rmi-vote-system
    ```

2. Set up the MySQL database:
    - Create a new database in MySQL:
    ```sql
    CREATE DATABASE vote_system;
    ```
    - Create the required tables for votes and users.
    
3. **Configure Database Connection**:
   Update the database connection details in the `Serveur.java` file to point to your MySQL database using environment variables (DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD).

