package com.ledger.common.exception;

import com.ledger.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author ledger
 * @version 1.0
 **/
@ControllerAdvice
@RestController
@Slf4j
public class GulimallExceptionControllerAdvice extends RuntimeException {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException e) {
        log.error("出现错误，{}，{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        HashMap<String, String> hashMap = new HashMap<>();
        fieldErrors.forEach(fieldError -> hashMap.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(),BizCodeEnume.VAILD_EXCEPTION.getMsg()).put("data", hashMap);
    }
    @ExceptionHandler(value = Exception.class)
    public R handleException(Exception e) {
        log.error("出现错误，{}，{}",e.getMessage(),e.getClass());
        e.printStackTrace();
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }

}
