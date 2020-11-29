import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.sql.Timestamp;

public class LoanCardPayment {
    private Scanner scnr = new Scanner(System.in);
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private Connection connection = null;
    private boolean isValid = true;
    private String acc_id;
    private String branch_id;
    private String type;
    private double amount;
    private String loanOrCard;
    private String cardNumber;
    private String loan_id;
    private String currentTimestamp;
    private String trans_id = "";

    public LoanCardPayment(String loanOrCard, String user, String password) {
        this.loanOrCard = loanOrCard;
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", user,
                    password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setDetails();
    }

    private void setDetails() {
        setLoan_id();
        //setAcc_id();
        setCardNumber();
        setType();
        setBranch_id();
        setAmount();
        setTrans_id();
        setCurrentTimestamp();
        recordTransaction();
        printConfirmation();
        System.out.println("---Returning to main menu---\n");

        try {
            if (statement != null)
                statement.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    private void setAcc_id() {
        if (loanOrCard.equals("loan")) {
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
    }

    private void setCardNumber() {
        if (loanOrCard.equals("card")) {
            isValid = false;
            // This will keep looping until there is valid input
            while (!isValid) {
                try {
                    // This just gets the card number
                    System.out.println("Please enter your credit card number: ");
                    cardNumber = scnr.nextLine();
                    if (!cardNumber.chars().allMatch(Character::isDigit)) {
                        throw new NumberFormatException();
                    }
                    String query = "select credit_card_number from credit_card where credit_card_number = ?";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, cardNumber);
                    resultSet = statement.executeQuery();
                    if (!resultSet.next()) {
                        throw new IllegalArgumentException();
                    }
                    isValid = true;
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
    }

    private void setType() {
        if(loanOrCard.equals("loan")) {
            type = "Loan Payment";
        }
        if(loanOrCard.equals("card")) {
            type = "Card Payment";
        }
    }

    private void setBranch_id() {
        if (loanOrCard.equals("card")) {
            isValid = false;
            while (!isValid) {
                try {
                    System.out.println("At which branch location was this card payment made: " +
                            "\nNOTE: Card payments can only be made at physical locations where teller type is labeled \"Both\"");
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
                        query = "select * from branch where teller_type = 'Both' and branch_id = " + branch_id;
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
    }

    private void setCurrentTimestamp() {
        Date date = new Date();
        Timestamp ts=new Timestamp(date.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentTimestamp = formatter.format(ts);
    }


    private void setTrans_id() {
        // I unnecessarily duplicated the code to attempt to solve errors with connections
        if (loanOrCard.equals("loan")) {
            String query = "";
            int tempTransID = 0;
            try {
                query = "select MAX(trans_id) as trans_id from transactionloan";
                statement = connection.prepareStatement(query);
                resultSet = statement.executeQuery();
                //System.out.println(query);
                if (!resultSet.next()) {
                    System.out.println("Empty result.");
                    throw new Exception();
                } else {
                    do {
                        //System.out.println(resultSet.getString("trans_id"));
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
        if(loanOrCard.equals("card")) {
            String query = "";
            int tempTransID = 0;
            try {
                query = "select MAX(trans_id) as trans_id from transactioncreditcard";
                statement = connection.prepareStatement(query);
                resultSet = statement.executeQuery();
                //System.out.println(query);
                if (!resultSet.next()) {
                    System.out.println("Empty result.");
                    throw new Exception();
                } else {
                    do {
                        //System.out.println(resultSet.getString("trans_id"));
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
    }

    private void printConfirmation(){
        if(loanOrCard.equals("loan")) {
            System.out.println("Confirmed. A loan payment of $" + amount + " has been payed for loan: " + loan_id);
        }
        if(loanOrCard.equals("card")) {
            System.out.println("Confirmed. A card payment of $" + amount + " has been payed for card: " + cardNumber);
        }
    }

    private void setLoan_id() {
        if(loanOrCard.equals("loan")) {
            isValid = false;
            while (!isValid) {
                System.out.println("Please select the id of the loan you would like to pay for: ");
                loan_id = scnr.nextLine();
                try {
                    String query = "select loan_id from loan where loan_id = " + loan_id;
                    statement = connection.prepareStatement(query);
                    resultSet = statement.executeQuery();
                    //System.out.println(query);
                    if (!resultSet.next()) {
                        //System.out.println("Empty result.");
                        throw new IllegalArgumentException();
                    }
                    isValid = true;
                } catch (IllegalArgumentException | SQLException e) {
                    System.out.println("Please enter a valid loan id.");
                    isValid = false;
                }
            }
        }
    }

    private void setAmount() {
        double balance = 0.0;
        isValid = false;
        while(!isValid) {
            try {
                if (loanOrCard.equals("loan")) {
                    String query = "select amount from loan where loan_id = " + loan_id;
                    statement = connection.prepareStatement(query);
                    resultSet = statement.executeQuery();
                    if (!resultSet.next()) {
                        System.out.println("Empty result.");
                        throw new SQLException();
                    } else {
                        do {
                            isValid = true;
                            System.out.println("Loan amount before payment: " + resultSet.getString("amount"));
                            balance = Double.parseDouble(resultSet.getString("amount"));
                        } while (resultSet.next());
                    }
                    System.out.println("What is the value of the loan payment: ");
                    String amt = scnr.nextLine();
                    amount = Double.parseDouble(amt);
                    // Checks to see if this is valid
                    if (amount > balance || amount < 0) {
                        throw new NumberFormatException();
                    } else {
                        isValid = true;
                        balance -= amount;
                        String update = "update loan set amount = " + (balance) +
                                " where loan_id = " + loan_id;
                        statement = connection.prepareStatement(update);
                        statement.executeUpdate();
                        System.out.println("Loan Payment Approved. \nYour current loan amount is: " + (balance));
                    }
                }
                if (loanOrCard.equals("card")) {
                    double running_balance = 0.0;
                    String query = "select running_balance from credit_card where credit_card_number = " + cardNumber;
                    statement = connection.prepareStatement(query);
                    resultSet = statement.executeQuery();
                    //System.out.println(query);
                    if (!resultSet.next()) {
                        System.out.println("Empty result.");
                        throw new Exception();
                    } else {
                        do {
                            running_balance = Double.parseDouble(resultSet.getString("running_balance"));
                        } while (resultSet.next());
                    }
                    System.out.println("Your credit card payment will go towards your running balance" +
                            "\nRunning Balance: " + running_balance);
                    System.out.println("What is the value of the credit card payment: ");
                    String amt = scnr.nextLine();
                    amount = Double.parseDouble(amt);
                    // Checks to see if this is valid
                    if (amount > running_balance || amount < 0) {
                        throw new IllegalArgumentException();
                    } else {
                        isValid = true;
                        running_balance -= amount;
                        String update = "update credit_card set running_balance = " + (running_balance) +
                                " where credit_card_number = " + cardNumber;
                        statement = connection.prepareStatement(update);
                        statement.executeUpdate();
                        System.out.println("Payment Approved. \nYour current running balance is: " + (running_balance));
                    }
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                isValid = false;
            } catch (NumberFormatException e) {
                System.out.println("You must enter a payment that is less or equal to than the current amount. Positive numbers only.");
                isValid = false;
            } catch (IllegalArgumentException e) {
                System.out.println("You must enter an amount that is less or equal to than the running balance. Positive numbers only.");
                isValid = false;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                isValid = false;
            }
        }
    }

    private void recordTransaction() {
        if(loanOrCard.equals("loan")) {
            try {
                String query = "insert into transactionloan values ('" + trans_id + "','" + loan_id + "','" +
                        type + "',TO_TIMESTAMP('" + currentTimestamp + "', 'YYYY-MM-DD HH24:MI:SS.FF'), -" + amount + ")";
                statement = connection.prepareStatement(query);
                resultSet = statement.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(loanOrCard.equals("card")) {
            try {
                String query = "insert into transactioncreditcard values ('" + trans_id + "','" + cardNumber + "','" +
                        branch_id + "','" + type + "',TO_TIMESTAMP('" + currentTimestamp + "', 'YYYY-MM-DD HH24:MI:SS.FF'), " + amount + ")";
                statement = connection.prepareStatement(query);
                resultSet = statement.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}