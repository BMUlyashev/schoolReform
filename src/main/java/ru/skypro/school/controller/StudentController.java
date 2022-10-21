package ru.skypro.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.school.record.FacultyRecord;
import ru.skypro.school.record.StudentAverageAge;
import ru.skypro.school.record.StudentQuantity;
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
    public FacultyRecord findStudentFaculty(@PathVariable Long id) {
        return studentService.findStudentFaculty(id);
    }

    @PatchMapping("/{id}/faculty")
    public StudentRecord updateFaculty(@PathVariable Long id, @RequestParam Long facultyId) {
        return studentService.updateFaculty(id, facultyId);
    }

    @PatchMapping("/{id}/avatar")
    public StudentRecord updateAvatar(@PathVariable Long id, @RequestParam Long avatarId) {
        return studentService.updateAvatar(id, avatarId);
    }

    @GetMapping("/quantity")
    public ResponseEntity<StudentQuantity> getStudentQuantity() {
        return ResponseEntity.ok(studentService.getStudentQuantity());
    }

    @GetMapping("/age-average")
    public ResponseEntity<StudentAverageAge> getStudentAverageAge() {
        return ResponseEntity.ok(studentService.getStudentAverageAge());
    }

    @GetMapping(params = "lastAddedSize")
    public Collection<StudentRecord> getLastAddedStudents(@RequestParam Integer lastAddedSize) {
        return studentService.getLastAddedStudents(lastAddedSize);
    }
}
