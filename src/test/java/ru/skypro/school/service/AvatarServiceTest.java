package ru.skypro.school.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.school.component.RecordMapper;
import ru.skypro.school.entity.Avatar;
import ru.skypro.school.exception.AvatarNotFoundException;
import ru.skypro.school.record.AvatarRecord;
import ru.skypro.school.repository.AvatarRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceTest {

    @Mock
    AvatarRepository avatarRepository;

    @InjectMocks
    AvatarService avatarService;

    @Spy
    RecordMapper recordMapper;

    @Test
    public void upload() throws IOException {
        final String imageFile = "src/test/java/ru/skypro/school/testresource/test.gif";
        final String imagePathNew = "src/test/java/ru/skypro/school/testresource";

        Path path = Paths.get(imageFile);

        MultipartFile multipartFile = new MockMultipartFile("file",
                "test.gif", "image/gif", Files.readAllBytes(path));

        Avatar avatar = createAvatar(multipartFile);
        avatar.setId(1);
        ReflectionTestUtils.setField(avatarService, "avatarsFolder", imagePathNew);

        when(avatarRepository.save(any(Avatar.class))).thenReturn(avatar);

        avatarService.upload(multipartFile);

        File actualFile = new File("src/test/java/ru/skypro/school/testresource/1.gif");
        File expectedFile = new File("src/test/java/ru/skypro/school/testresource/test.gif");

        assertThat(actualFile).hasSameBinaryContentAs(expectedFile);

        actualFile.delete();
    }

    @Test
    public void testReadFromDb() throws IOException {
        final String imageFile = "src/test/java/ru/skypro/school/testresource/test.gif";
        Path path = Paths.get(imageFile);
        MultipartFile multipartFile = new MockMultipartFile("file",
                "test.gif", "image/gif", Files.readAllBytes(path));
        Avatar avatar = createAvatar(multipartFile);
        Pair<String, byte[]> expected = Pair.of(avatar.getMediaType(), avatar.getData());
        when(avatarRepository.findById(any()))
                .thenReturn(Optional.of(avatar))
                .thenReturn(Optional.empty());

        assertThat(avatarService.readAvatarFromDb(1L)).isEqualTo(expected);
        assertThatThrownBy(() -> avatarService.readAvatarFromDb(1L)).isInstanceOf(AvatarNotFoundException.class);
    }

    @Test
    public void testReadFromFs() throws IOException {
        final String imageFile = "src/test/java/ru/skypro/school/testresource/test.gif";
        Path path = Paths.get(imageFile);
        MultipartFile multipartFile = new MockMultipartFile("file",
                "test.gif", "image/gif", Files.readAllBytes(path));
        Avatar avatar = createAvatar(multipartFile);
        avatar.setFilePath(path.toString());
        Pair<String, byte[]> expected = Pair.of(avatar.getMediaType(), avatar.getData());
        when(avatarRepository.findById(any()))
                .thenReturn(Optional.of(avatar))
                .thenReturn(Optional.empty());

        assertThat(avatarService.readAvatarFromFs(1L)).isEqualTo(expected);
        assertThatThrownBy(() -> avatarService.readAvatarFromFs(1L)).isInstanceOf(AvatarNotFoundException.class);
    }

    @Test
    public void findAllByPage() {
        List<Avatar> avatars = List.of(
                createAvatar(1),
                createAvatar(2)
        );
        List<AvatarRecord> avatarsRecords = List.of(
                createAvatarRecord(1),
                createAvatarRecord(2)
        );
        Page<Avatar> page = new PageImpl<>(avatars);
        int size = 2;

        when(avatarRepository.findAll(any(PageRequest.class))).thenReturn(page);

        assertThat(avatarService.getAllAvatars(1, size)).hasSize(size)
                .extracting(AvatarRecord::getId).contains(1L, 2L);
    }

    private Avatar createAvatar(MultipartFile avatarFile) throws IOException {
        Avatar avatar = new Avatar();
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setData(avatarFile.getBytes());
        return avatar;
    }

    private Avatar createAvatar(long id) {
        Avatar avatar = new Avatar();
        avatar.setId(id);
        avatar.setMediaType(MediaType.MULTIPART_FORM_DATA_VALUE);
        return avatar;
    }

    private AvatarRecord createAvatarRecord(long id) {
        AvatarRecord avatarRecord = new AvatarRecord();
        avatarRecord.setId(id);
        avatarRecord.setMediaType(MediaType.MULTIPART_FORM_DATA_VALUE);
        avatarRecord.setUrl("http://localhost:8080/avatars/" + id + "/from-db");
        return avatarRecord;
    }
}
