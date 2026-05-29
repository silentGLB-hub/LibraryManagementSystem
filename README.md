# Library Management System

Spring MVC + JDBC + MySQL application for Tomcat 10.

## Requirements

- JDK 17 or newer
- Maven 3.9 or newer
- MySQL 8 or newer
- Apache Tomcat 10

## Database setup

Run the SQL script:

```sql
SOURCE database.sql;
```

The script creates `library_db`, all tables, constraints, and demo data.

Demo accounts:

- `admin / 123`
- `lib / 123`
- `reader / 123`

## Database connection

The default connection is configured in:

```text
src/main/java/com/library/config/DbUtil.java
```

Defaults:

- URL: `jdbc:mysql://localhost:3306/library_db`
- User: `root`
- Password: empty

Update these values if your local MySQL credentials are different.

## Build and deploy

Build the WAR:

```bash
mvn clean package
```

Deploy:

```text
target/LibraryManagementSystem.war
```

to Tomcat 10, then open:

```text
http://localhost:8080/LibraryManagementSystem/
```

## Main features

- Login/logout
- Reader self-registration
- Role-based access for ADMIN, LIBRARIAN, and READER
- Dashboard
- Book search, add, delete, category/author/publisher assignment
- Category, author, and publisher management
- Reader account management
- Cover image upload and update
- Borrow and return books
- Automatic overdue status update
- Fine calculation at `5,000 VND/day`
- Excel/PDF report export
- Borrow statistics charts
- Responsive pages for desktop and mobile
- SMTP overdue reminder emails

## SMTP reminder configuration

Set these environment variables before starting Tomcat:

```text
LIBRARY_SMTP_HOST=smtp.example.com
LIBRARY_SMTP_PORT=587
LIBRARY_SMTP_TLS=true
LIBRARY_SMTP_FROM=library@example.com
LIBRARY_SMTP_USER=library@example.com
LIBRARY_SMTP_PASSWORD=your-password
```

Then open `Reports` and use `Send overdue emails`.
