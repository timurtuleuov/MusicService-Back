package com.timur.spotify.service.music;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String STATIC_PATH = "src/main/resources/static/";
    private static final String COVER_FOLDER = "covers/";

    public String savePlaylistCover(MultipartFile file) throws IOException {
        // Уникальное имя файла
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // Путь к папке static/covers/
        Path uploadPath = Paths.get(STATIC_PATH + COVER_FOLDER);

        // Создаём директорию, если не существует
        Files.createDirectories(uploadPath);

        // Полный путь к файлу
        Path filePath = uploadPath.resolve(fileName);

        // Сохраняем файл (перезаписываем, если уже есть)
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Возвращаем относительный путь, который можно использовать как URL
        return "/static/" + COVER_FOLDER + fileName;
    }

    public String saveFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // Путь к папке static/
        Path uploadPath = Paths.get(STATIC_PATH);

        // Создаём директорию, если не существует
        Files.createDirectories(uploadPath);

        // Полный путь
        Path filePath = uploadPath.resolve(fileName);

        // Сохраняем файл
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Возвращаем относительный путь
        return "/static/" + fileName;
    }
}