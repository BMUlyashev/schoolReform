package ru.skypro.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.skypro.school.component.RecordMapper;
import ru.skypro.school.entity.Faculty;
import ru.skypro.school.exception.FacultyNotFoundException;
import ru.skypro.school.record.FacultyRecord;
import ru.skypro.school.record.StudentRecord;
import ru.skypro.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final RecordMapper recordMapper;

    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);


    public FacultyService(FacultyRepository facultyRepository, RecordMapper recordMapper) {
        this.facultyRepository = facultyRepository;
        this.recordMapper = recordMapper;
    }

    public FacultyRecord create(FacultyRecord facultyRecord) {
        logger.info("Was invoked method to create faculty");
        return recordMapper.toRecord(facultyRepository.save(recordMapper.toEntity(facultyRecord)));
    }

    public FacultyRecord read(Long id) {
        logger.info("Was invoked method to find faculty");
        return recordMapper.toRecord(facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id)));
    }

    public FacultyRecord update(Long id, FacultyRecord facultyRecord) {
        logger.info("Was invoked method to update faculty");
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id));
        logger.debug("Was found {}", faculty);
        faculty.setName(facultyRecord.getName());
        faculty.setColor(facultyRecord.getColor());
        return recordMapper.toRecord(facultyRepository.save(faculty));
    }

    public FacultyRecord delete(Long id) {
        logger.info("Was invoked method to delete faculty");
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id));
        facultyRepository.delete(faculty);
        return recordMapper.toRecord(faculty);
    }

    public Collection<FacultyRecord> findByColor(String color) {
        logger.info("Was invoked method to find faculty by color = {}", color);
        return facultyRepository.findByColor(color).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public Collection<FacultyRecord> findByFilterString(String filterString) {
        logger.info("Was invoked method to find faculty by filter = {}", filterString);
        return facultyRepository.findByNameLikeIgnoreCaseOrColorLikeIgnoreCase(filterString, filterString).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public Collection<StudentRecord> getStudentsByFaculty(Long id) {
        logger.info("Was invoked method to find students in faculty with id = {}", id);
        return facultyRepository.findById(id)
                .map(Faculty::getStudents)
                .map(s -> s.stream()
                        .map(recordMapper::toRecord)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new FacultyNotFoundException(id));
    }

    public String getFacultyLongestName() {
        logger.info("Was invoked method to find faculty with the longest name");
        return facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .max(Comparator.comparing(String::length))
                .orElse("");
    }
}
