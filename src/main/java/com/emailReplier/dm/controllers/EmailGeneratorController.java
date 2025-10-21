package com.emailReplier.dm.controllers;

import com.emailReplier.dm.model.EmailEntity;
import com.emailReplier.dm.service.EmailGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailGeneratorController {


    private final EmailGeneratorService emailGeneratorService;

    public EmailGeneratorController(EmailGeneratorService emailGeneratorService) {
        this.emailGeneratorService = emailGeneratorService;
    }


    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailEntity emailEntity){
        System.out.println("content is:"+emailEntity);
        String response = emailGeneratorService.generateEmail(emailEntity);
        return ResponseEntity.ok(response);
    }
}
