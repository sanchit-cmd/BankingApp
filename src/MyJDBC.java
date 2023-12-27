import java.sql.*;

public class MyJDBC {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";
    private static final String DB_PASSWORD = "sanchit";
    private static final String DB_USERNAME = "root";

    private static Connection connectDb() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // if valid an object with the user info is returned
    public User validateLogin(String username, String password) {
        try {
            Connection connection = connectDb();
            assert connection != null;
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userID = resultSet.getInt("id");
                double currentBalance = resultSet.getDouble("current_balance");
                System.out.println("Login Complete!");
                return new User(userID, username, password, currentBalance);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Register new user into database
    public boolean register(String username, String password) {
        try {
            Connection connection = connectDb();
            if (checkUser(username)) {
                System.out.println("User Name Already Taken!");
                return false;
            }

            if (!validateNewUser(username))
                return false;

            assert connection != null;
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO users(username, password) " + "VALUES(?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            preparedStatement.executeUpdate();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check if the user already exist in the database
    public boolean checkUser(String username) {
        try {
            Connection connection = connectDb();

            assert connection != null;
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // check whether the user has entered a username of length greater than 6
    public boolean validateNewUser(String username) {
        if (username.length() >= 6) {
            return true;

        } else {
            System.out.println("Username must contain more than 6 characters!");
            return false;
        }
    }

    // displays all the transactions
    public void displayTransactions(User user) {
        try {
            Connection connection = connectDb();
            PreparedStatement transactions = connection.prepareStatement(
                    "SELECT * FROM transactions WHERE user_id = ?");
            transactions.setInt(1, user.getId());

            ResultSet resultSet = transactions.executeQuery();

            while (resultSet.next()) {
                double amountVal = resultSet.getDouble("transaction_amount");
                String transactionType = resultSet.getString("transaction_type");

                System.out.println(amountVal + " " + transactionType);
            }
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // adds new transactions to the database
    public static boolean addTransactionToDatabase(Transaction transaction) {
        try {
            Connection connection = connectDb();
            PreparedStatement insertTransaction = connection.prepareStatement(
                    "INSERT transactions(user_id, transaction_type, transaction_amount, transaction_date) VALUES(?,?,?,NOW())");
            insertTransaction.setInt(1, transaction.getUserId());
            insertTransaction.setString(2, transaction.getTransactionType());
            insertTransaction.setDouble(3, transaction.getTransactionAmount());

            insertTransaction.execute();
            connection.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // updates the user's current balance in the database
    public static boolean updateCurrentBalance(User user) {
        try {
            Connection connection = connectDb();
            PreparedStatement updateBalance = connection.prepareStatement(
                    "UPDATE users SET current_balance = ? WHERE id = ?");

            updateBalance.setDouble(1, user.getCurrentBalance());
            updateBalance.setInt(2, user.getId());

            updateBalance.executeUpdate();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // transfer the amount to a different user
    public static boolean transfer(User user, String transferedUser, double amount) {
        try {
            User transferUser = null;
            Connection connection = connectDb();
            PreparedStatement transferQuery = connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ?");

            transferQuery.setString(1, transferedUser);

            ResultSet resultSet = transferQuery.executeQuery();

            while (resultSet.next()) {

                transferUser = new User(resultSet.getInt("id"), transferedUser,
                        resultSet.getString("password"),
                        resultSet.getDouble("current_balance"));
            }

            if (transferUser != null) {
                Transaction transferTransaction = new Transaction(user.getId(), "transfer", -amount, null);

                Transaction recievedTransaction = new Transaction(transferUser.getId(), "transfer", amount, null);

                transferUser.setCurrentBalance(transferUser.getCurrentBalance() + amount);
                updateCurrentBalance(transferUser);

                user.setCurrentBalance(user.getCurrentBalance() - amount);
                updateCurrentBalance(user);

                addTransactionToDatabase(transferTransaction);
                addTransactionToDatabase(recievedTransaction);

                return true;
            } else {
                System.out.println("User does not exist...");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
