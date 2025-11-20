package com.flightapp.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.flightapp.GlobalExceptionHandler;
import com.flightapp.request.PassengerRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // -------------------- RuntimeException Handler --------------------

    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Test runtime error");

        ResponseEntity<Map<String, Object>> response = handler.handleRuntimeException(ex);

        assertEquals(400, response.getBody().get("status"));
        assertEquals("Test runtime error", response.getBody().get("error"));
    }

    // -------------------- Validation Exception Handler --------------------

    @Test
    void testHandleValidationErrors() {

        PassengerRequest req = new PassengerRequest();
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(req, "passengerRequest");

        // Simulating validation errors (manually)
        bindingResult.addError(new FieldError("passengerRequest", "name", "must not be blank"));
        bindingResult.addError(new FieldError("passengerRequest", "gender", "must not be blank"));
        bindingResult.addError(new FieldError("passengerRequest", "age", "must be >= 1"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(ex);

        Map<String, Object> body = response.getBody();

        assertEquals(400, body.get("status"));
        assertEquals("Validation failed", body.get("error"));

        Map<String, String> details = (Map<String, String>) body.get("details");

        assertEquals("must not be blank", details.get("name"));
        assertEquals("must not be blank", details.get("gender"));
        assertEquals("must be >= 1", details.get("age"));
    }
}
