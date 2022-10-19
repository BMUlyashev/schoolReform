package ru.skypro.school.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.skypro.school.entity.Avatar;
import ru.skypro.school.entity.Faculty;
import ru.skypro.school.entity.Student;
import ru.skypro.school.record.AvatarRecord;
import ru.skypro.school.record.FacultyRecord;
import ru.skypro.school.record.StudentRecord;

@Component
public class RecordMapper {

    public StudentRecord toRecord(Student student) {
        StudentRecord studentRecord = new StudentRecord();
        studentRecord.setId(student.getId());
        studentRecord.setName(student.getName());
        studentRecord.setAge(student.getAge());

        if (student.getFaculty() != null) {
            studentRecord.setFacultyRecord(toRecord(student.getFaculty()));
        }

        if (student.getAvatar() != null) {
            studentRecord.setAvatarRecord(toRecord(student.getAvatar()));
        }

        return studentRecord;
    }

    public Student toEntity(StudentRecord studentRecord) {
        Student student = new Student();
        student.setName(studentRecord.getName());
        student.setAge(studentRecord.getAge());
        return student;
    }

    public FacultyRecord toRecord(Faculty faculty) {
        FacultyRecord facultyRecord = new FacultyRecord();

        facultyRecord.setId(faculty.getId());
        facultyRecord.setName(faculty.getName());
        facultyRecord.setColor(faculty.getColor());

        return facultyRecord;
    }

    public Faculty toEntity(FacultyRecord facultyRecord) {
        Faculty faculty = new Faculty();
        faculty.setName(facultyRecord.getName());
        faculty.setColor(facultyRecord.getColor());
        return faculty;
    }

    public AvatarRecord toRecord(Avatar avatar) {
        AvatarRecord avatarRecord = new AvatarRecord();
        avatarRecord.setId(avatar.getId());
        avatarRecord.setMediaType(avatar.getMediaType());
        avatarRecord.setUrl("http://localhost:8080/avatars/" + avatar.getId() + "/from-db");
        return avatarRecord;
    }
}
