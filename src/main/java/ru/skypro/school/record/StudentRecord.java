package ru.skypro.school.record;

import java.util.Objects;

public class StudentRecord {
    private Long id;
    private String name;
    private int age;

    private FacultyRecord facultyRecord;

    public FacultyRecord getFacultyRecord() {
        return facultyRecord;
    }

    public void setFacultyRecord(FacultyRecord facultyRecord) {
        this.facultyRecord = facultyRecord;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentRecord that = (StudentRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

