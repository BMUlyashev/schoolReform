package ru.skypro.school.service;

import org.springframework.stereotype.Service;
import ru.skypro.school.component.RecordMapper;
import ru.skypro.school.entity.Faculty;
import ru.skypro.school.exception.FacultyNotFoundException;
import ru.skypro.school.record.FacultyRecord;
import ru.skypro.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final RecordMapper recordMapper;

    public FacultyService(FacultyRepository facultyRepository, RecordMapper recordMapper) {
        this.facultyRepository = facultyRepository;
        this.recordMapper = recordMapper;
    }

    public FacultyRecord create(FacultyRecord facultyRecord) {
        return recordMapper.toRecord(facultyRepository.save(recordMapper.toEntity(facultyRecord)));
    }

    public FacultyRecord read(Long id) {
        return recordMapper.toRecord(facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id)));
    }

    public FacultyRecord update(Long id, FacultyRecord facultyRecord) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id));
        faculty.setName(facultyRecord.getName());
        faculty.setColor(facultyRecord.getColor());
        return recordMapper.toRecord(facultyRepository.save(faculty));
    }

    public FacultyRecord delete(Long id) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id));
        facultyRepository.delete(faculty);
        return recordMapper.toRecord(faculty);
    }

    public Collection<FacultyRecord> findByColor(String color) {
        return facultyRepository.findByColor(color).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public Collection<FacultyRecord> findByFilterString(String filterString) {
        return facultyRepository.findByNameLikeIgnoreCaseOrColorLikeIgnoreCase(filterString, filterString).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }
}
