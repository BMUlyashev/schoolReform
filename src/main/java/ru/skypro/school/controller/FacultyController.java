package ru.skypro.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.skypro.school.record.FacultyRecord;
import ru.skypro.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("/faculties")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public FacultyRecord create(@RequestBody FacultyRecord facultyRecord) {
        return facultyService.create(facultyRecord);
    }

    @GetMapping("{id}")
    public FacultyRecord read(@PathVariable Long id) {
        return facultyService.read(id);
    }

    @PutMapping("{id}")
    public FacultyRecord update(@PathVariable Long id, @RequestBody FacultyRecord facultyRecord) {
        return facultyService.update(id, facultyRecord);
    }

    @DeleteMapping("{id}")
    public FacultyRecord delete(@PathVariable Long id) {
        return facultyService.delete(id);
    }

    @GetMapping(params = "color")
    public Collection<FacultyRecord> findByColor(@RequestParam String color) {
        return facultyService.findByColor(color);
    }

    @GetMapping(params = "filterString")
    public Collection<FacultyRecord> findByFilterString(@RequestParam String filterString) {
        return facultyService.findByFilterString(filterString);
    }
}
