# FinanceFlow
FinanceFlow is a comprehensive web interface designed to help individuals manage their personal finances effectively. The platform allows users to track income and expenses, categorize transactions, set budget goals, and visualize spending patterns through interactive charts and reports......


Quick Start Guide:
Use MySQL versions 8.0.44 for server and workbench and get 8.0.33 for MySQL connector.
Used Tomcat 11.0.
Ensure DBUtil.java contains the correct user and password information.
	My local SQL server password for root was "p1FF%" if you want to copy that to your root pwd.
Ensure your NetBeans version is compatible(check NetBeans version screenshot).

About:
Currently this is the Backend portion of the project. Only PreparedStatements are used and the username is
	tracked through each session. There are only 2 tables but it was decided that two tables should be
	enough as user info will be kept in one table and all transactions in the other.

Database schema:

users:
- uid INT PRIMARY KEY AUTO_INCREMENT | unique id for each user
- name VARCHAR(50) | username
- pwd VARCHAR(50) | password

transactions:
- tid INT PRIMARY KEY AUTO_INCREMENT | unique id for transactions if they need to be referenced later
- uid INT NOT NULL | each transaction has the user's unique id to tie it to their account
- transaction_type ENUM('income', 'expense') NOT NULL | keep track of type of transactions
- FOREIGN KEY (uid) REFERENCES user(uid) | uid is our foreign key

How to run:
1. Ensure all versions are compatible(versions listed above in "Quick Start Guide")
2. Create your database and tables using by copy and pasting from the DatabaseCreationScript text file and running those commands.
3. Ensure your MySQL server and DBUtil.java file contain the correct user and password information otherwise exceptions.
4. Ensure both your tomcat and sql servers are running.
5. Ensure your tomcat is properly connected to your netbeans project.
6. Build and run the project in netbeans.
7. Use MySQL Workbench to check and verify database updates.

URLS vs Servlets:
Every Url should follow this convention for the servlets
http://localhost:8080/FinanceFlow/ServletName.
For example: 
	RegisterServlet is: http://localhost:8080/FinanceFlow/RegisterServlet

- Note:
	Currently LoginServlet and DataServlet do not utilize their pages
