package ru.skypro.school.exception;

public class StudentFacultyNotFoundException extends RuntimeException {
    private final long id;

    public StudentFacultyNotFoundException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
