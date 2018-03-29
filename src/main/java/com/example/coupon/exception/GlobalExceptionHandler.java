package com.example.coupon.exception;

import com.example.coupon.config.CouponProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by alcava00 on 2018. 3. 27..
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CouponProperties couponProperties;

    @ExceptionHandler(CouponException.class)
    public ResponseEntity<Object> doResolveCouponException(HttpServletRequest request, HttpServletResponse response,
                                                           Object handler, CouponException ex) {
        logger.error("******************* ", ex);
        return createResponseEntity(ex.getErrorCd(), ex.getMessageArg(), Optional.ofNullable(ex.getHttpStatus()).orElse(HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler
    public ResponseEntity<Object> doResolveException(HttpServletRequest request, HttpServletResponse response,
                                                     Object handler, Exception ex) {
        logger.error("!*******************  ", ex);

        return createResponseEntity(couponProperties.getDefaultErrorCode(), new String[]{ex.getMessage()}, couponProperties.getDefaultExceptionHttpStatus());
    }

    private String resolveLocalizedErrorMessage(FieldError fieldError) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(fieldError.getObjectName() + "." + fieldError.getField(), new String[]{(String) fieldError.getRejectedValue(),}, fieldError.getDefaultMessage(), LocaleContextHolder.getLocale());
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        logger.error("******************* !!", ex);
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        //validation 오류 처리
        if (ex instanceof MethodArgumentNotValidException) {

            ExceptionResponse er = new ExceptionResponse();
            er.setErrorCode(couponProperties.getDefaultErrorCode());

            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            StringBuilder sb = new StringBuilder();
            for (FieldError fieldError : fieldErrors) {
                String msg = messageSource.getMessage(fieldError.getObjectName() + "." + fieldError.getField(), new String[]{(String) fieldError.getRejectedValue(),}, fieldError.getDefaultMessage(), LocaleContextHolder.getLocale());
                sb.append(msg).append(" ");
            }
            er.setMessage(sb.toString());
            return ResponseEntity.status(status).body(er);
        }
        return createResponseEntity(couponProperties.getDefaultErrorCode(), new String[]{status + ":" + ex.getMessage()}, status);
    }

    private ResponseEntity<Object> createResponseEntity(String errorCd, String[] args, HttpStatus status) {
        ExceptionResponse er = new ExceptionResponse();
        er.setErrorCode(errorCd);
        er.setMessage(messageSource.getMessage(errorCd, args, "exception : ", LocaleContextHolder.getLocale()));
        return ResponseEntity.status(status).body(er);
    }

}
