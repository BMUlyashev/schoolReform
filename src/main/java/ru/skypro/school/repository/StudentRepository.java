package ru.skypro.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.school.entity.Student;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Collection<Student> findByAge(Integer age);
}
