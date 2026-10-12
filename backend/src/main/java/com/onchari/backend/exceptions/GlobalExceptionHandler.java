package com.onchari.backend.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String DEFAULT_ERROR_MESSAGE =
            "An unexpected exception occured while processing request";

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ProblemDetail> onResourceNotFoundException(
            Exception e, HttpServletRequest httpServlet) {
        String errorMessage = e.getMessage() != null ? e.getLocalizedMessage() : DEFAULT_ERROR_MESSAGE;
        ProblemDetail problemDetail = this.createProblemDetail(errorMessage);
        logException(problemDetail, HttpStatus.NOT_FOUND, e);
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    private ProblemDetail createProblemDetail(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message);
        problemDetail.setProperty("message", message);
        return problemDetail;
    }

    private void logException(ProblemDetail problemDetail, HttpStatus httpStatus, Exception cause) {
        if (httpStatus.is4xxClientError()) {
            log.info(problemDetail.getDetail());
        } else {
            log.error("Error handling request", cause);
        }
    }
}
