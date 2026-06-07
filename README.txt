================================================================
       CAT ADOPTION CENTER SYSTEM - README
================================================================

PROJECT TITLE   : Cat Adoption Center Management System


----------------------------------------------------------------
OVERVIEW
----------------------------------------------------------------
A text-based Java application connected to a MySQL database
for managing a cat adoption center. The system supports
cat listings, adoptions, adopter management, worker schedules,
fee tracking, and reporting.


----------------------------------------------------------------
REQUIREMENTS
----------------------------------------------------------------
- Java 21 or higher
- MySQL 8.0 or higher
- MySQL JDBC Driver (mysql-connector-j 9.3.0)
- Maven 3.6 or higher (optional, for building from source)


----------------------------------------------------------------
DATABASE SETUP 
----------------------------------------------------------------
1. Create the database in MySQL:

   CREATE DATABASE cat_adoption_db;

2. Run the schema script:

   SOURCE /path/to/createschema.sql;

3. Run the data script:

   SOURCE /path/to/initdata.sql;

To reset the database from scratch:

   SOURCE /path/to/dropschema.sql;
   SOURCE /path/to/createschema.sql;
   SOURCE /path/to/initdata.sql;


----------------------------------------------------------------
CONFIGURATION
----------------------------------------------------------------
Before running the application, open DBConnection.java and
set your MySQL credentials:

   String url      = "jdbc:mysql://localhost:3306/cat_adoption_db";
   String user     = "root";
   String password = " ";


----------------------------------------------------------------
HOW TO RUN (using JAR file)
----------------------------------------------------------------
Main class: petadoption.Main

Run with the following command:

   java -cp pet-adoption-db-1.0-SNAPSHOT.jar petadoption.Main

Or if the JDBC driver is separate:

   java -cp pet-adoption-db-1.0-SNAPSHOT.jar:mysql-connector-j-9.3.0.jar petadoption.Main

On Windows, replace : with ;

   java -cp pet-adoption-db-1.0-SNAPSHOT.jar;mysql-connector-j-9.3.0.jar petadoption.Main


----------------------------------------------------------------
HOW TO RUN (using Maven from source)
----------------------------------------------------------------
   mvn compile
   mvn exec:java -Dexec.mainClass="petadoption.Main"


----------------------------------------------------------------
APPLICATION MENUS
----------------------------------------------------------------
The application has 6 main menus:

  [1] Cat Menu
      - Add a new cat
      - Search available cats by type
      - Delete an available cat

  [2] Fee Menu
      - Update adoption fee by cat type 
      - Analyze fee change history 

  [3] Adopter Menu
      - View all adopters
      - Search adopter by name
      - View adopter details with adoption history
      - Add new adopter
      - Update adopter info 
      - Delete adopter
      - View adopter demographic history 
      - Compare sales BEFORE vs AFTER demographic change 
      - Sales breakdown by city across all adopters 

  [4] Adoption Menu
      - Browse available cats
      - Create new adoption transaction
      - View transaction details
      - List all transactions
      - Filter transactions by adopter
      - Filter transactions by shelter

  [5] Worker Menu
      - Add worker
      - Search workers by role
      - Update worker salary 
      - Add worker schedule
      - Show schedules by date
      - Delete worker schedule
      - Delete worker

  [6] Report Menu
      - Worker salary report by role
      - Worker count and average salary by shelter city
      - Adoption summary by shelter city
      - Adoption fee change analysis by cat type
      - Adopter demographic change sales analysis


----------------------------------------------------------------
PROJECT STRUCTURE
----------------------------------------------------------------
src/
  main/
    java/
      petadoption/
        Main.java           - Entry point, main menu
        DBConnection.java   - MySQL connection setup
        CatMenu.java        - Cat management
        FeeMenu.java        - Fee management (REQ13)
        AdopterMenu.java    - Adopter management (REQ14)
        AdoptionMenu.java   - Adoption transactions
        WorkerMenu.java     - Worker management
        ReportMenu.java     - Reports and analysis
        TablePrinter.java   - Formats query results as table
        InputHelper.java    - Shared user input utilities

sql/
  createschema.sql          - Creates all tables, indexes, views
  initdata.sql              - Inserts sample data
  dropschema.sql            - Drops all views and tables




