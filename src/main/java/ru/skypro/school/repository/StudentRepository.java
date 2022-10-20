package ru.skypro.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skypro.school.entity.Student;

import java.util.Collection;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Collection<Student> findByAge(Integer age);

    Collection<Student> findByAgeBetween(Integer min, Integer max);

    @Query(value = "SELECT COUNT(*) FROM students", nativeQuery = true)
    Integer getStudentQuantity();

    @Query(value = "SELECT AVG(age) FROM students", nativeQuery = true)
    Double getStudentAverageAge();

    @Query(value = "SELECT * FROM students ORDER BY id DESC LIMIT :size ", nativeQuery = true)
    Collection<Student> getLastStudents(Integer size);
}
