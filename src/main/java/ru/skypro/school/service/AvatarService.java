package ru.skypro.school.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.school.component.RecordMapper;
import ru.skypro.school.entity.Avatar;
import ru.skypro.school.exception.AvatarNotFoundException;
import ru.skypro.school.record.AvatarRecord;
import ru.skypro.school.repository.AvatarRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AvatarService {

    private final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    @Value("${path.to.avatars.folder}")
    private String avatarsFolder;
    private final AvatarRepository avatarRepository;
    private final RecordMapper recordMapper;

    public AvatarService(AvatarRepository avatarRepository, RecordMapper recordMapper) {
        this.avatarRepository = avatarRepository;
        this.recordMapper = recordMapper;
    }

    public void upload(MultipartFile avatarFile) throws IOException {
        logger.info("Was invoked method to upload avatar");
        Avatar avatar = createAvatar(avatarFile);
        String extension = Optional.ofNullable(avatarFile.getOriginalFilename())
                .map(a -> a.substring(a.lastIndexOf(".")))
                .orElse("");

        Path path = Paths.get(avatarsFolder).resolve(avatar.getId() + extension);
        logger.debug("File path for avatar is {}", path.toString());
        Files.createDirectories(path.getParent());
        Files.write(path, avatar.getData());
        avatar.setFilePath(path.toString());
        avatarRepository.save(avatar);
    }

    private Avatar createAvatar(MultipartFile avatarFile) throws IOException {
        Avatar avatar = new Avatar();
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setData(avatarFile.getBytes());
        return avatarRepository.save(avatar);
    }

    public Pair<String, byte[]> readAvatarFromDb(Long id) {
        logger.info("Was invoked method to get avatar from DB");
        Avatar avatar = avatarRepository.findById(id).orElseThrow(() -> new AvatarNotFoundException(id));
        return Pair.of(avatar.getMediaType(), avatar.getData());
    }

    public Pair<String, byte[]> readAvatarFromFs(Long id) throws IOException {
        logger.info("Was invoked method to get avatar from filesystem");
        Avatar avatar = avatarRepository.findById(id).orElseThrow(() -> new AvatarNotFoundException(id));
        return Pair.of(avatar.getMediaType(), Files.readAllBytes(Paths.get(avatar.getFilePath())));
    }

    public Collection<AvatarRecord> getAllAvatars(Integer page, Integer size) {
        logger.info("Was invoked method to get avatars");
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return avatarRepository.findAll(pageRequest).getContent().stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }
}
