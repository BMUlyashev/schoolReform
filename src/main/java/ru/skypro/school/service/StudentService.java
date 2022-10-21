package ru.skypro.school.service;

import org.springframework.stereotype.Service;
import ru.skypro.school.component.RecordMapper;
import ru.skypro.school.entity.Avatar;
import ru.skypro.school.entity.Faculty;
import ru.skypro.school.entity.Student;
import ru.skypro.school.exception.AvatarNotFoundException;
import ru.skypro.school.exception.FacultyNotFoundException;
import ru.skypro.school.exception.StudentFacultyNotFoundException;
import ru.skypro.school.exception.StudentNotFoundException;
import ru.skypro.school.record.FacultyRecord;
import ru.skypro.school.record.StudentAverageAge;
import ru.skypro.school.record.StudentQuantity;
import ru.skypro.school.record.StudentRecord;
import ru.skypro.school.repository.AvatarRepository;
import ru.skypro.school.repository.FacultyRepository;
import ru.skypro.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    private final FacultyRepository facultyRepository;
    private final AvatarRepository avatarRepository;
    private final RecordMapper recordMapper;

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository, AvatarRepository avatarRepository, RecordMapper recordMapper) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.avatarRepository = avatarRepository;
        this.recordMapper = recordMapper;
    }

    public StudentRecord create(StudentRecord studentRecord) {
        return recordMapper.toRecord(studentRepository.save(recordMapper.toEntity(studentRecord)));
    }

    public StudentRecord read(Long id) {
        return recordMapper.toRecord(studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id)));
    }

    public StudentRecord update(Long id, StudentRecord studentRecord) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        student.setName(studentRecord.getName());
        student.setAge(studentRecord.getAge());

        return recordMapper.toRecord(studentRepository.save(student));
    }

    public StudentRecord delete(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.delete(student);
        return recordMapper.toRecord(student);
    }

    public Collection<StudentRecord> findByAge(Integer age) {
        return studentRepository.findByAge(age).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public Collection<StudentRecord> findByAgeBetween(Integer min, Integer max) {
        return studentRepository.findByAgeBetween(min, max).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public FacultyRecord findStudentFaculty(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        return recordMapper.toRecord(Optional.ofNullable(student.getFaculty())
                .orElseThrow(() -> new StudentFacultyNotFoundException(id)));

    }

    public StudentRecord updateAvatar(Long id, Long avatarId) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        Avatar avatar = avatarRepository.findById(avatarId).orElseThrow(() -> new AvatarNotFoundException(avatarId));

        student.setAvatar(avatar);

        return recordMapper.toRecord(studentRepository.save(student));
    }

    public StudentRecord updateFaculty(Long id, Long facultyId) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        Faculty faculty = facultyRepository.findById(facultyId).orElseThrow(() -> new FacultyNotFoundException(facultyId));

        student.setFaculty(faculty);
        return recordMapper.toRecord(studentRepository.save(student));
    }

    public StudentQuantity getStudentQuantity() {
        return studentRepository.getStudentQuantity();
    }

    public StudentAverageAge getStudentAverageAge() {
        return studentRepository.getStudentAverageAge();
    }

    public Collection<StudentRecord> getLastAddedStudents(Integer size) {
        return studentRepository.getLastStudents(size).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }
}
