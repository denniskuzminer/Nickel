import java.sql.*;
import java.util.*;

public class BankTester {

    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement statement = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        String user = "";
        String password = "";
        Scanner scnr = new Scanner(System.in);
        boolean isValid = true;
        boolean quit = false;
        do {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");

                System.out.println("Enter username to enter Nickel Bank Database: ");
                user = scnr.nextLine();
                System.out.println("Enter password: ");
                password = scnr.nextLine();
                System.out.println("Connecting to database...");
                connection = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", user,
                        password);
                isValid = true;
            } catch (SQLException e1) {
                isValid = false;
                System.out.println("Invalid username or password. Try again.");
            } catch (ClassNotFoundException e2) {
                e2.printStackTrace();
            }
        } while (!isValid);
        System.out.println("\n---Connected to Nickel Bank---\n");
        while (!quit) {
            System.out.println("Welcome to Nickel Bank.");
            System.out.println("How can we help you?");
            System.out.println("Type p to enter a purchase.");
            System.out.println("Type lp or cp to make a loan or card payment.");
            System.out.println("Type d or w to make a withdrawal or deposit.");
            System.out.println("Type l to enter a loan.");
            System.out.println("Type q to exit Nickel Bank Management System.");
            String ans = scnr.nextLine();
            switch (ans) {
                case "p":
                    System.out.println("Are you using a credit or debit card? ");
                    isValid = false;
                    while (!isValid) {
                        ans = scnr.nextLine();
                        if (ans.equalsIgnoreCase("debit") || ans.equalsIgnoreCase("debit card")) {
                            Purchase p = new Purchase("debit", user, password);
                            isValid = true;
                        }
                        else {
                            if (ans.equalsIgnoreCase("credit") || ans.equalsIgnoreCase("credit card")) {
                                Purchase p = new Purchase("credit", user, password);
                                isValid = true;
                            } else {
                                System.out.println("Please enter either debit or credit card");
                                isValid = false;
                            }
                        }
                    }
                    break;
                case "lp":
                    LoanCardPayment lp = new LoanCardPayment("loan", user, password);
                    break;
                case "cp":
                    LoanCardPayment cp = new LoanCardPayment("card", user, password);
                    break;
                case "d":
                case "w":
                    AccountDepWith dw = new AccountDepWith(user, password);
                    break;
                case "l":
                    TakeOutLoan l = new TakeOutLoan(user, password);
                    break;
                case "q":
                    System.out.println("Thank you for choosing Nickel Bank!");
                    quit = true;
                    break;
                default:
                    System.out.println("Please enter in a valid letter from the menu.");
                    quit = false;
                    continue;
            }
        }
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
}