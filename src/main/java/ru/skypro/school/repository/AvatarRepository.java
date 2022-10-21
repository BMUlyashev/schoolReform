package ru.skypro.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.school.entity.Avatar;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {
}
