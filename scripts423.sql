
SELECT students.name, students.age, faculties.name
FROM students
LEFT JOIN faculties ON students.faculty_id = faculties.id;

SELECT students.name, students.age, faculties.name
FROM students
LEFT JOIN faculties ON students.faculty_id = faculties.id
INNER JOIN avatars on students.avatar_id = avatars.id;