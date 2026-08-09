# Role-Based SQL Client Application

## Overview
A Java Swing desktop application that connects to a MySQL database and lets different users execute SQL commands through a GUI, with results displayed in a table. Users authenticate with role-specific credentials (root, accountant, client) which determine their database permissions.

## Features
- GUI-based SQL command entry and execution (`SQLClientApp`)
- Configurable database connections via `.properties` files
- Dynamic result table rendering (`ResultSetTableModel`)
- Role-based accountant view for reviewing operations logs (`AccountantApp`)

## Tech Stack
- Java Swing (GUI)
- JDBC (MySQL Connector/J)

## How to Run
1. Set up a local MySQL instance and create the databases referenced in the `.properties` files.
2. Update the `.properties` files with your own database credentials.
3. Compile and run:
```
javac -cp .:mysql-connector-j.jar *.java
java -cp .:mysql-connector-j.jar SQLClientApp
```

## Notes
Screenshots of each role's command flow are included in the `*Screenshots` folders.
