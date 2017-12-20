package com.izhiliao.web;

import java.lang.reflect.UndeclaredThrowableException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.izhiliao.util.ResultInfo;

public class CommonController {

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody ResultInfo greetingExceptionHandler(Exception ex) {
		
		System.out.println("@getClass:" + this.getClass().getSimpleName() + "---Exception:" + ex.getMessage());
		ResultInfo info = new ResultInfo();
		info.ReturnValue = -1;
		info.ErrorInfo = ex.getMessage();
		return info;
	}
	@ExceptionHandler(UndeclaredThrowableException.class)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody ResultInfo greetingExceptionHandler(UndeclaredThrowableException ex) {
		
		ResultInfo info = new ResultInfo();
		info.ReturnValue = -1;
		info.ErrorInfo = ex.getUndeclaredThrowable().getMessage();
		return info;
	}


}