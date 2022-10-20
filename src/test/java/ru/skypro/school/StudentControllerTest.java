package ru.skypro.school;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.skypro.school.controller.StudentController;
import ru.skypro.school.entity.Faculty;
import ru.skypro.school.entity.Student;
import ru.skypro.school.record.FacultyRecord;
import ru.skypro.school.record.StudentRecord;
import ru.skypro.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    StudentController studentController;

    @MockBean
    StudentRepository studentRepository;


    @Test
    public void create() {
        StudentRecord studentRecord = createStudentRecord(1, "test", 18);
        Student student = createStudent(1, "test", 18);

        when(studentRepository.save(any(Student.class))).thenReturn(student);

        ResponseEntity<StudentRecord> recordResponseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/students",
                studentRecord, StudentRecord.class);
        assertThat(recordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(recordResponseEntity.getBody().getId()).isEqualTo(student.getId());
        assertThat(recordResponseEntity.getBody().getAge()).isEqualTo(student.getAge());
    }

    @Test
    public void read() {
        StudentRecord studentRecord = createStudentRecord(1, "test", 18);
        Student student = createStudent(1, "test", 18);

        when(studentRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(student))
                .thenReturn(Optional.empty());

        ResponseEntity<StudentRecord> recordResponseEntity = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/students/1", StudentRecord.class);

        assertThat(recordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(recordResponseEntity.getBody().getId()).isEqualTo(student.getId());
        assertThat(recordResponseEntity.getBody().getAge()).isEqualTo(student.getAge());


        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/students/2", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isEqualTo("Студент с id = 2 не найден!");
    }

    @Test
    public void update() {
        StudentRecord studentRecord = createStudentRecord(1, "test", 18);
        Student student = createStudent(1, "test", 18);
        Student studentNew = createStudent(1, "testNew", 20);
        StudentRecord studentRecordNew = createStudentRecord(1, "testNew", 20);

        when(studentRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(student))
                .thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(studentNew);


        ResponseEntity<StudentRecord> recordResponseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/students/1",
                HttpMethod.PUT,
                new HttpEntity<>(studentRecordNew),
                StudentRecord.class);

        assertThat(recordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(recordResponseEntity.getBody().getAge()).isEqualTo(studentRecordNew.getAge());
        assertThat(recordResponseEntity.getBody().getName()).isEqualTo(studentRecordNew.getName());

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/students/2",
                HttpMethod.PUT,
                new HttpEntity<>(studentRecordNew),
                String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isEqualTo("Студент с id = 2 не найден!");
    }

    @Test
    public void delete() {
        StudentRecord studentRecord = createStudentRecord(1, "test", 18);
        Student student = createStudent(1, "test", 18);

        when(studentRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(student))
                .thenReturn(Optional.empty());

        doNothing().when(studentRepository).delete(any(Student.class));

//        HttpEntity<StudentRecord> entity = new HttpEntity<>(studentRecord);

        ResponseEntity<StudentRecord> responseEntity = testRestTemplate.exchange(
                "http://localhost:" + port + "/students/1",
                HttpMethod.DELETE, new HttpEntity<>(StudentRecord.class), StudentRecord.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getId()).isEqualTo(studentRecord.getId());

        ResponseEntity<String> responseEntityBad = testRestTemplate.exchange(
                "http://localhost:" + port + "/students/2",
                HttpMethod.DELETE, new HttpEntity<>(StudentRecord.class), String.class);

        assertThat(responseEntityBad.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntityBad.getBody()).isEqualTo("Студент с id = 2 не найден!");
    }

    @Test
    public void findByAge() {

        List<Student> students = List.of(
                createStudent(1, "1", 18),
                createStudent(3, "3", 18)
        );

        List<StudentRecord> studentsRecords = List.of(
                createStudentRecord(1, "1", 18),
                createStudentRecord(3, "3", 18)
        );

        when(studentRepository.findByAge(any(Integer.class))).thenReturn(students);

        ResponseEntity<Collection<StudentRecord>> response = testRestTemplate.exchange("http://localhost:" + port + "/students?age={age}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<StudentRecord>>() {
                }
                , 18);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Collection<StudentRecord> actual = response.getBody();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(studentsRecords);
    }

    @Test
    public void findByBetweenAge() {

        List<Student> students = List.of(
                createStudent(1, "1", 18),
                createStudent(3, "3", 20)
        );

        List<StudentRecord> studentsRecords = List.of(
                createStudentRecord(1, "1", 18),
                createStudentRecord(3, "3", 20)
        );

        when(studentRepository.findByAgeBetween(any(Integer.class), any(Integer.class))).thenReturn(students);

        ResponseEntity<Collection<StudentRecord>> response = testRestTemplate.exchange("http://localhost:" + port + "/students?minAge={min}&maxAge={max}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }, 18, 20);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Collection<StudentRecord> actual = response.getBody();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(studentsRecords);
    }

    @Test
    public void findByFaculty() {
        Student student = createStudent(1, "test", 18);
        Faculty faculty = createFaculty(1, "1", "red");
        StudentRecord studentRecord = createStudentRecord(1, "test", 18);
        FacultyRecord facultyRecord = createFacultyRecord(1, "1", "red");
        student.setFaculty(faculty);
        studentRecord.setFacultyRecord(facultyRecord);
        when(studentRepository.findById(any()))
                .thenReturn(Optional.of(student))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(student));

        ResponseEntity<FacultyRecord> response = testRestTemplate.getForEntity("http://localhost:" + port + "/students/1/faculty",
                FacultyRecord.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo(facultyRecord.getName());

        ResponseEntity<String> responseBad = testRestTemplate.getForEntity("http://localhost:" + port + "/students/1/faculty",
                String.class);

        assertThat(responseBad.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseBad.getBody()).isEqualTo("Студент с id = 1 не найден!");

        student.setFaculty(null);

        responseBad = testRestTemplate.getForEntity("http://localhost:" + port + "/students/1/faculty",
                String.class);

        assertThat(responseBad.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseBad.getBody()).isEqualTo("У студента с id = 1 не найден факультет!");
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

    private Faculty createFaculty(long id, String name, String color) {
        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);
        return faculty;
    }

    private FacultyRecord createFacultyRecord(long id, String name, String color) {
        FacultyRecord facultyRecord = new FacultyRecord();
        facultyRecord.setId(id);
        facultyRecord.setName(name);
        facultyRecord.setColor(color);
        return facultyRecord;
    }
}
