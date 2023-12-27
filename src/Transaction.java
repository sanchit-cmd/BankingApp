import java.sql.Date;

public class Transaction {
    private final int userId;
    private final String transactionType;
    private final double transactionAmount;
    private final Date transactionDate;

    public Transaction(int userId, String transactionType, double transactionAmount, Date transactionDate) {
        this.userId = userId;
        this.transactionAmount = transactionAmount;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
    }

    public int getUserId() {
        return userId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }
}
