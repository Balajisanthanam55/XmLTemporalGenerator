package com.Temporal.SDKBuilder.XML.Controller;

import com.Temporal.SDKBuilder.XML.Entity.UpdateItem;
import com.Temporal.SDKBuilder.XML.Entity.UpdateSet;
import com.Temporal.SDKBuilder.XML.Service.BuilderService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.squareup.javapoet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Modifier;

@RestController
@RequestMapping("/api/workflow")
public class TemporalWorkflowController {

    private static final Logger logger = LoggerFactory.getLogger(TemporalWorkflowController.class);

    @Autowired
    BuilderService buildService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            logger.warn("File upload attempt with empty file.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        try {
            String xmlContent = new String(file.getBytes());
            // Parse XML and generate Java code
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            UpdateSet updateSet = xmlMapper.readValue(xmlContent, UpdateSet.class);

            List<String> javaClasses = buildService.generateJavaCode(updateSet);
            return ResponseEntity.ok(String.join("\n", javaClasses));
        } catch (IOException e) {
            logger.error("Error processing file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file");
        }
    }
}