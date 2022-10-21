package ru.skypro.school.service;


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

    @Value("${path.to.avatars.folder}")
    private String avatarsFolder;
    private final AvatarRepository avatarRepository;
    private final RecordMapper recordMapper;

    public AvatarService(AvatarRepository avatarRepository, RecordMapper recordMapper) {
        this.avatarRepository = avatarRepository;
        this.recordMapper = recordMapper;
    }

    public void upload(MultipartFile avatarFile) throws IOException {
        Avatar avatar = createAvatar(avatarFile);
        String extension = Optional.ofNullable(avatarFile.getOriginalFilename())
                .map(a -> a.substring(a.lastIndexOf(".")))
                .orElse("");

        Path path = Paths.get(avatarsFolder).resolve(avatar.getId() + extension);

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
        Avatar avatar = avatarRepository.findById(id).orElseThrow(() -> new AvatarNotFoundException(id));
        return Pair.of(avatar.getMediaType(), avatar.getData());
    }

    public Pair<String, byte[]> readAvatarFromFs(Long id) throws IOException {
        Avatar avatar = avatarRepository.findById(id).orElseThrow(() -> new AvatarNotFoundException(id));
        return Pair.of(avatar.getMediaType(), Files.readAllBytes(Paths.get(avatar.getFilePath())));
    }

    public Collection<AvatarRecord> getAllAvatars(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return avatarRepository.findAll(pageRequest).getContent().stream()
                .map(recordMapper::toRecord)
                .collect(Collectors.toList());
    }
}
