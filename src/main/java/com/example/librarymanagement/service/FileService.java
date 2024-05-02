package com.example.librarymanagement.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String uploadFile(MultipartFile multipartFile, String filePrefix) throws IOException;
    String uploadFile(byte[] fileBytes, String fileName, String filePrefix) throws IOException;
    void deleteFile(String[] fileNames);
}