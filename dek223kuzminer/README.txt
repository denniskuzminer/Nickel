README

This is the README file

Run the command "tree" to view all files and directories as a reference 

Go to dek223kuzminer -> BankFinalProjectSRC and run the following command

javac -cp ./:../dek223/ojdbc8.jar -d ../dek223 *.java

Then change directories to dek223 by typing cd .. and then cd dek223 and type ls -lt. 
After, run the following commands

jar cfmv dek223.jar Manifest.txt *.class

java -jar dek223.jar

NOTE: Having SQL Developer open when running the jar file *may* cause the code to withhold 
conflicting connections to the database (such as initial connections, updates, and queries). To ensure that 
the code runs properly, it is strongly encouraged to close out other connections to the database on 
your machine. 

The project spec states “You should implement at least the deposit/withdrawal interface(2), the 
purchase interface(7) and one more of your choice.” -> I have implemented 2, 7, and 6) “Take out a loan”
EDIT: After waiting a long time after I submitted, I realized that I probably had enough time to do another interface, 
so I also implemented 3) “Payment on a loan or credit card”
Hopefully this could make up for some points I may have missed in some other areas. : )

Enter a username and password and connect to the database
From here there will be a menu where you can choose from p, d/w, l, lp, cp, and q

p - This is to enter a purchase 
This will ask for a debit or credit
Here is a sample debit card number to make purchases on belonging to account 10001: 7751654471668020
Here is a sample credit card number to make purchases on belonging to customer 10001: 7783518622113010
Then enter whether the purchase was made at a location NOT affiliated with Nickel Bank
If you respond no, enter the branch location of the purchase
Then you will enter a purchase that is not over the limit or will make a checking account go negative
A valid amount will be recorded in transaction tables

d/w - This is to enter a deposit or withdrawal and is handled by the same class
For this, you will need to enter an account number 
Use account 10001 as a sample
Then enter which location the deposit/withdrawal was made in (You cannot deposit at an 
ATM-only location and the code will not let you enter a deposit)
Then you must input a deposit/withdrawal amount 
A negative value will be considered as a withdrawal; positive, a deposit.
Then it will bring you back to the main menu 

l - This is to take out a loan 
For this, you will need to enter an account number 
Use account 10001 as a sample
Then enter what type of loan you would like to take out
Then enter the amount, monthly payment, and rate


lp - This will allow a user to pay a loan based on the loan_id 
Use loan 20010 as a sample
Then enter a payment
Then everything else will be inserted by java

cp - This will allow a user to pay off some of their running balance on their credit card
based on the credit card number
Use card 7783518622113010 as a sample
You must then enter a physical location (where teller type is labeled “Both”) where this payment was made
Then enter a payment
Then everything else will be inserted by java
(cp and lp are very similar so they are handled by the same class)

q - This will exit the program

A note about the java code: 
The code was created to implement a high level of abstraction so that the code that is 
written into the main method just consists of constructors.
Within these constructors is a method called setDetails().
Within setDetails() is a group of smaller methods that break down the problem into smaller tasks.
These methods query the user for all information needed to insert data into table and maintain
the consistency of the database. 
All setDetails() methods and the main method closes out the connection and statements

A note about the data:
All data was created by me 
I used Excel to bulk-generate insert statements
Most customer names are just the names of old soul singers (idk)
All card numbers start with 7

-- Dennis Kuzminer
dek223@lehigh.edu
Prof Sihong Xie
Section 011
