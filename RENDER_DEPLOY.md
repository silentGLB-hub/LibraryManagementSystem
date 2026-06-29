# Deploy Library Management System to Render

## 1. Database

This application uses MySQL. Create a reachable MySQL 8 database with an
external provider, then run `database.sql` once on that database.

Set these Render environment variables:

```text
LIBRARY_DB_URL=jdbc:mysql://HOST:PORT/library_db?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
LIBRARY_DB_USER=your_database_user
LIBRARY_DB_PASSWORD=your_database_password
```

Do not use `localhost` in `LIBRARY_DB_URL`. Inside the Render container,
`localhost` refers to the container itself.

## 2. Render service

Create a Web Service with these values:

```text
Language: Docker
Branch: main
Root Directory: leave blank
Dockerfile Path: ./Dockerfile
Health Check Path: /login
```

The container listens on Render's `PORT` variable and deploys the WAR as the
root application. Open the generated `https://SERVICE.onrender.com/` URL.

## 3. Email reminders

SMTP is optional. Configure the following variables to enable reminders:

```text
LIBRARY_SMTP_HOST=smtp.example.com
LIBRARY_SMTP_PORT=587
LIBRARY_SMTP_TLS=true
LIBRARY_SMTP_FROM=library@example.com
LIBRARY_SMTP_USER=library@example.com
LIBRARY_SMTP_PASSWORD=your-app-password
```

## 4. Uploaded covers and PDF books

Render's filesystem is ephemeral by default. Files uploaded while the service
is running can disappear after a restart or deploy. Use a paid persistent disk
or external object storage before relying on uploads in production.
