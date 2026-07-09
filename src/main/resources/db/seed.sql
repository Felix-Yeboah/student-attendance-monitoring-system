INSERT IGNORE INTO students (student_number, first_name, last_name, email, programme, gender, active) VALUES
('UCC/IT/001', 'Ama', 'Mensah', 'ama.mensah@example.edu.gh', 'MSc Information Technology', 'Female', TRUE),
('UCC/IT/002', 'Kwame', 'Asante', 'kwame.asante@example.edu.gh', 'MSc Information Technology', 'Male', TRUE),
('UCC/IT/003', 'Esi', 'Owusu', 'esi.owusu@example.edu.gh', 'MSc Information Technology', 'Female', TRUE),
('UCC/IT/004', 'Kojo', 'Boateng', 'kojo.boateng@example.edu.gh', 'MSc Information Technology', 'Male', TRUE),
('UCC/IT/005', 'Adwoa', 'Appiah', 'adwoa.appiah@example.edu.gh', 'MSc Information Technology', 'Female', TRUE);

INSERT IGNORE INTO courses (course_code, course_title, lecturer_name, semester) VALUES
('INF811D', 'Object Oriented Programming', 'Dr. Example Lecturer', 'Semester 1'),
('INF812D', 'Database Systems', 'Dr. Example Lecturer', 'Semester 1'),
('INF813D', 'Research Methods in IT', 'Dr. Example Lecturer', 'Semester 1');
