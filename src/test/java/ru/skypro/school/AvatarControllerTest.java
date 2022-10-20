package ru.skypro.school;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.school.controller.AvatarController;
import ru.skypro.school.entity.Avatar;
import ru.skypro.school.repository.AvatarRepository;
import ru.skypro.school.service.AvatarService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AvatarController.class)
@ExtendWith(MockitoExtension.class)
public class AvatarControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AvatarRepository avatarRepository;

    @SpyBean
    AvatarService avatarService;

    @Test
    public void create() throws Exception {
        final String imageFile = "src/test/java/ru/skypro/school/testresource/test.gif";
        final String imagePathNew = "src/test/java/ru/skypro/school/testresource";

        Path path = Paths.get(imageFile);

        MockMultipartFile multipartFile = new MockMultipartFile("avatarFile",
                "test.gif", "image/gif", Files.readAllBytes(path));

        ReflectionTestUtils.setField(avatarService, "avatarsFolder", imagePathNew);
        Avatar avatar = createAvatar(multipartFile);
        avatar.setId(1L);
        avatar.setFilePath("src/test/java/ru/skypro/school/testresource/test.gif");

        when(avatarRepository.save(any())).thenReturn(avatar);
        when(avatarRepository.findById(any())).thenReturn(Optional.of(avatar));

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/avatars")
                        .file(multipartFile))
                .andExpect(status().isOk());


        File actualFile = new File("src/test/java/ru/skypro/school/testresource/1.gif");
        File expectedFile = new File("src/test/java/ru/skypro/school/testresource/test.gif");
        assertThat(actualFile).exists();
        assertThat(actualFile).hasSameBinaryContentAs(expectedFile);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/avatars/1/from-fs")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    assertThat(result.getResponse().getContentLength()).isEqualTo(actualFile.length());
                }));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/avatars/1/from-db")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    assertThat(result.getResponse().getContentLength()).isEqualTo(actualFile.length());
                }));

        actualFile.delete();
    }

    private Avatar createAvatar(MultipartFile avatarFile) throws IOException {
        Avatar avatar = new Avatar();
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setData(avatarFile.getBytes());
        return avatar;
    }
}
