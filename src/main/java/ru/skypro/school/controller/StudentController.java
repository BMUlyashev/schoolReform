package ru.skypro.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<StudentRecord> create(@RequestBody StudentRecord studentRecord) {
        return ResponseEntity.ok(studentService.create(studentRecord));
    }

    @GetMapping("{id}")
    public ResponseEntity<StudentRecord> read(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.read(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<StudentRecord> update(@PathVariable Long id,
                                                @RequestBody StudentRecord studentRecord) {
        return ResponseEntity.ok(studentService.update(id, studentRecord));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<StudentRecord> delete(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.delete(id));
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
    public FacultyRecord findStudentFaculty(@PathVariable Long id) {
        return studentService.findStudentFaculty(id);
    }
}
