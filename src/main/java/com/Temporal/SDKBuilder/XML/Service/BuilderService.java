package com.Temporal.SDKBuilder.XML.Service;

import com.Temporal.SDKBuilder.XML.Entity.UpdateItem;
import com.Temporal.SDKBuilder.XML.Entity.UpdateSet;
import com.squareup.javapoet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class BuilderService {


    public List<String> generateJavaCode(UpdateSet updateSet) {
        List<String> javaClasses = new ArrayList<>();
        List<UpdateItem> flowItems = new ArrayList<>();
        List<UpdateItem> actionItems = new ArrayList<>();

        // Separate update items into flow and action lists
        for (UpdateItem item : updateSet.getItems()) {
            if ("flow".equalsIgnoreCase(item.getType())) {
                flowItems.add(item);
            } else if ("action type".equalsIgnoreCase(item.getType())) {
                actionItems.add(item);
            }
        }

        // Generate flow classes
        for (UpdateItem flowItem : flowItems) {
            String flowClassName = sanitizeClassName(flowItem.getTargetName());
            javaClasses.add(generateFlowInterface(flowClassName, sanitizeMethodName(flowItem.getTargetName())));
            javaClasses.add(generateFlowClass(flowClassName, actionItems));
        }

        // Generate action classes
        for (UpdateItem actionItem : actionItems) {
            String actionClassName = sanitizeClassName(actionItem.getTargetName());
            javaClasses.add(generateActionClass(actionClassName));
        }

        return javaClasses;
    }

    private String generateFlowInterface(String className, String methodName) {
        TypeSpec.Builder interfaceBuilder = TypeSpec.interfaceBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(ClassName.get("io.temporal.workflow", "WorkflowInterface"));

        MethodSpec workflowMethod = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(ClassName.get("io.temporal.workflow", "WorkflowMethod"))
                .returns(void.class)
                .addParameter(String.class, "orderId")
                .build();
        interfaceBuilder.addMethod(workflowMethod);

        JavaFile javaFile = JavaFile.builder("com.example.workflow", interfaceBuilder.build()).build();
        return javaFile.toString();
    }
    private String generateFlowClass(String className, List<UpdateItem> actionItems) {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className + "Impl")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get("com.example.workflow", className))
                .addField(FieldSpec.builder(Logger.class, "logger", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$T.getLogger($L.class)", LoggerFactory.class, className + "Impl")
                        .build())
                .addField(FieldSpec.builder(ClassName.get("com.example.workflow", "TelecomOrderActivities"), "activities", Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("$T.newActivityStub($T.class, $T.newBuilder().setScheduleToCloseTimeout($T.ofMinutes(5)).build())",
                                ClassName.get("io.temporal.workflow", "Workflow"),
                                ClassName.get("com.example.workflow", "TelecomOrderActivities"),
                                ClassName.get("io.temporal.activity", "ActivityOptions"),
                                Duration.class)
                        .build());

        // Add methods to the flow class
        MethodSpec.Builder executeFlowMethodBuilder = MethodSpec.methodBuilder("executeFlow")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement("logger.info(\"Executing flow logic\")")
                .addStatement("// TODO: Implement flow logic here");

        for (UpdateItem actionItem : actionItems) {
            String actionClassName = sanitizeClassName(actionItem.getTargetName());
            executeFlowMethodBuilder.addStatement("$L.executeAction()", actionClassName);
        }

        MethodSpec executeFlowMethod = executeFlowMethodBuilder.build();
        classBuilder.addMethod(executeFlowMethod);

        JavaFile javaFile = JavaFile.builder("com.example.workflow", classBuilder.build()).build();
        return javaFile.toString();
    }




    private String generateActionClass(String className) {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC);

        // Example: Add a sample method to the action class
        MethodSpec method = MethodSpec.methodBuilder("executeAction")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement("// TODO: Implement action logic here")
                .build();
        classBuilder.addMethod(method);

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

    private String sanitizeClassName(String name) {
        if (name == null || name.isEmpty()) {
            return "DefaultClassName";
        }
        // Remove non-alphanumeric characters and capitalize each word
        StringBuilder sanitized = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : name.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                sanitized.append(capitalizeNext ? Character.toUpperCase(c) : c);
                capitalizeNext = false;
            } else {
                capitalizeNext = true;
            }
        }
        return sanitized.toString();
    }
}



