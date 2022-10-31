package ru.skypro.school.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import ru.skypro.school.component.RecordMapper;
import ru.skypro.school.entity.Avatar;
import ru.skypro.school.entity.Faculty;
import ru.skypro.school.entity.Student;
import ru.skypro.school.exception.StudentFacultyNotFoundException;
import ru.skypro.school.exception.StudentNotFoundException;
import ru.skypro.school.record.*;
import ru.skypro.school.repository.AvatarRepository;
import ru.skypro.school.repository.FacultyRepository;
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

    @Mock
    FacultyRepository facultyRepository;

    @Mock
    AvatarRepository avatarRepository;

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

    @Test
    public void findFaculty() {
        Faculty faculty = createFaculty(1, "1", "red");
        Student student = createStudent(1, "test", 18);
        FacultyRecord facultyRecord = createFacultyRecord(1, "1", "red");
        student.setFaculty(faculty);

        when(studentRepository.findById(any()))
                .thenReturn(Optional.of(student));

        assertThat(studentService.findStudentFaculty(1L)).isEqualTo(facultyRecord);
    }

    @Test
    public void findFacultyStudentNotFound() {

        when(studentRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.findStudentFaculty(1L)).isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    public void findFacultyStudentFacultyNotFound() {
        Student student = createStudent(1, "test", 18);
        when(studentRepository.findById(any()))
                .thenReturn(Optional.of(student));

        assertThatThrownBy(() -> studentService.findStudentFaculty(1L)).isInstanceOf(StudentFacultyNotFoundException.class);
    }

    @Test
    public void updateAvatar() {
        Avatar avatar = createAvatar(2, "home", 4);
        Student student = createStudent(1, "test", 18);
        StudentRecord studentRecord = createStudentRecord(1, "test", 18);
        AvatarRecord avatarRecord = createAvatarRecord(2);

        studentRecord.setAvatarRecord(avatarRecord);
        student.setAvatar(avatar);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(avatarRepository.findById(2L)).thenReturn(Optional.of(avatar));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        assertThat(studentService.updateAvatar(1L, 2L)).isEqualTo(studentRecord);
    }

    @Test
    public void updateFaculty() {
        Student student = createStudent(1, "test", 18);
        Faculty faculty = createFaculty(1, "1", "red");
        StudentRecord studentRecord = createStudentRecord(1, "test", 18);
        FacultyRecord facultyRecord = createFacultyRecord(1, "1", "red");

        student.setFaculty(faculty);
        studentRecord.setFacultyRecord(facultyRecord);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        assertThat(studentService.updateFaculty(1L, 1L)).isEqualTo(studentRecord);
    }

    @Test
    public void getStudentQuantity() {
        StudentQuantity expected = new StudentQuantity() {
            @Override
            public int getStudentQuantity() {
                return 4;
            }
        };

        when(studentRepository.getStudentQuantity()).thenReturn(expected);

        assertThat(studentService.getStudentQuantity()).isEqualTo(expected);
    }

    @Test
    public void getStudentAverageAge() {
        StudentAverageAge expected = new StudentAverageAge() {
            @Override
            public double getStudentAverageAge() {
                return 10.5;
            }
        };
        when(studentRepository.getStudentAverageAge()).thenReturn(expected);

        assertThat(studentService.getStudentAverageAge()).isEqualTo(expected);
    }

    @Test
    public void getLastStudents() {
        List<Student> students = List.of(
                createStudent(7, "1", 18),
                createStudent(6, "3", 18)
        );

        when(studentRepository.getLastStudents(any())).thenReturn(students);

        assertThat(studentService.getLastAddedStudents(2)).hasSize(2);
    }

    @Test
    public void getNamesStudentsStartWith() {
        List<Student> students = List.of(
                createStudent(6, "Гермиона Грейнджер", 18),
                createStudent(7, "Гарри Поттер", 18),
                createStudent(8, "Драко Малфой", 18),
                createStudent(9, "Маркус Флинт", 18)
        );
        List<String> expected = List.of("ГАРРИ ПОТТЕР", "ГЕРМИОНА ГРЕЙНДЖЕР");

        when(studentRepository.findAll()).thenReturn(students);
        assertThat(studentService.getNamesStudentsStartWith("Г"))
                .hasSize(2)
                .containsExactlyElementsOf(expected);

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

    private Avatar createAvatar(long id, String filePath, long fileSize) {
        Avatar avatar = new Avatar();
        avatar.setId(id);
        avatar.setMediaType(MediaType.MULTIPART_FORM_DATA_VALUE);
        avatar.setFilePath(filePath);
        avatar.setFileSize(fileSize);
        avatar.setData(new byte[]{0, 1, 2, 3});
        return avatar;
    }

    private AvatarRecord createAvatarRecord(long id) {
        AvatarRecord avatarRecord = new AvatarRecord();
        avatarRecord.setId(id);
        avatarRecord.setMediaType(MediaType.MULTIPART_FORM_DATA_VALUE);
        avatarRecord.setUrl("http://localhost:8080/avatars/" + id + "/from-db");
        return avatarRecord;
    }
}
