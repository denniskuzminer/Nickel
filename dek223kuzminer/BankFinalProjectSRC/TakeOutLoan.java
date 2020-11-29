import java.security.InvalidAlgorithmParameterException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.sql.Timestamp;

public class TakeOutLoan {
    private Scanner scnr = new Scanner(System.in);
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private Connection connection = null;
    private boolean isValid = true;
    private String acc_id;
    private String type;
    private double amount;
    private double monthly_payment;
    private double rate;
    private String loan_id;
    private String currentTimestamp;
    private String trans_id = "";

    public TakeOutLoan(String user, String password) {
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
        setType();
        setAmount();
        setMonthlyPayment();
        setRate();
        setLoan_id();
        setTrans_id();
        setCurrentTimestamp();
        recordLoan();
        recordTransaction();
        System.out.println("Confirmed. A loan of $" + amount + " has been issued for account: " + acc_id);
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

    private void setAmount() {
        isValid = false;
        while(!isValid) {
            try {
                System.out.println("Please enter the amount of the loan: ");
                String amt = scnr.nextLine();
                amount = Double.parseDouble(amt);
                isValid = true;
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Please enter a number. ");
                isValid = false;
            }
        }
    }

    private void setMonthlyPayment() {
        isValid = false;
        while(!isValid) {
            try {
                System.out.println("Please enter the monthly payment of the loan: ");
                String mp = scnr.nextLine();
                monthly_payment = Double.parseDouble(mp);
                isValid = true;
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Please enter a number. ");
                isValid = false;
            }
        }
    }

    private void setRate() {
        isValid = false;
        while(!isValid) {
            try {
                System.out.println("Please enter the rate of the loan: ");
                String r = scnr.nextLine();
                rate = Double.parseDouble(r);
                isValid = true;
                if(rate > 1 || rate < 0) {
                    throw new SQLDataException();
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Please enter a number. ");
                isValid = false;
            } catch (SQLDataException e) {
                System.out.println("Please enter a number between 0 and 1 to represent the rate.");
                isValid = false;
            }
        }
    }

    private void setType() {
        isValid = false;
        // This will keep looping until there is valid input
        while (!isValid) {
            try {
                // This just gets the acc number
                System.out.println("(1/2) What type of loan would you like to take out Mortgage or Unsecured: ");
                type = scnr.nextLine();
                if (type.equals("1") || type.equalsIgnoreCase("Mortgage")) {
                    type = "Mortgage";
                    isValid = true;
                } else {
                    if (type.equals("2") || type.equalsIgnoreCase("Unsecured")) {
                        type = "Unsecured";
                        isValid = true;
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter either a Mortgage or an Unsecured loan.");
                isValid = false;
            }
        }
    }

    private void setCurrentTimestamp() {
        Date date = new Date();
        Timestamp ts=new Timestamp(date.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentTimestamp = formatter.format(ts);
    }


    private void setLoan_id() {
        int tempLoanID = 0;
        try {
            String query = "select MAX(loan_id) as loan_id from loan";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                System.out.println("Empty result.");
                throw new Exception();
            } else {
                do {
                    tempLoanID = Integer.parseInt(resultSet.getString("loan_id"));
                } while (resultSet.next());
            }
            tempLoanID++;
            loan_id = Integer.toString(tempLoanID);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void recordLoan() {
        try {
            String query = "insert into loan values ('" + loan_id + "','" + acc_id + "'," +
                    amount + ",'" + type + "'," + monthly_payment + "," + rate + ")";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setTrans_id() {
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

    private void recordTransaction() {
        try {
            String query = "insert into transactionloan values ('" + trans_id + "','" + loan_id + "','" +
                    "Loan Created" + "',TO_TIMESTAMP('" + currentTimestamp + "', 'YYYY-MM-DD HH24:MI:SS.FF')," + amount + ")";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {  
            e.printStackTrace();
        }
    }
}