package com.testcaju.controllers;

import com.testcaju.domain.user.User;
import com.testcaju.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name = "/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userService.getById(id.toString())
                .map(user -> ResponseEntity.ok().body(user))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/all")
    public List<User> All() {
        return this.userService.all();
    }
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleMissingRequestBody(HttpMessageNotReadableException exception) {
        logger.error("Missing request body!", exception);

        return new ResponseEntity<>(new HashMap<>() {{
            put("code", "07"); // Return default message if the request does not contain a body
        }}, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleInvalidRequestBody(MethodArgumentNotValidException exception) {
        logger.error("Invalid request body!", exception);

        return new ResponseEntity<>(new HashMap<>() {{
            put("code", "07"); // Return default message if the request does not contain a body
        }}, HttpStatus.OK);
    }

}
