package ru.skypro.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(StudentService.class);

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
        logger.info("Was invoked method to create student");
        return recordMapper.toRecord(studentRepository.save(recordMapper.toEntity(studentRecord)));
    }

    public StudentRecord read(Long id) {
        logger.info("Was invoked method to find student by id");
        return recordMapper.toRecord(studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id)));
    }

    public StudentRecord update(Long id, StudentRecord studentRecord) {
        logger.info("Was invoked method to update student");
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        logger.debug("Was found {}", student);
        student.setName(studentRecord.getName());
        student.setAge(studentRecord.getAge());
        logger.debug("New values for student: age = {}, name = {}", student.getAge(), student.getName());
        return recordMapper.toRecord(studentRepository.save(student));
    }

    public StudentRecord delete(Long id) {
        logger.info("Was invoked method to delete student");
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.delete(student);
        return recordMapper.toRecord(student);
    }

    public Collection<StudentRecord> findByAge(Integer age) {
        logger.info("Was invoked method to find student with age =  {}", age);
        return studentRepository.findByAge(age).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public Collection<StudentRecord> findByAgeBetween(Integer min, Integer max) {
        logger.info("Was invoked method to find students with age between ({}, {})", min, max);
        return studentRepository.findByAgeBetween(min, max).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }

    public FacultyRecord findStudentFaculty(Long id) {
        logger.info("Was invoked method to find faculty of student с id = {}", id);
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        return recordMapper.toRecord(Optional.ofNullable(student.getFaculty())
                .orElseThrow(() -> new StudentFacultyNotFoundException(id)));

    }

    public StudentRecord updateAvatar(Long id, Long avatarId) {
        logger.info("Was invoked method to set avatar for student");
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        Avatar avatar = avatarRepository.findById(avatarId).orElseThrow(() -> new AvatarNotFoundException(avatarId));

        student.setAvatar(avatar);

        return recordMapper.toRecord(studentRepository.save(student));
    }

    public StudentRecord updateFaculty(Long id, Long facultyId) {
        logger.info("Was invoked method to set faculty for student");
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        Faculty faculty = facultyRepository.findById(facultyId).orElseThrow(() -> new FacultyNotFoundException(facultyId));

        student.setFaculty(faculty);
        return recordMapper.toRecord(studentRepository.save(student));
    }

    public StudentQuantity getStudentQuantity() {
        logger.info("Was invoked method to get quantity students");
        return studentRepository.getStudentQuantity();
    }

    public StudentAverageAge getStudentAverageAge() {
        logger.info("Was invoked method to get average age of students");
        return studentRepository.getStudentAverageAge();
    }

    public Collection<StudentRecord> getLastAddedStudents(Integer size) {
        logger.info("Was invoked method to get {} last added students", size);
        return studentRepository.getLastStudents(size).stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }
}
