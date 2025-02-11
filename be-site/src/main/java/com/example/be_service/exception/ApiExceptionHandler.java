package com.example.be_service.exception;

import com.example.be_service.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_LOG_FORMAT = "Error: URI: {}, ErrorCode: {}, Message: {}";

    private void logException(int errorCode, Exception ex, WebRequest request) {
        String message = ex.getMessage();
        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), errorCode, message);
        log.debug(ex.toString());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDTO> handleAccessDeniedException(AccessDeniedException ex,
        WebRequest request) {
        String message = ex.getMessage();
        ErrorDTO ErrorDTO = new ErrorDTO(HttpStatus.FORBIDDEN.toString(), "Access Denied", message);
        logException(403, ex, request);
        return new ResponseEntity<>(ErrorDTO, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDTO> handleNotFoundException(NotFoundException ex,
        WebRequest request) {
        String message = ex.getMessage();
        ErrorDTO ErrorDTO = new ErrorDTO(HttpStatus.NOT_FOUND.toString(), "NotFound", message);
        logException(404, ex, request);
        return new ResponseEntity<>(ErrorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WrongEmailFormatException.class)
    public ResponseEntity<ErrorDTO> handleWrongEmailFormatException(WrongEmailFormatException ex,
        WebRequest request) {
        String message = ex.getMessage();
        ErrorDTO ErrorDTO = new ErrorDTO(HttpStatus.BAD_REQUEST.toString(), "Bad request", message);
        logException(400, ex, request);
        return ResponseEntity.badRequest().body(ErrorDTO);
    }

    @ExceptionHandler(CreateGuestUserException.class)
    public ResponseEntity<ErrorDTO> CreateGuestUserException(CreateGuestUserException ex,
        WebRequest request) {
        String message = ex.getMessage();
        ErrorDTO ErrorDTO = new ErrorDTO(HttpStatus.BAD_REQUEST.toString(), "Bad request", message);
        logException(500, ex, request);
        return ResponseEntity.badRequest().body(ErrorDTO);
    }

    private String getServletPath(WebRequest webRequest) {
        ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
        return servletRequest.getRequest().getServletPath();
    }
}
