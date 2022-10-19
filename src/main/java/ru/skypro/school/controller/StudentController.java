package ru.skypro.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.school.entity.Faculty;
import ru.skypro.school.record.FacultyRecord;
import ru.skypro.school.record.StudentRecord;
import ru.skypro.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public StudentRecord create(@RequestBody StudentRecord studentRecord) {
        return studentService.create(studentRecord);
    }

    @GetMapping("{id}")
    public StudentRecord read(@PathVariable Long id) {
        return studentService.read(id);
    }

    @PutMapping("{id}")
    public StudentRecord update(@PathVariable Long id,
                                                @RequestBody StudentRecord studentRecord) {
        return studentService.update(id, studentRecord);
    }

    @DeleteMapping("{id}")
    public StudentRecord delete(@PathVariable Long id) {
        return studentService.delete(id);
    }

    @GetMapping(params = "age")
    public Collection<StudentRecord> findByAge(@RequestParam int age) {
        return studentService.findByAge(age);
    }

    @GetMapping(params = {"minAge", "maxAge"})
    public Collection<StudentRecord> findByAgeBetween(@RequestParam int minAge, @RequestParam int maxAge) {
        return studentService.findByAgeBetween(minAge, maxAge);
    }

    @GetMapping("/{id}/faculty")
    public ResponseEntity<Faculty> findStudentFaculty(@PathVariable Long id) {
        Faculty faculty = studentService.findStudentFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
        //return studentService.findStudentFaculty(id);
    }
}
