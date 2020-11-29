drop table customer;

create table customer (
	cust_id char(5),
	name varchar(20) not null,
	address varchar(100),
	phone varchar(12),
	primary key (cust_id) );


insert into customer values('10001','John Smith','7889 East Virginia Court, Brooklyn, NY 11220','347-893-2276');
insert into customer values('10002','John Smith Jr.','9364 Greenrose Lane, Brooklyn, NY 11233','718-230-5840');
insert into customer values('10003','Sam Cooke','48 La Sierra Lane, Brooklyn, NY 11220','917-971-5073');
insert into customer values('10004','Al Green','256 Shirley Drive, Brooklyn, NY 11212','929-293-2458');
insert into customer values('10005','Ray Charles','9918 NE. Second Rd., Brooklyn, NY 11216','917-990-6307');
insert into customer values('10006','Luther Vandross','335 South Paris Hill St., Brooklyn, NY 11238','718-922-7277');
insert into customer values('10007','Otis Redding','8919 Birch Hill St., Brooklyn, NY 11220','212-909-9199');
insert into customer values('10008','Sharon Jones','5 Pheasant Ave., Brooklyn, NY 11210','212-787-5902');
insert into customer values('10009','Durand Jones','668 Vale Ave., Brooklyn, NY 11221','347-875-8089');
insert into customer values('10010','Eli Reed','34 Sussex St., Brooklyn, NY 11201','646-763-3509');

select * from customer;


drop table account;

create table account (
	acc_id char(5),
	min_balance numeric(8,2),
	type varchar(8),
	rate numeric(5, 3),
	balance numeric(8,2),
	penalty_fee numeric(8, 2), 
	primary key (acc_id),
	check (type in ('Checking', 'Savings') ), 
	check ( ( type = 'Savings' ) or ( penalty_fee = 0 and type = 'Checking' and min_balance = 0 ) ),
	check ( balance > 0 ) );

insert into account values('10001',0,'Checking',0.01,2000,0);
insert into account values('10002',2000,'Savings',0.02,100000,10);
insert into account values('10003',0,'Checking',0.01,100,0);
insert into account values('10004',0,'Checking',0.01,200,0);
insert into account values('10005',1000,'Savings',0.02,64572,10);
insert into account values('10006',900,'Savings',0.02,48973,25);
insert into account values('10007',1500,'Savings',0.02,88888,15);
insert into account values('10008',0,'Checking',0.01,300,0);
insert into account values('10009',0,'Checking',0.01,1400,0);
insert into account values('10010',700,'Savings',0.02,66666,20);

select * from account;

drop table customer_account;

create table customer_account (
	cust_id char(5),
	acc_id char(5),
	primary key (cust_id, acc_id),
	foreign key (cust_id) references customer(cust_id),
	foreign key (acc_id) references account(acc_id) );
    
insert into Customer_account values('10001','10001');
insert into Customer_account values('10001','10002');
insert into Customer_account values('10003','10003');
insert into Customer_account values('10004','10004');
insert into Customer_account values('10005','10005');
insert into Customer_account values('10006','10006');
insert into Customer_account values('10007','10007');
insert into Customer_account values('10008','10001');
insert into Customer_account values('10009','10009');
insert into Customer_account values('10010','10010');

select * from customer_account;


drop table credit_card;


create table credit_card ( 
	credit_card_number char(16),
	expiration_date char(5) not null,
	rate numeric(5,3) not null,
	limit numeric(8, 2) not null,
	running_balance numeric(8, 2) not null,
	balance_due numeric(8, 2) not null,
	primary key (credit_card_number) );
    
insert into credit_card values('7783518622113010','09/23',0.23,10000,4003,1500.15);
insert into credit_card values('7324967385718960','08/23',0.23,15000,7342.55,2544.99);
insert into credit_card values('7870866056111850','11/23',0.23,20000,1100.55,0);
insert into credit_card values('7293052337143920','02/24',0.23,13000,666.66,0);
insert into credit_card values('7090336151672750','11/22',0.23,17500,8888.88,10000.11);

