import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        String username, password;
        User user = null;
        double amount;

        Scanner sc = new Scanner(System.in);
        MyJDBC jdbc = new MyJDBC();
        boolean running = false;
        System.out.println(
                """
                        What would you like to Do?
                        1. Login
                        2. Register
                        3. Quit
                        """);
        int userInput = sc.nextInt();
        sc.nextLine();

        switch (userInput) {
            case 1:
                System.out.println("Enter Username: ");
                username = sc.nextLine();
                System.out.println("Enter Password: ");
                password = sc.nextLine();
                user = jdbc.validateLogin(username, password);

                if (user != null) {
                    running = true;
                } else {
                    System.out.println("Login Failed! Please try again later...");
                }
                break;

            case 2:
                System.out.println("Enter Username: ");
                String usernameReg = sc.nextLine();
                System.out.println("Enter Password: ");
                String passwordReg = sc.nextLine();

                boolean register = jdbc.register(usernameReg, passwordReg);

                if (register) {
                    System.out.println("Registered");
                    System.out.println("please Login!");
                    System.out.println();

                    System.out.println("Enter Username: ");
                    username = sc.nextLine();
                    System.out.println("Enter Password: ");
                    password = sc.nextLine();
                    user = jdbc.validateLogin(username, password);

                    if (user != null) {
                        running = true;
                    } else {
                        System.out.println("Login Failed! Please try again later...");
                    }

                } else {
                    System.out.println("Error");
                }
                break;
            case 3:
                System.exit(0);
                break;
            default:
                sc.close();
                System.out.println("Invalid Input...");
                break;
        }

        while (running) {
            System.out.println();
            System.out.println("Current Balance: " + user.getCurrentBalance());
            System.out.println();
            System.out.println(
                    """
                            1. Deposit
                            2. Withdraw
                            3. Transaction
                            4. Transfer
                            5. Quit
                            """);

            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Enter Amount:");
                    amount = sc.nextDouble();
                    handleTransaction("deposit", amount, user);
                    break;

                case 2:
                    System.out.println("Enter Amount:");
                    amount = sc.nextDouble();
                    if (amount > user.getCurrentBalance()) {
                        System.out.println("Insufficient Balance");
                    } else {
                        handleTransaction("withdraw", amount, user);
                    }
                    break;

                case 3:
                    System.out.println("Transactions are:");
                    jdbc.displayTransactions(user);
                    break;

                case 4:
                    System.out.println("Enter username: ");
                    sc.nextLine();
                    String transferUsername = sc.nextLine();

                    System.out.println("Enter amount");
                    double transferAmount = sc.nextDouble();

                    handleTransfer(user, transferUsername, transferAmount);
                    break;

                case 5:
                    System.out.println("Thank you");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid Input...");
                    break;
            }

        }
        sc.close();
    }

    static void handleTransaction(String transactionType, double amount, User user) {
        Transaction transaction = null;

        if (transactionType.equalsIgnoreCase("deposit")) {
            user.setCurrentBalance(user.getCurrentBalance() + amount);
            transaction = new Transaction(user.getId(), transactionType, amount, null);
        } else {
            user.setCurrentBalance(user.getCurrentBalance() - amount);
            transaction = new Transaction(user.getId(), transactionType, -amount, null);
        }

        if (MyJDBC.addTransactionToDatabase(transaction) && MyJDBC.updateCurrentBalance(user)) {
            System.out.println(transactionType + " Successful");
        }
    }

    static void handleTransfer(User user, String transferUser, double amount) {
        if (MyJDBC.transfer(user, transferUser, amount)) {
            System.out.println("Transfer Success...");
        } else {
            System.out.println("Transfer Failed...");
        }
    }
}
