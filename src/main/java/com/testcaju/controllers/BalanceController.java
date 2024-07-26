package com.testcaju.controllers;

import com.testcaju.domain.user.Balance;
import com.testcaju.services.BalanceService;
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
@RequestMapping("/balance")
@Tag(name = "/balance")
public class BalanceController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    @Autowired
    private BalanceService BalanceService;

    @GetMapping("/{id}")
    public ResponseEntity<Balance> getBalanceById(@PathVariable Long id) {
        return BalanceService.getById(id.toString())
                .map(Balance -> ResponseEntity.ok().body(Balance))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/all")
    public List<Balance> All() {
        return this.BalanceService.all();
    }

    @GetMapping(path = "/by-userid/{id}")
    public ResponseEntity<Balance> getById(@PathVariable Long id) {
        return BalanceService.getById(id.toString())
                .map(user -> ResponseEntity.ok().body(user))
                .orElseGet(() -> ResponseEntity.notFound().build());
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
