CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_number VARCHAR(30) NOT NULL UNIQUE,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    programme VARCHAR(120) NOT NULL,
    gender VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    username VARCHAR(60) NOT NULL UNIQUE,
    password_salt VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'LECTURER') NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(15) NOT NULL UNIQUE,
    course_title VARCHAR(150) NOT NULL,
    lecturer_name VARCHAR(120) NOT NULL,
    semester VARCHAR(40) NOT NULL,
    lecturer_user_id INT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (lecturer_user_id)
        REFERENCES users(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS attendance_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('PRESENT', 'ABSENT', 'LATE') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    UNIQUE (student_id, course_id, attendance_date)
);