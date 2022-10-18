package ru.skypro.school.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.school.component.RecordMapper;
import ru.skypro.school.entity.Student;
import ru.skypro.school.exception.StudentNotFoundException;
import ru.skypro.school.record.StudentRecord;
import ru.skypro.school.repository.StudentRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    StudentRepository studentRepository;

    @Spy
    RecordMapper recordMapper = new RecordMapper();

    @InjectMocks
    StudentService studentService;

    @Test
    public void create() {
        Student student = createStudent(1, "test", 18);
        StudentRecord studentRecord = createStudentRecord(1, "test", 18);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        assertThat(studentService.create(studentRecord)).isEqualTo(studentRecord);
    }

    @Test
    public void read() {
        Student student = createStudent(1, "test", 18);
        StudentRecord studentRecord = createStudentRecord(1, "test", 18);

        when(studentRepository.findById(any())).thenReturn(Optional.of(student));

        assertThat(studentService.read(1L)).isEqualTo(studentRecord);
    }

    @Test
    public void readNotFound() {
        Student student = createStudent(1, "test", 18);
        StudentRecord studentRecord = createStudentRecord(1, "test", 18);

        when(studentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> studentService.read(2L)).isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    public void update() {
        StudentRecord studentRecord = createStudentRecord(1, "testRecord", 20);
        Student student = createStudent(1, "test", 18);
        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        assertThat(studentService.update(1L, studentRecord)).isEqualTo(studentRecord);
    }

    @Test
    public void updateNotFound() {
        StudentRecord studentRecord = createStudentRecord(1, "testRecord", 20);
        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.update(1L, studentRecord)).isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    public void delete() {
        Student student = createStudent(1, "test", 18);
        StudentRecord studentRecord = createStudentRecord(1, "test", 18);
        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(student));
        doNothing().when(studentRepository).delete(any(Student.class));

        assertThat(studentService.delete(1L)).isEqualTo(studentRecord);
        verify(studentRepository, times(1)).delete(any(Student.class));
    }

    @Test
    public void deleteNotFound() {
        StudentRecord studentRecord = createStudentRecord(1, "testRecord", 20);
        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.delete(1L)).isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    public void findByAgeBetween() {
        List<Student> students = List.of(
                createStudent(1, "1", 18),
                createStudent(3, "3", 19),
                createStudent(5, "5", 20)
        );

        int minAge = 18;
        int maxAge = 20;
        int minAgeFail = 10;
        int maxAgeFail = 13;

        when(studentRepository.findByAgeBetween(any(), any()))
                .thenReturn(students)
                .thenReturn(Collections.emptyList());

        assertThat(studentService.findByAgeBetween(minAge, maxAge))
                .filteredOn(student -> student.getAge() >= minAge && student.getAge() <= maxAge)
                .hasSize(3);
        assertThat(studentService.findByAgeBetween(minAgeFail, maxAgeFail))
                .hasSize(0);
    }

    @Test
    public void findByAge() {
        List<Student> students = List.of(
                createStudent(1, "1", 18),
                createStudent(3, "3", 18),
                createStudent(5, "5", 18)
        );


        when(studentRepository.findByAge(any()))
                .thenReturn(students)
                .thenReturn(Collections.emptyList());

        assertThat(studentService.findByAge(18))
                .filteredOn(studentRecord -> studentRecord.getAge() == 18)
                .hasSize(3);
        assertThat(studentService.findByAge(25))
                .filteredOn(studentRecord -> studentRecord.getAge() == 25)
                .hasSize(0);
    }

    private Student createStudent(long id, String name, int age) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setAge(age);
        return student;
    }

    private StudentRecord createStudentRecord(long id, String name, int age) {
        StudentRecord studentRecord = new StudentRecord();
        studentRecord.setId(id);
        studentRecord.setName(name);
        studentRecord.setAge(age);
        return studentRecord;
    }
}
