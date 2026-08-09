# Enterprise Application Development Series

A series of projects exploring enterprise application architecture — event-driven programming, databases, client-server systems, and multi-tier web applications.

Each project builds upon the previous, gradually simulating enterprise-level software systems.

---

## Project 1: Package Management Facility Simulator
A **multi-threaded Java** application simulating a package routing facility.
- Routes package groups between shared conveyors using concurrent threads
- Implements **thread synchronization and locking** to prevent deadlocks
- Uses `CountDownLatch` and `CyclicBarrier` to coordinate station startup
- Focuses on safe concurrent access to shared resources, not database transactions

[Read More →](./Project1/README.md)

---

## Project 2: Role-Based SQL Client Application
A **Java Swing GUI application** using **JDBC and MySQL**.
- Allows multiple users to execute SQL commands on a remote database
- Implements role-based permissions for root, accountant, and client users
- Logs all user operations in a separate operations log database
- GUI front-end validates user credentials and displays query results

[Read More →](./Project2/README.md)

---

## Project 3: Enterprise Web Management System
A **multi-tier web application** using **Servlets, JSP, JDBC, and MySQL**.
- User login with role-based access (root, client, data-entry, accountant)
- Enforces business rules across suppliers, parts, jobs, and shipments
- Uses prepared statements for safe SQL execution
- Runs on **Apache Tomcat** with a persistent MySQL backend

[Read More →](./Project3/README.md)

---
