package ru.skypro.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skypro.school.entity.Student;
import ru.skypro.school.record.StudentAverageAge;
import ru.skypro.school.record.StudentQuantity;

import java.util.Collection;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Collection<Student> findByAge(Integer age);

    Collection<Student> findByAgeBetween(Integer min, Integer max);

    @Query(value = "SELECT COUNT(*) as studentQuantity FROM students", nativeQuery = true)
    StudentQuantity getStudentQuantity();

    @Query(value = "SELECT AVG(age) as studentAverageAge FROM students", nativeQuery = true)
    StudentAverageAge getStudentAverageAge();

    @Query(value = "SELECT * FROM students ORDER BY id DESC LIMIT :size ", nativeQuery = true)
    Collection<Student> getLastStudents(Integer size);
}
