package com.timur.spotify.service.music;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String uploadDirectory = "D:\\IT\\SpotifyClone\\spotify\\src\\main\\resources\\static\\";

    public String saveFile(MultipartFile file) throws IOException {
        // Создаем уникальное имя файла
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // Путь к директории сохранения файла
        Path uploadPath = Paths.get(uploadDirectory);

        // Создаем директорию, если она не существует
        Files.createDirectories(uploadPath);

        // Полный путь к файлу
        Path filePath = uploadPath.resolve(fileName);

        // Сохраняем файл на файловой системе
        Files.copy(file.getInputStream(), filePath);

        // Возвращаем путь к сохраненному файлу
        return filePath.toString();
    }
}