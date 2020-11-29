import java.security.InvalidAlgorithmParameterException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.sql.Timestamp;

public class AccountDepWith {

    private Scanner scnr = new Scanner(System.in);
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private Connection connection = null;
    private boolean isValid = true;
    private String checkingOrSavings;
    private String trans_id;
    private String acc_id;
    private String branch_id;
    private String type;
    private String currentTimestamp;
    private double amount;
    private double penalty;
    private double min_balance;
    private double balance;

    public AccountDepWith(String user, String password) {
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", user,
                    password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setDetails();
    }

    private void setDetails() {
        setAcc_id();
        setCheckingOrSavings();
        setBranch_id();
        setAmount(); // This method should identify whether it is a (-)withdrawal or (+)deposit
        setType();
        setTrans_id();
        setCurrentTimestamp();
        recordTransaction();

        System.out.println("---Returning to main menu---\n");

        try {
            if(statement != null)
                statement.close();
        }
        catch(SQLException sqlException) {
            sqlException.printStackTrace();
        }
        try {
            if(connection != null)
                connection.close();
        }
        catch(SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    private void setType() {
        if(amount >= 0) {
            type = "Deposit";
        }
        if(amount < 0) {
            type = "Withdrawal";
        }
    }

    private void setCurrentTimestamp() {
        Date date = new Date();
        Timestamp ts=new Timestamp(date.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentTimestamp = formatter.format(ts);
    }

    private void setAcc_id() {
        isValid = false;
        // This will keep looping until there is valid input
        while (!isValid) {
            try {
                // This just gets the acc number
                System.out.println("Please enter your account number: ");
                acc_id = scnr.nextLine();
                String query = "select acc_id from account where acc_id = " + acc_id;
                statement = connection.prepareStatement(query);
                resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    throw new IllegalArgumentException();
                }
                isValid = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Your account is not in our database. Please check that you are entering an existing account.");
                isValid = false;
            } catch (SQLSyntaxErrorException e) {
                System.out.println("This account is not in our database. Please check that you are entering an existing account.");
                isValid = false;
            } catch (SQLException e) {
                System.out.println("This account is not in our database. Please check that you are entering an existing account.");
                isValid = false;
            }
        }
    }

    private void setCheckingOrSavings() {
        try {
            String query = "select type from account where acc_id = " + acc_id;
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                System.out.println("Empty result.");
                throw new Exception();
            } else {
                do {
                    checkingOrSavings = (resultSet.getString("type")).toLowerCase();
                } while (resultSet.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAmount() {
        isValid = false;
        while(!isValid) {
            try {
                isValid = true;
                System.out.println("What was the value of the withdrawal/deposit? \n" +
                        "A negative value will be considered as a withdrawal; positive, a deposit: ");
                String amt = scnr.nextLine();
                amount = Double.parseDouble(amt);
                //System.out.println(amount);
                if(amount >= 0) {
                    if(branch_id.equals("10002")) {
                        throw new InvalidAlgorithmParameterException();
                    }
                    System.out.println("Depositing $" + (amount));
                }
                if(amount < 0) {
                    System.out.println("Withdrawing $" + -(amount));
                }
                String query = "select balance from account where account.acc_id = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, acc_id);
                resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    System.out.println("Empty result.");
                    throw new SQLException();
                } else {
                    do {
                        isValid = true;
                        System.out.println("Balance before deposit/withdrawal: " + resultSet.getString("balance"));
                        balance = Double.parseDouble(resultSet.getString("balance"));
                    } while (resultSet.next());
                    if(checkingOrSavings.equals("checking")) {
                        if (balance + amount < 0) {
                            System.out.println("Insufficient Funds. Withdrawal Declined.");
                            throw new SQLException();
                        } else {
                            balance += amount;
                            String update = "update account set balance = " + (balance) +
                                    " where account.acc_id = " + (acc_id);
                            statement = connection.prepareStatement(update);
                            statement.executeUpdate();
                            System.out.println("Withdrawal/Deposit Approved. \nYour current account balance is: " + (balance));
                        }
                    }
                    if(checkingOrSavings.equals("savings")) {
                        try {
                            query = "select min_balance from account where acc_id = " + acc_id;
                            statement = connection.prepareStatement(query);
                            resultSet = statement.executeQuery();
                            if (!resultSet.next()) {
                                System.out.println("Empty result.");
                                throw new SQLException();
                            } else {
                                do {
                                    min_balance = Double.parseDouble(resultSet.getString("min_balance"));
                                } while (resultSet.next());
                            }
                            query = "select penalty_fee from account where acc_id = " + acc_id;
                            statement = connection.prepareStatement(query);
                            resultSet = statement.executeQuery();
                            if (!resultSet.next()) {
                                System.out.println("Empty result.");
                                throw new SQLException();
                            } else {
                                do {
                                    penalty = Double.parseDouble(resultSet.getString("penalty_fee"));
                                } while (resultSet.next());
                            }
                            if (balance + amount < min_balance) {
                                balance = balance + amount - penalty;
                                System.out.println("Your current balance has fallen below your minimum balance. Penalty fee of $" + penalty + " imposed.");
                            } else {
                                balance += amount;
                            }
                            String update = "update account set balance = " + (balance) +
                                    " where account.acc_id = " + (acc_id);
                            statement = connection.prepareStatement(update);
                            statement.executeUpdate();
                            System.out.println("Withdrawal/Deposit Approved. \nYour current account balance is: " + (balance));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
                isValid = false;
            } catch (InvalidAlgorithmParameterException e) {
                System.out.println("Note that deposit services are not available at ATM locations.\n" +
                        "If you are looking to make a deposit, please do this at an in-person location.\n" +
                        "Please enter a withdrawal amount.");
                isValid = false;
            } catch (SQLException e) {
                e.getMessage();
                isValid = false;
            }
        }
    }

    private void setBranch_id() {
        isValid = false;
        while(!isValid) {
            try {
                System.out.println("At which branch location was this withdrawal or deposit made: ");
                String query = "select * from branch where branch_id > 10000";
                statement = connection.prepareStatement(query);
                resultSet = statement.executeQuery();
                ResultSetMetaData rsmd = resultSet.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                while (resultSet.next()) {
                    for (int i = 1; i <= columnsNumber; i++) {
                        if (i > 1) System.out.print(",  ");
                        String columnValue = resultSet.getString(i);
                        System.out.print(columnValue + " " + rsmd.getColumnName(i));
                    }
                    System.out.println("");
                }
                branch_id = scnr.nextLine();
                isValid = false;
                try {
                    query = "select * from branch where branch_id = " + branch_id;
                    statement = connection.prepareStatement(query);
                    resultSet = statement.executeQuery();
                    if (!resultSet.next()) {
                        throw new IllegalArgumentException();
                    }
                    isValid = true;
                } catch (IllegalArgumentException | SQLException e) {
                    System.out.println("Please enter the ID of the bank location where this payment was made.");
                    isValid = false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                isValid = false;
            }
        }
    }

    private void setTrans_id() {
        int tempTransID = 0;
        try {
            String query = "select MAX(trans_id) as trans_id from transactionaccount";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                System.out.println("Empty result.");
                throw new Exception();
            } else {
                do {
                    tempTransID = Integer.parseInt(resultSet.getString("trans_id"));
                } while (resultSet.next());
            }
            tempTransID++;
            trans_id = Integer.toString(tempTransID);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void recordTransaction() {
        try {
            String query = "insert into transactionaccount values ('" + trans_id + "','" + acc_id + "','" +
                    branch_id + "','" + type + "',TO_TIMESTAMP('" + currentTimestamp + "', 'YYYY-MM-DD HH24:MI:SS.FF')," + -(amount) + ")";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}