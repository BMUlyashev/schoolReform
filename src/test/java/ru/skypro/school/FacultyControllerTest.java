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
import ru.skypro.school.record.FacultyRecord;
import ru.skypro.school.repository.FacultyRepository;
import ru.skypro.school.service.FacultyService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(status().isNotFound());

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
