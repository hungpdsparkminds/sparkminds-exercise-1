package com.example.librarymanagement.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {
    private final AmazonS3 amazonS3client;

    @Value("${amazon.s3.bucket.name}")
    private String bucketName;
    @Value("${amazon.s3.bucket.url}")
    private String bucketUrl;

    @Override
    public String uploadFile(MultipartFile multipartFile, String filePrefix) throws IOException {
        String result = null;
        if (multipartFile != null) {
            File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(multipartFile.getBytes());
            } catch (IOException e) {
                log.error("Error converting multipartFile to file", e);
            }
            String fileName = filePrefix + UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
            amazonS3client.putObject(new PutObjectRequest(bucketName, fileName, file));
            result = amazonS3client.getUrl(bucketName, fileName).toString();
            Files.delete(Path.of(Objects.requireNonNull(multipartFile.getOriginalFilename())));
        }
        return result;
    }

    @Override
    public String uploadFile(byte[] fileBytes, String fileName, String filePrefix) throws IOException {
        String result = null;
        if (fileBytes != null) {
            File file = new File(fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileBytes);
            } catch (IOException e) {
                log.error("Error converting to file", e);
            }
            String fileKey = filePrefix + UUID.randomUUID() + "_" + fileName;
            amazonS3client.putObject(new PutObjectRequest(bucketName, fileKey, file));
            result = amazonS3client.getUrl(bucketName, fileKey).toString();
            Files.delete(Path.of(Objects.requireNonNull(fileName)));
        }
        return result;
    }

    @Override
    public void deleteFile(String[] fileNames) {
        String[] keys = new String[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            keys[i] = extractKey(fileNames[i]);
        }
        amazonS3client.deleteObjects(new DeleteObjectsRequest(bucketName)
                .withKeys(keys));
    }

    String extractKey(String url) {
        if (url.startsWith(bucketUrl)) {
            return url.substring(bucketUrl.length());
        } else {
            throw new IllegalArgumentException("The URL doesn't start with the expected base.");
        }
    }
}