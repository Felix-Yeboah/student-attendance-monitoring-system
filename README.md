# Student Attendance Monitoring System

A JavaFX desktop application for recording student attendance, managing students and courses, and producing attendance reports. The project is designed for the **INF811D Object Oriented Programming** practical project at the University of Cape Coast.

## Features

- Dashboard with student, course, and today’s attendance totals.
- Student management: add, update, search, and delete student records.
- Course management: add, update, and delete courses.
- Attendance register: select a course and date, mark each active student as Present, Late, or Absent, then save in one action.
- Reports: filter attendance by course, date, and status; see totals and attendance rate; export a CSV file.
- Input validation and clear error messages.
- MySQL persistence using JDBC and prepared statements.
- Professional JavaFX interface built with FXML and CSS.

## Technology stack

| Area | Technology |
|---|---|
| Language | Java 21 |
| User interface | JavaFX 21 + FXML + CSS |
| Build tool | Maven |
| Database | MySQL 8+ |
| Database access | JDBC / MySQL Connector/J |
| IDE | IntelliJ IDEA Community Edition |
| Version control | Git and GitHub |

## Project structure

```text
student-attendance-monitoring-system/
├── docs/                         Project documentation and report prompts
├── screenshots/                  Put real screenshots here after running the app
├── src/
│   ├── main/
│   │   ├── java/com/ucc/attendance/
│   │   │   ├── controller/        JavaFX controllers
│   │   │   ├── dao/               Database access objects
│   │   │   ├── database/          Connection and database setup
│   │   │   ├── exception/         Project-specific exceptions
│   │   │   ├── model/             Domain classes
│   │   │   ├── service/           Business logic
│   │   │   └── util/              Validation and helper classes
│   │   │
│   │   └── resources/
│   │       ├── com/ucc/attendance/fxml/
│   │       ├── com/ucc/attendance/css/
│   │       └── db/                Schema and sample data scripts
│   └── test/                      Unit tests
├── pom.xml
└── SETUP_GUIDE.md
```

## Prerequisites

1. **JDK 21** installed and selected in IntelliJ IDEA.
2. **IntelliJ IDEA Community Edition**.
3. **MySQL Server 8 or newer** running locally.
4. Internet access on first run so Maven can download project dependencies.
5. Git and a GitHub account.

## First-time setup

1. Extract the project ZIP.
2. Open IntelliJ IDEA, choose **Open**, and select this project folder (the folder containing `pom.xml`).
3. When IntelliJ asks to load Maven changes, choose **Load**.
4. Configure your local MySQL credentials:

   ```text
   Copy database.properties.template to database.properties
   ```

   Edit `database.properties` and enter your MySQL username and password. This local file is ignored by Git.
5. Start MySQL.
6. In IntelliJ, open the Maven panel and run:

   ```text
   javafx > javafx:run
   ```

   Or run this command in the project terminal:

   ```bash
   mvn clean javafx:run
   ```

The application creates `student_attendance_db`, its tables, and sample data automatically when it first connects. Your MySQL user must have permission to create a database and tables.

## Database setup alternative

If your MySQL account does not have permission to create a database automatically, run these scripts manually from MySQL Workbench in this order:

1. `src/main/resources/db/schema.sql`
2. `src/main/resources/db/seed.sql`

Then set the URL in `database.properties` to the database you created.

## Running tests

```bash
mvn test
```

## Building a distributable runtime image

```bash
mvn clean javafx:jlink
```

Maven creates a platform-specific runtime image in `target/`. Test the generated application on the same operating system before submission.

## Git workflow

Create the Git repository before making personal changes:

```bash
git init
git add .
git commit -m "Initial JavaFX attendance monitoring system"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/student-attendance-monitoring-system.git
git push -u origin main
```

Do **not** commit `database.properties`, because it may contain your password.

## OOP evidence

See [`docs/OOP_EVIDENCE.md`](docs/OOP_EVIDENCE.md) for the exact classes that demonstrate encapsulation, inheritance, polymorphism, and abstraction.

## Screenshots for submission

After you have launched and tested the program, capture screenshots of:

1. Dashboard
2. Student management screen
3. Course management screen
4. Attendance register with marked statuses
5. Attendance report / CSV export result

Save them in the `screenshots/` folder and reference them from your final technical report.

## Important academic note

This codebase is a learning scaffold. Read every class, run and test it yourself, customise the interface and sample data, and write the report from your own implementation experience.
