package com.Temporal.SDKBuilder.XML.Controller;

import com.Temporal.SDKBuilder.XML.Entity.UpdateItem;
import com.Temporal.SDKBuilder.XML.Entity.UpdateSet;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.squareup.javapoet.*;
import javax.lang.model.element.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/workflow")
public class TemporalWorkflowController {

    private static final Logger logger = LoggerFactory.getLogger(TemporalWorkflowController.class);

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

            String javaCode = generateJavaCode(updateSet);
            return ResponseEntity.ok(javaCode);
        } catch (IOException e) {
            logger.error("Error processing file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file");
        }
    }

    private String generateJavaCode(UpdateSet updateSet) {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("GeneratedWorkflow")
                .addModifiers(Modifier.PUBLIC);

        // Example: Add methods based on the update set
        for (UpdateItem item : updateSet.getItems()) {
            String methodName = sanitizeMethodName(item.getTargetName());



            MethodSpec method = MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addStatement("// TODO: Implement " + item.getName())
                    .build();
            classBuilder.addMethod(method);
        }

        JavaFile javaFile = JavaFile.builder("com.example.workflow", classBuilder.build()).build();

        return javaFile.toString();
    }

    private String sanitizeMethodName(String name) {
        if (name == null || name.isEmpty()) {
            return "defaultMethodName";
        }
        // Remove non-alphanumeric characters and capitalize the next character after a space
        StringBuilder sanitized = new StringBuilder();
        boolean capitalizeNext = false;
        for (char c : name.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                sanitized.append(capitalizeNext ? Character.toUpperCase(c) : c);
                capitalizeNext = false;
            } else if (c == ' ') {
                capitalizeNext = true;
            }
        }
        return sanitized.toString();
    }
}
