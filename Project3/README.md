# Enterprise Web Management System

## Overview
A three-tier web application built with Jakarta Servlets and JSP, backed by a MySQL database. Users log in and are routed to a role-specific dashboard (Root, Accountant, Data Entry, or Client), each with different permissions for managing suppliers, parts, jobs, and shipments.

## Features
- Session-based authentication (`authenticationServlet`, `authentication.html`)
- Role-specific dashboards: `rootPage.jsp`, `accountantHome.jsp`, `dataEntryHome.jsp`, `clientPage.jsp`
- Servlet-backed data entry for parts, jobs, shipments, and suppliers
- Prepared statements for safe SQL execution against MySQL

## Tech Stack
- Jakarta Servlets & JSP
- MySQL (JDBC)
- Deployed on Apache Tomcat

## How to Run
1. Deploy to a Tomcat server with a configured MySQL database.
2. Update the database connection properties under `WEB-INF/lib/` with your own credentials.
3. Access `authentication.html` to log in and reach your role's dashboard.

## Notes
Screenshots of each role's workflow are included in the `*Screenshots` folders.
