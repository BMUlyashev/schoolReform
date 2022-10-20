package ru.skypro.school;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skypro.school.component.RecordMapper;
import ru.skypro.school.controller.FacultyController;
import ru.skypro.school.entity.Faculty;
import ru.skypro.school.entity.Student;
import ru.skypro.school.record.FacultyRecord;
import ru.skypro.school.record.StudentRecord;
import ru.skypro.school.repository.FacultyRepository;
import ru.skypro.school.service.FacultyService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FacultyController.class)
@ExtendWith(MockitoExtension.class)
public class FacultyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    private FacultyService facultyService;

    @SpyBean
    private RecordMapper recordMapper;

    @Test
    public void create() throws Exception {
        Faculty faculty = createFaculty(1, "test", "red");
        FacultyRecord facultyRecord = createFacultyRecord(1, "test", "red");
        JSONObject json = new JSONObject();
        json.put("name", facultyRecord.getName());
        json.put("color", facultyRecord.getColor());

        when(facultyRepository.save(any())).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculties")
                        .content(json.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));

    }

    @Test
    public void read() throws Exception {
        Faculty faculty = createFaculty(1, "test", "black");

        when(facultyRepository.findById(any()))
                .thenReturn(Optional.of(faculty))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculties/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculties/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void editFaculty() throws Exception {
        Faculty faculty = createFaculty(1, "test", "black");
        Faculty facultyNew = createFaculty(1, "test2", "red");
        FacultyRecord facultyRecord = createFacultyRecord(1, "test2", "red");

        JSONObject json = new JSONObject();
        json.put("name", facultyNew.getName());
        json.put("color", facultyNew.getColor());

        when(facultyRepository.findById(any()))
                .thenReturn(Optional.of(faculty))
                .thenReturn(Optional.empty());
        when(facultyRepository.save(any(Faculty.class)))
                .thenReturn(facultyNew);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculties/1")
                        .content(json.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(facultyRecord.getId()))
                .andExpect(jsonPath("$.name").value(facultyRecord.getName()))
                .andExpect(jsonPath("$.color").value(facultyRecord.getColor()));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculties/1")
                        .content(json.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
                            .isEqualTo("Факультет с id = 1 не найден!");
                });
    }

    @Test
    public void delete() throws Exception {
        Faculty faculty = createFaculty(1, "test", "black");
        FacultyRecord facultyRecord = createFacultyRecord(1, "test", "black");

        when(facultyRepository.findById(any()))
                .thenReturn(Optional.of(faculty))
                .thenReturn(Optional.empty());
        doNothing().when(facultyRepository).deleteById(any());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(facultyRecord.getName()))
                .andExpect(jsonPath("$.color").value(facultyRecord.getColor()))
        ;

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
                            .isEqualTo("Факультет с id = 1 не найден!");
                });
    }

    @Test
    public void findByFilterString() throws Exception {
        List<Faculty> faculties = List.of(
                createFaculty(1, "1", "red"),
                createFaculty(3, "3", "red")
        );

        when(facultyRepository.findByNameLikeIgnoreCaseOrColorLikeIgnoreCase(any(), any()))
                .thenReturn(faculties)
                .thenReturn(Collections.emptyList());


        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculties?filterString=red")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].color").value(containsInAnyOrder("red", "red")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculties?filterString=")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void findStudent() throws Exception {
        List<Student> students = List.of(
                createStudent(1, "1", 17),
                createStudent(3, "3", 18)
        );

        Faculty faculty = createFaculty(1, "test", "red");
        faculty.setStudents(students);

        when(facultyRepository.findById(any())).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculties/{id}/students", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").value(containsInAnyOrder("1", "3")));
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
