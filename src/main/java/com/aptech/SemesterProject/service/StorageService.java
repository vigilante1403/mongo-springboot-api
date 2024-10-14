package com.aptech.SemesterProject.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init(); // start service
    String store(String root,String prefix,MultipartFile file); // store file
    Stream<Path> loadAll(); // load all file
    Path load(String fileName, String prefix); // load file
    Resource loadAsResource(String fileName, String prefix);
    void deleteAll();
    void deleteFile(String fileName, String prefix) throws IOException;
}