select * from credit_card;


drop table loan;

create table loan (
	loan_id char(5),
    acc_id char(5),
	amount numeric(8, 2),
	type varchar(9),
	monthly_payment numeric(8, 2),
	rate numeric(5, 3),
	primary key (loan_id),
    foreign key (acc_id) references account(acc_id),
	check (type in ('Mortgage', 'Unsecured') ) );
    
insert into loan values('20001','10002',500000,'Mortgage',2285,0.0457);
insert into loan values('20005','10005',20000,'Unsecured',130.8,0.0654);
insert into loan values('20007','10007',15000,'Unsecured',101.85,0.0679);
insert into loan values('20006','10006',200000,'Mortgage',1002,0.0501);
insert into loan values('20009','10009',999999,'Mortgage',4860,0.0486);
insert into loan values('20010','10010',9800,'Unsecured',300,0.075);

select * from transactionloan;
select * from loan;

drop table branch;

create table branch (
	branch_id char(5),
	address varchar(100),
	teller_type varchar(4),
	primary key (branch_id),
	check (teller_type in ('ATM', 'Both', 'None') ) );
    
insert into branch values('10001','951 Kings Highway, Brooklyn, NY, 11223','Both');
insert into branch values('10002','407 Manhattan Avenue, Brooklyn, NY 11222','ATM');
insert into branch values('10000','Not affiliated with branch ','None');
    
select * from branch;

drop table debit_card;


create table debit_card ( 
	debit_card_number char(16),
	acc_id char(5),
	expiration_date char(5) not null,
	primary key (debit_card_number), 
	foreign key (acc_id) references account(acc_id) );
    
insert into debit_card values('7751654471668020','10001','03/21');
insert into debit_card values('7404457929934360','10003','03/23');
insert into debit_card values('7595269365885050','10004','12/20');
insert into debit_card values('7631283434174050','10008','06/24');
insert into debit_card values('7772728260951300','10009','07/21');


select * from debit_card;

drop table Customer_creditcard;

create table Customer_creditcard (
	credit_card_number char(16),
	cust_id char(5),
	primary key (credit_card_number, cust_id),
	foreign key (credit_card_number) references credit_card(credit_card_number),
	foreign key (cust_id) references customer(cust_id) );
    
insert into Customer_creditcard values('7783518622113010','10002');
insert into Customer_creditcard values('7324967385718960','10005');
insert into Customer_creditcard values('7870866056111850','10006');
insert into Customer_creditcard values('7293052337143920','10007');
insert into Customer_creditcard values('7090336151672750','10010');
insert into Customer_creditcard values('7783518622113010','10001');
insert into Customer_creditcard values('7324967385718960','10002');
insert into Customer_creditcard values('7870866056111850','10003');
insert into Customer_creditcard values('7293052337143920','10004');
insert into Customer_creditcard values('7090336151672750','10005');
insert into Customer_creditcard values('7783518622113010','10006');
insert into Customer_creditcard values('7324967385718960','10007');
insert into Customer_creditcard values('7870866056111850','10008');
insert into Customer_creditcard values('7293052337143920','10009');

select * from Customer_creditcard;
/*
drop table customer_loan;

create table customer_loan(
	cust_id char(5),
	loan_id char(5),
	primary key (loan_id),
	foreign key (cust_id) references customer(cust_id),
	foreign key (loan_id) references loan(loan_id) );
    
insert into Customer_loan values('10001','20001');
insert into Customer_loan values('10005','20005');
insert into Customer_loan values('10007','20007');
insert into Customer_loan values('10006','20006');
insert into Customer_loan values('10009','20009');

select * from customer_loan;
*/
drop table transactionaccount;

