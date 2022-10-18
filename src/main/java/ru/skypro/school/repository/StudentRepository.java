package ru.skypro.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skypro.school.entity.Faculty;
import ru.skypro.school.entity.Student;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Collection<Student> findByAge(Integer age);

    Collection<Student> findByAgeBetween(Integer min, Integer max);

    @Query(value = "SELECT f FROM Faculty f, Student s WHERE f.id = s.faculty.id AND s.id = :id")
    Faculty findStudentFaculty(Long id);
}
