package com.testcaju.controllers;

import com.testcaju.domain.transaction.Transaction;
import com.testcaju.dtos.TransactionDTO;
import com.testcaju.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
@Tag(name = "/transaction")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    @Autowired
    private TransactionService transactionService;

    @Operation(method = "POST", summary = "Authorizes a transaction", description = "\n" +
            "Authorizes a transaction based on the merchant's MCC and the user's wallet balance.")
    @PostMapping(path = "/authorize",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = """
                            - `{ "code": "00" }` **approved**\s
                            - `{ "code": "51" }` **rejected** insufficient balance
                            - `{ "code": "07" }` **rejected** other reasons""",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = "{ \"code\": \"00\" }")))
    })
    public ResponseEntity<Map<String, String>> authorize(@Valid @RequestBody TransactionDTO transaction) {
        HashMap<String, String> map = new HashMap<>();

        String code = this.transactionService.authorize(transaction);
        map.put("code", code);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @GetMapping(path = "/all")
    public List<Transaction> All() {
        return this.transactionService.all();
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
