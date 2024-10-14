package com.aptech.SemesterProject.service;

import com.aptech.SemesterProject.exception.StorageException;
import com.aptech.SemesterProject.exception.StorageFileNotFoundException;
import com.aptech.SemesterProject.utility.StorageProperties;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements  StorageService{

    private Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties){
        if(properties.getLocation().trim().length()==0){
            throw new StorageException("File upload location can not be empty");
        }
        this.rootLocation= Paths.get(properties.getLocation());
    }
    @Override
    public void init() {
        try{
            Files.createDirectories(rootLocation);
        }catch(IOException ex){
            throw new StorageException("Could not initialize storage", ex);
        }
    }

    @Override
    public String store(String root,String prefix,MultipartFile file) {
        try{
            Path root1 = Paths.get(root);
            if(file.isEmpty()){
                throw new StorageException("Failed to store empty file");
            }
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            // Get the MIME type
            String mimeType = file.getContentType();
            List<String> allowedMimeTypes = Arrays.asList(MimeTypeUtils.IMAGE_JPEG_VALUE, MimeTypeUtils.IMAGE_PNG_VALUE, MimeTypeUtils.IMAGE_GIF_VALUE);

            // Validate MIME type
            if (!allowedMimeTypes.contains(mimeType)) {
                throw new StorageException(file.getOriginalFilename() + " is not an image file");
            }

            // Additional check if needed: Validate file extension
            List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
            if (!allowedExtensions.contains(fileExtension.toLowerCase())) {
                throw new StorageException(file.getOriginalFilename() + " has an invalid file extension");
            }


            root1=root1.resolve(prefix);
            if(!Files.exists(root1)){
                Files.createDirectories(root1);
            }
            String fileName = prefix+"_"+ UUID.randomUUID().toString()+"."+fileExtension;

            Path destinationFile = root1.resolve(Paths.get(fileName)).normalize().toAbsolutePath();
            System.out.println(destinationFile);
            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream,destinationFile,StandardCopyOption.REPLACE_EXISTING);
            }
            return  fileName;
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new StorageException("Failed to store file",e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try{
            return Files.walk(this.rootLocation,1)
                    .filter(path->!path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize); // read all stored files up to 1 subfolder
        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageException("Failed to read stored files",e);
        }
    }

    @Override
    public Path load(String fileName,String prefix) {
        Path root = rootLocation.resolve(prefix);
        return root.resolve(fileName);
    }

    @Override
    public Resource loadAsResource(String fileName,String prefix) {
        try{
            Path file = load(fileName, prefix);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists()||resource.isReadable()){
                return resource;
            }else{
                throw new StorageFileNotFoundException("Could not read file: "+fileName);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new StorageFileNotFoundException("Could not read file: "+fileName);
        }

    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
    @Override
    public void deleteFile(String fileName, String prefix) throws IOException {
        Path file = load(fileName,prefix);

        if(file!=null){
            if(Files.exists(file)){
                Files.delete(file);
            }
        }

    }
}