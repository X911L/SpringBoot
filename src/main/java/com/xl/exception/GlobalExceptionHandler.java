package com.xl.exception;

import com.xl.common.CommonErrorCode;
import com.xl.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ResponseBody
	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public RestErrorResponse customException(BusinessException e) {

		log.error("【系统异常】{}",e.getMessage(),e);

		ErrorCode errorCode = e.getErrorCode();

		return new RestErrorResponse(errorCode);

	}

	@ResponseBody
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public RestErrorResponse exception(Exception e) {

		log.error("【系统异常】{}",e.getMessage(),e);

		return new RestErrorResponse(String.valueOf(CommonErrorCode.UNKOWN.getCode()),
				CommonErrorCode.UNKOWN.getDesc()+":"+e.getMessage());

	}
}