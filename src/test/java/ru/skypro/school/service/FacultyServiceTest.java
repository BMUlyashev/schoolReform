package ru.skypro.school.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.school.component.RecordMapper;
import ru.skypro.school.entity.Faculty;
import ru.skypro.school.entity.Student;
import ru.skypro.school.exception.FacultyNotFoundException;
import ru.skypro.school.record.FacultyRecord;
import ru.skypro.school.record.StudentRecord;
import ru.skypro.school.repository.FacultyRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class FacultyServiceTest {

    @Mock
    FacultyRepository facultyRepository;

    @Spy
    RecordMapper recordMapper = new RecordMapper();

    @InjectMocks
    FacultyService facultyService;

    @Test
    public void create() {
        FacultyRecord facultyRecord = createFacultyRecord(1, "test", "color");
        Faculty faculty = createFaculty(1, "test", "color");
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        assertThat(facultyService.create(facultyRecord)).isEqualTo(facultyRecord);
    }

    @Test
    public void read() {
        Faculty faculty = createFaculty(1, "test", "color");
        FacultyRecord facultyRecord = createFacultyRecord(1, "test", "color");

        when(facultyRepository.findById(any())).thenReturn(Optional.of(faculty));

        assertThat(facultyService.read(1L)).isEqualTo(facultyRecord);
    }

    @Test
    public void readNotFound() {
        Faculty faculty = createFaculty(1, "test", "color");
        FacultyRecord facultyRecord = createFacultyRecord(1, "test", "color");

        when(facultyRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> facultyService.read(2L)).isInstanceOf(FacultyNotFoundException.class);
    }

    @Test
    public void update() {
        Faculty faculty = createFaculty(1, "test", "color");
        FacultyRecord facultyRecord = createFacultyRecord(1, "test", "color");

        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        assertThat(facultyService.update(1L, facultyRecord)).isEqualTo(facultyRecord);
    }

    @Test
    public void updateNotFound() {
        FacultyRecord facultyRecord = createFacultyRecord(1, "test", "color");
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facultyService.update(1L, facultyRecord)).isInstanceOf(FacultyNotFoundException.class);
    }

    @Test
    public void delete() {
        Faculty faculty = createFaculty(1, "test", "color");
        FacultyRecord facultyRecord = createFacultyRecord(1, "test", "color");
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(faculty));
        doNothing().when(facultyRepository).delete(any(Faculty.class));

        assertThat(facultyService.delete(1L)).isEqualTo(facultyRecord);
        verify(facultyRepository, times(1)).delete(any(Faculty.class));
    }

    @Test
    public void deleteNotFound() {
        FacultyRecord facultyRecord = createFacultyRecord(1, "test", "color");
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facultyService.delete(1L)).isInstanceOf(FacultyNotFoundException.class);
    }

    @Test
    public void findByColor() {
        List<Faculty> faculties = List.of(
                createFaculty(1, "1", "red"),
                createFaculty(3, "3", "red"),
                createFaculty(5, "5", "red")
        );

        when(facultyRepository.findByColor(any()))
                .thenReturn(faculties)
                .thenReturn(Collections.emptyList());

        assertThat(facultyService.findByColor(any()))
                .filteredOn(faculty -> faculty.getColor().equals("red"))
                .hasSize(3);
        assertThat(facultyService.findByColor(any()))
                .filteredOn(faculty -> faculty.getColor().equals("blue"))
                .hasSize(0);
    }

    @Test
    public void getByFilterString() {
        List<Faculty> facultiesEqualColor = List.of(
                createFaculty(1, "1", "red"),
                createFaculty(3, "3", "red")
        );
        List<Faculty> facultiesEqualName = List.of(
                createFaculty(1, "1", "red"),
                createFaculty(3, "1", "blue")
        );

        List<FacultyRecord> facultiesRecordEqualColor = List.of(
                createFacultyRecord(1, "1", "red"),
                createFacultyRecord(3, "3", "red")
        );
        List<FacultyRecord> facultiesRecordEqualName = List.of(
                createFacultyRecord(1, "1", "red"),
                createFacultyRecord(3, "1", "blue")
        );

        when(facultyRepository.findByNameLikeIgnoreCaseOrColorLikeIgnoreCase("red", "red"))
                .thenReturn(facultiesEqualColor);


        when(facultyRepository.findByNameLikeIgnoreCaseOrColorLikeIgnoreCase("1", "1"))
                .thenReturn(facultiesEqualName);

        assertThat(facultyService.findByFilterString("red"))
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(facultiesRecordEqualColor);
        assertThat(facultyService.findByFilterString("1"))
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(facultiesRecordEqualName);
    }

    @Test
    public void findStudent() {
        Faculty faculty = createFaculty(1, "test", "color");
        List<Student> students = List.of(
                createStudent(1, "1", 18),
                createStudent(3, "3", 19)
        );

        List<StudentRecord> studentsRecords = List.of(
                createStudentRecord(1, "1", 18),
                createStudentRecord(3, "3", 19)
        );

        faculty.setStudents(students);

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        assertThat(facultyService.getStudentsByFaculty(1L))
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(studentsRecords);
    }

    @Test
    public void findStudentFacultyNotFound() {
        when(facultyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facultyService.getStudentsByFaculty(1L)).isInstanceOf(FacultyNotFoundException.class);
    }

    @Test
    public void getFacultyLongestName() {
        List<Faculty> faculties = List.of(
                createFaculty(1, "3", "red"),
                createFaculty(3, "12", "red"),
                createFaculty(5, "5", "red")
        );
        when(facultyRepository.findAll()).thenReturn(faculties);
        assertThat(facultyService.getFacultyLongestName())
                .isEqualTo("12");

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
