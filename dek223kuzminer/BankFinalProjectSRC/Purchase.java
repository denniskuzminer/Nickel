import java.sql.*;
import java.util.*;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Purchase {

    private Scanner scnr = new Scanner(System.in);
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private Connection connection = null;
    private boolean isValid = true;
    private String debitOrCredit;
    private String trans_id;
    private String acc_id;
    private String cardNumber;
    private String branch_id;
    private String type = "Purchase";
    private String currentTimestamp;
    private double amount;
    private double limit;
    private double running_balance;
    private double balance;

    public Purchase(String debitOrCredit, String user, String password) {
        this.debitOrCredit = debitOrCredit;
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", user,
                    password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setDetails();
    }

    private void setDetails() {
        setCardNumber();
        setBranch_id();
        setAmount();
        setAcc_id();
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

    private void setCurrentTimestamp() {
        Date date = new Date();
        Timestamp ts=new Timestamp(date.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentTimestamp = formatter.format(ts);
    }

    private void setCardNumber() {
        isValid = false;
        // This will keep looping until there is valid input
        while (!isValid) {
            try {
                // This just gets the card number
                System.out.println("Please enter card number: ");
                cardNumber = scnr.nextLine();
                if(!cardNumber.chars().allMatch( Character::isDigit )) {
                    throw new NumberFormatException();
                }
                // The if else if just confirms that the card is indeed in the database
                // If it is not, throws an exception
                if (debitOrCredit.equals("debit")) {
                    String query = "select debit_card_number from debit_card where debit_card_number = " + cardNumber;
                    statement = connection.prepareStatement(query);
                    resultSet = statement.executeQuery();
                    //statement.setString(1, cardNumber);
                    if (!resultSet.next()) {
                        throw new IllegalArgumentException();
                    }
                    isValid = true;
                }
                else {
                    if (debitOrCredit.equals("credit")) {
                        String query = "select credit_card_number from credit_card where credit_card_number = ?";
                        statement = connection.prepareStatement(query);
                        statement.setString(1, cardNumber);
                        resultSet = statement.executeQuery();
                        if (!resultSet.next()) {
                            throw new IllegalArgumentException();
                        }
                        isValid = true;
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a card number.");
                isValid = false;
            } catch (IllegalArgumentException e) {
                System.out.println("Your card is not in our database. Please check that you are entering a debit card.");
                isValid = false;
            } catch (SQLSyntaxErrorException e) {
                System.out.println("Please enter a valid number");
                isValid = false;
            } catch (SQLException e) {
                e.printStackTrace();
                isValid = false;
            }
        }
    }

    private void setAmount() {
        isValid = false;
        while(!isValid) {
            try {
                System.out.println("What was the value of the purchase: ");
                String amt = scnr.nextLine();
                amount = Double.parseDouble(amt);
                if (debitOrCredit.equals("debit")) {
                    // This block gets the balance from the associated checking account and stores it
                    String query = "select balance from account join debit_card on account.acc_id = debit_card.acc_id where debit_card_number = ?";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, cardNumber);
                    resultSet = statement.executeQuery();
                    if (!resultSet.next()) {
                        System.out.println("Empty result.");
                        throw new SQLException();
                    } else {
                        do {
                            isValid = true;
                            System.out.println("Balance before transaction: " + resultSet.getString("balance"));
                            balance = Double.parseDouble(resultSet.getString("balance"));
                        } while (resultSet.next());
                    }

                    // Checks to see if you have enough money for this transaction
                    if (balance - amount < 0) {
                        System.out.println("Insufficient Funds. Card Declined.");
                        // Eventually I will have to make sure that this is handled in the setDetails method
                        // Because if there is insufficient funds then it should bring you back to the main menu
                        // Prob will have to insert a try catch in the setDetails and then declare
                        // throws exception in the method header
                        throw new IllegalArgumentException();
                    } else {
                        //query
                        balance -= amount;
                        //7751654471668020
                        String update = "update account set balance = " + (balance) +
                                " where account.acc_id = " +
                                "(select account.acc_id from account join debit_card on account.acc_id = debit_card.acc_id " +
                                "where debit_card_number = " + cardNumber + " )";
                        statement = connection.prepareStatement(update);
                        statement.executeUpdate();
                        System.out.println("Transaction Approved. \nYour current account balance is: " + (balance));
                    }
                }
                else {
                    if(debitOrCredit.equals("credit")) {
                        // Finds card limit
                        String query = "select limit from credit_card where credit_card_number = ?";
                        statement = connection.prepareStatement(query);
                        statement.setString(1, cardNumber);
                        resultSet = statement.executeQuery();
                        if (!resultSet.next()) {
                            System.out.println("Empty result.");
                            throw new SQLException();
                        } else {
                            do {
                                isValid = true;
                                System.out.println("Card limit: " + resultSet.getString("limit"));
                                limit = Double.parseDouble(resultSet.getString("limit"));
                            } while (resultSet.next());
                        }

                        // Finds running_balance
                        String query2 = "select running_balance from credit_card where credit_card_number = ?";
                        statement = connection.prepareStatement(query2);
                        statement.setString(1, cardNumber);
                        resultSet = statement.executeQuery();
                        if (!resultSet.next()) {
                            System.out.println("Empty result.");
                            throw new SQLException();
                        } else {
                            do {
                                isValid = true;
                                System.out.println("Running Balance: " + resultSet.getString("running_balance"));
                                running_balance = Double.parseDouble(resultSet.getString("running_balance"));
                            } while (resultSet.next());
                        }

                        // Adds purchase to the running_balance
                        if (running_balance + amount > limit) {
                            System.out.println("Balance after purchase will surpass limit. Card Declined.");
                            // Prob need to put a more detailed exception
                            throw new IllegalArgumentException();
                        } else {
                            //query
                            running_balance += amount;
                            //7751654471668020
                            String update = "update credit_card set running_balance = " + (running_balance) +
                                    "where credit_card_number = " + cardNumber;
                            statement = connection.prepareStatement(update);
                            statement.executeUpdate();
                            System.out.println("Transaction Approved. \nYour current running balance is: " + (running_balance));
                        }
                    }
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Please enter a number.");
                isValid = false;
            } catch (SQLException e) {
                e.getMessage();
                isValid = false;
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter a valid purchase amount.");
                isValid = false;
            }
        }
    }

    private void setAcc_id() {
        if(debitOrCredit.equals("debit")) {
            try {
                String query = "select account.acc_id from account join debit_card on account.acc_id = debit_card.acc_id where debit_card_number = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, cardNumber);
                resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    System.out.println("Empty result.");
                    throw new Exception();
                } else {
                    do {
                        acc_id = resultSet.getString("acc_id");
                    } while (resultSet.next());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setBranch_id() {
        isValid = false;
        while(!isValid) {
            System.out.println("(Yes/No) Was this purchase made at a location not affiliated with Nickel Bank directly\n" +
                    "i.e. A retail location, Online, etc.");
            // This is just a temporary value that stores the answer
            branch_id = scnr.nextLine();
            if (branch_id.equals("Yes") || branch_id.equals("yes")) {
                branch_id = "10000";
                isValid = true;
            }
            else {
                if (branch_id.equals("No") || branch_id.equals("no")) {
                    try {
                        while (!isValid) {
                            System.out.println("Which branch location was this purchase made at: ");
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
                                if (branch_id.equals("10000")) {
                                    throw new IllegalArgumentException();
                                }
                                isValid = true;
                            } catch (IllegalArgumentException | SQLException e) {
                                System.out.println("Please enter the ID of the bank location where this payment was made.");
                                isValid = false;
                            }
                        }
                    } catch(SQLException e) {
                        e.printStackTrace();
                        isValid = false;
                    }
                } else {
                    System.out.println("Please enter Yes or No. ");
                    isValid = false;
                }
            }
        }
    }

    private void setTrans_id() {
        String query = "";
        int tempTransID = 0;
        try {
            if(debitOrCredit.equals("debit")) {
                query = "select MAX(trans_id) as trans_id from transactionaccount";
            } else {
                if (debitOrCredit.equals("credit")) {
                    query = "select MAX(trans_id) as trans_id from transactioncreditcard";
                }
            }
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
            if (debitOrCredit.equals("debit")) {
                String query = "insert into transactionaccount values ('" + trans_id + "','" + acc_id + "','" +
                        branch_id + "','" + type + "',TO_TIMESTAMP('" + currentTimestamp + "', 'YYYY-MM-DD HH24:MI:SS.FF')," + -(amount) + ")";
                statement = connection.prepareStatement(query);
                resultSet = statement.executeQuery();
            }
            if (debitOrCredit.equals("credit")) {
                String query = "insert into transactioncreditcard values ('" + trans_id + "','" + cardNumber + "','" +
                        branch_id + "','" + type + "',TO_TIMESTAMP('" + currentTimestamp + "', 'YYYY-MM-DD HH24:MI:SS.FF')," + -(amount) + ")";
                statement = connection.prepareStatement(query);
                resultSet = statement.executeQuery();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
////7783518622113010