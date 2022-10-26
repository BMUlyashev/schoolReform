package ru.skypro.school.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<String> handleStudentNotFoundException(StudentNotFoundException e) {
        logger.error("Студент с id = {} не найден!", e.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(String.format("Студент с id = %d не найден!", e.getId()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(FacultyNotFoundException.class)
    public ResponseEntity<String> handleFacultyNotFoundException(FacultyNotFoundException e) {
        logger.error("Факультет с id = {} не найден!", e.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(String.format("Факультет с id = %d не найден!", e.getId()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(StudentFacultyNotFoundException.class)
    public ResponseEntity<String> handleStudentFacultyNotFoundException(StudentFacultyNotFoundException e) {
        logger.error("У студента с id = {} не найден факультет!", e.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(String.format("У студента с id = %d не найден факультет!", e.getId()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AvatarNotFoundException.class)
    public ResponseEntity<String> handleAvatarNotFoundException(AvatarNotFoundException e) {
        logger.error("Аватар с id = {} не найден!", e.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(String.format("Аватар с id = %d не найден!", e.getId()));
    }
}
