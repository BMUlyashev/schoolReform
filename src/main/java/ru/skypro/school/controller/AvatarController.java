package ru.skypro.school.controller;


import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.school.record.AvatarRecord;
import ru.skypro.school.service.AvatarService;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/avatars")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@RequestParam MultipartFile avatarFile) throws IOException {
        avatarService.upload(avatarFile);
    }

    @GetMapping("/{id}/from-db")
    public ResponseEntity<byte[]> readAvatarFromDb(@PathVariable Long id) {
        Pair<String, byte[]> content = avatarService.readAvatarFromDb(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(content.getFirst()))
                .contentLength(content.getSecond().length)
                .body(content.getSecond());
    }

    @GetMapping("/{id}/from-fs")
    public ResponseEntity<byte[]> readAvatarFromFs(@PathVariable Long id) throws IOException {
        Pair<String, byte[]> content = avatarService.readAvatarFromFs(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(content.getFirst()))
                .contentLength(content.getSecond().length)
                .body(content.getSecond());
    }

    @GetMapping
    public Collection<AvatarRecord> getAllAvatars(@RequestParam Integer page,
                                                  @RequestParam Integer size) {
        return avatarService.getAllAvatars(page, size);
    }
}
