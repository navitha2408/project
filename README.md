# Java Project Collection 🚀

This repository contains three Java-based projects focused on database operations, data structures, and MySQL integration.

---

##  Projects Included

### 1.  DatabaseApp (JavaFX + MySQL GUI)
A JavaFX-based GUI tool to manage MySQL tables such as `EMP`, `BONUS`, and `DEPT`. Supports operations like:

- ✅ Create Table
- ✅ Insert, Update, Delete rows
- ✅ Select and display data
- ✅ Drop & Truncate table
- ✅ Checkbox-based row deletion
- ✅ Background color feedback for actions

 Technologies: `JavaFX`, `JDBC`, `MySQL`, `Swing`, `JDK 21+`

>  Make sure to update your MySQL connection URL, username, and password inside the source code before running.

---

### 2.  MySQLTestDB
Simple Java program to test your MySQL connection and execute basic SQL operations like creating a table.

- Connects to a local database
- Executes `CREATE TABLE` and `INSERT` statements
- Basic `SELECT` output to console

 Technologies: `Java`, `JDBC`, `MySQL Connector/J`

---

### 3.  DataStructureCalculator
A console-based data structure tool that performs expression evaluation and checks:

- ✅ Stack, Queue, Linked List operations
- ✅ Parentheses matching
- ✅ Expression balancing validation

 Technologies: `Java (OOP, Collections)`, `Scanner`, `Stack`, `Queue`, `LinkedList`

---

##  How to Run

1. Install [Java JDK 21+](https://www.oracle.com/java/technologies/javase-downloads.html)
2. Add `mysql-connector-j-9.3.0.jar` to your classpath
3. For JavaFX:
   ```bash
   javac --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml DatabaseApp.java
   java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml DatabaseApp
