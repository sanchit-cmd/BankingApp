public class User {
    private final int id;
    private final String username, password;
    private double currentBalance;

    User(int id, String username, String password, double currentBalance) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.currentBalance = currentBalance;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

}