create table transactionaccount ( 
	trans_id char(6),
	acc_id char(5),
	--This is the location where the trasaction occurred
	branch_id char(5),
	type varchar(20),
	time timestamp,
	--SET time_zone='+00:00';
	amount numeric(8, 2),
	primary key (trans_id),
	foreign key (acc_id) references account(acc_id),
	foreign key (branch_id) references branch(branch_id),
	--There needs to be a contraint that makes Deposits only valid if the location of this transaction is at a bank that is BOTH
	check (type in (
	'Withdrawal', 'Deposit', 'Fund Transfer', 'Purchase', 'Loan Payment', 'Card Payment' 
	) ) );
    
insert into transactionaccount values('100001','10001','10001','Deposit',TO_TIMESTAMP('2020-03-24 3:05:23', 'YYYY-MM-DD HH24:MI:SS.FF'),1000);
insert into transactionaccount values('100002','10001','10002','Withdrawal',TO_TIMESTAMP('2020-01-13 4:25:13', 'YYYY-MM-DD HH24:MI:SS.FF'),-500);
insert into transactionaccount values('100003','10001','10001','Fund Transfer',TO_TIMESTAMP('2020-02-02 10:12:56', 'YYYY-MM-DD HH24:MI:SS.FF'),-100);
insert into transactionaccount values('100004','10002','10001','Fund Transfer',TO_TIMESTAMP('2020-02-02 10:12:56', 'YYYY-MM-DD HH24:MI:SS.FF'),100);
insert into transactionaccount values('100005','10004','10000','Purchase',TO_TIMESTAMP('2020-03-05 12:15:15', 'YYYY-MM-DD HH24:MI:SS.FF'),-30);
insert into transactionaccount values('100006','10004','10000','Purchase',TO_TIMESTAMP('2020-04-02 3:15:15', 'YYYY-MM-DD HH24:MI:SS.FF'),-55);
insert into transactionaccount values('100007','10004','10000','Purchase',TO_TIMESTAMP('2020-03-28 15:15:15', 'YYYY-MM-DD HH24:MI:SS.FF'),-77.77);

select * from transactionaccount;

drop table transactioncreditcard;

create table transactioncreditcard ( 
	trans_id char(6),
	credit_card_number char(16),
	--This is the location where the trasaction occurred
	branch_id char(5),
	type varchar(20),
	time timestamp,
	--SET time_zone='+00:00';
	amount numeric(8, 2),
	primary key (trans_id),
	foreign key (credit_card_number) references credit_card(credit_card_number),
	foreign key (branch_id) references branch(branch_id),
	--There needs to be a contraint that makes Deposits only valid if the location of this transaction is at a bank that is BOTH
	check (type in (
	'Withdrawal', 'Deposit', 'Fund Transfer', 'Purchase', 'Loan Payment', 'Card Payment' 
	) ) );
    
insert into transactioncreditcard values('200001','7783518622113010','10001','Purchase',TO_TIMESTAMP('2020-02-21 11:56:36', 'YYYY-MM-DD HH24:MI:SS.FF'),-1000);
insert into transactioncreditcard values('200002','7324967385718960','10002','Purchase',TO_TIMESTAMP('2020-02-02 10:12:56', 'YYYY-MM-DD HH24:MI:SS.FF'),-500);
insert into transactioncreditcard values('200003','7870866056111850','10001','Purchase',TO_TIMESTAMP('2019-09-09 9:13:35', 'YYYY-MM-DD HH24:MI:SS.FF'),-100);
insert into transactioncreditcard values('200004','7293052337143920','10001','Card Payment',TO_TIMESTAMP('2023-03-03 2:18:34', 'YYYY-MM-DD HH24:MI:SS.FF'),2000);
insert into transactioncreditcard values('200005','7090336151672750','10000','Purchase',TO_TIMESTAMP('2021-09-02 3:34:39', 'YYYY-MM-DD HH24:MI:SS.FF'),-30);
insert into transactioncreditcard values('200006','7090336151672750','10000','Purchase',TO_TIMESTAMP('2022-08-01 11:18:59', 'YYYY-MM-DD HH24:MI:SS.FF'),-55);
insert into transactioncreditcard values('200007','7090336151672750','10000','Purchase',TO_TIMESTAMP('2020-12-07 7:17:30', 'YYYY-MM-DD HH24:MI:SS.FF'),-77.77);

