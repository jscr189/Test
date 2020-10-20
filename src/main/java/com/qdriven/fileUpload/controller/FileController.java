package com.qdriven.fileUpload.controller;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileController {

    @Autowired
    FileService fileService;

    @GetMapping("/")
    public String index() {
        return "upload";
    }
    
    @GetMapping("/index")
    public String indexPage() {
        return "index";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        String message = fileService.uploadFile(file);

        redirectAttributes.addFlashAttribute("message",
            message);

        return "redirect:/";
    }

    @PostMapping("/uploadFiles")
    public String uploadFiles(@RequestParam("files") MultipartFile[] files, RedirectAttributes redirectAttributes) {
    	String[] message = {""};
        Arrays.asList(files)
            .stream()
            .forEach(file -> {
            	message[0]+=fileService.uploadFile(file)+"                  ";
            });

        redirectAttributes.addFlashAttribute("message",
        		message[0]);

        return "redirect:/";
    }
}
