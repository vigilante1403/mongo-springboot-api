package com.aptech.SemesterProject.controller;

import com.aptech.SemesterProject.service.StorageService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/file")
public class FileUploadController {
    @GetMapping(value="/image/{prefix}/{filename}", produces= MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getFile(@PathVariable String filename,@PathVariable String prefix){
        try{
            Path file= Paths.get("upload","image").resolve(prefix).resolve(filename);
            Resource resource= new UrlResource(file.toUri());
            byte[] file1 = new byte[1024*1024*5];
            if(resource.exists()||resource.isReadable()){
                file1= FileUtils.readFileToByteArray(resource.getFile());

            }
            return new ResponseEntity<byte[]>(file1, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }
}