select * from transactioncreditcard;

drop table transactionloan;

create table transactionloan ( 
	trans_id char(6),
	loan_id char(5),
	type varchar(20),
	time timestamp,
	--SET time_zone='+00:00';
	amount numeric(8, 2),
	primary key (trans_id),
	foreign key (loan_id) references loan(loan_id),
	--There needs to be a contraint that makes Deposits only valid if the location of this transaction is at a bank that is BOTH
	check (type in (
	'Loan Created', 'Loan Payment'
	) ) );

insert into transactionloan values('300001','20001','Loan Created',TO_TIMESTAMP('2021-03-30 12:51:57', 'YYYY-MM-DD HH24:MI:SS.FF'),500000);
insert into transactionloan values('300002','20005','Loan Created',TO_TIMESTAMP('2020-02-03 12:13:35', 'YYYY-MM-DD HH24:MI:SS.FF'),20000);
insert into transactionloan values('300003','20007','Loan Created',TO_TIMESTAMP('2018-08-08 8:19:48', 'YYYY-MM-DD HH24:MI:SS.FF'),15000);
insert into transactionloan values('300004','20006','Loan Created',TO_TIMESTAMP('2023-03-03 2:18:34', 'YYYY-MM-DD HH24:MI:SS.FF'),200000);
insert into transactionloan values('300005','20009','Loan Created',TO_TIMESTAMP('2020-07-01 10:24:34', 'YYYY-MM-DD HH24:MI:SS.FF'),999999);
insert into transactionloan values('300006','20010','Loan Created',TO_TIMESTAMP('2021-09-21 10:12:36', 'YYYY-MM-DD HH24:MI:SS.FF'),10000);
insert into transactionloan values('300007','20010','Loan Payment',TO_TIMESTAMP('2021-10-21 10:24:26', 'YYYY-MM-DD HH24:MI:SS.FF'),-200);

select * from transactionloan;

grant select on customer to grader;
grant select on account to grader;
grant select on credit_card to grader;
grant select on loan to grader;
grant select on branch to grader;
grant select on debit_card to grader;
grant select on customer_account to grader;
grant select on customer_creditcard to grader;
grant select on transactionaccount to grader;
grant select on transactioncreditcard to grader;
grant select on transactionloan to grader;


select balance from account join debit_card on account.acc_id = debit_card.acc_id where debit_card_number = 7751654471668020;

update account set balance = 2000 where acc_id = '10001';

update loan set amount = 9600 where acc_id = '10010';

select * from transactioncreditcard;
select * from credit_card;

select MAX(trans_id) from transactionaccount;

select running_balance from credit_card where credit_card_number = 7783518622113010;

select * from transactioncreditcard order by trans_id;

select * from account;

select * from branch;


update account set penalty_fee = 10 where acc_id = '10002';
update account set penalty_fee = 10 where acc_id = '10005';
update account set penalty_fee = 25 where acc_id = '10006';
update account set penalty_fee = 15 where acc_id = '10007';
update account set penalty_fee = 20 where acc_id = '10010';

select * from loan;

SELECT * from transactionloan;

select MAX(loan_id) as loan_id from loan;
select MAX(trans_id) as trans_id from transactionloan;
select MAX(trans_id) as trans_id from transactionloan;


create trigger tr_loan after insert on loan
for each row 
begin 
    insert into transactionloan(loan_id, type, time, amount) 
    values(loan_id, 'Loan Created',SYSDATE, amount);
end;

create sequence transactionloan_sequence;

CREATE OR REPLACE TRIGGER transactionloan_on_insert
  BEFORE INSERT ON transactionloan
  FOR EACH ROW
BEGIN
  SELECT transactionloan_sequence.nextval
  INTO :new.trans_id
  FROM dual;
END;

insert into loan values('20010','10001',500000,'Mortgage',2285,0.0457);


drop trigger tr_loan;

select * from user_tables;

    select * from takes;