package com.qdriven.fileUpload.controller;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.qdriven.doc.ProcessDocThread;

@Service
public class FileService {

//    @Value("${app.upload.dir:${user.home}}")
//    public String uploadDir;

    public String uploadFile(MultipartFile file) {
    	String out = "Error occurred while processing the file "+file.getOriginalFilename();
        try {
        	ClassLoader classLoader = getClass().getClassLoader();
        	String fileName = System.getProperty("user.dir") + "/src/main/resources/input/" + 
        						StringUtils.cleanPath(file.getOriginalFilename());
            Path copyLocation = Paths.get(fileName);
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            ProcessDocThread processDocThread = new ProcessDocThread(new File(fileName));
            out = processDocThread.convert();
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileStorageException("Could not store file " + file.getOriginalFilename()
                + ". Please try again!");
        }
        return out;
    }
}
