package cn.swunlp.auth.advice;

import cn.swunlp.auth.exception.AppRegisterException;
import cn.swunlp.backend.base.base.exception.BusinessException;
import cn.swunlp.backend.base.security.constant.AppRegisterResult;
import cn.swunlp.backend.base.web.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一处理应用注册结果
 * @author TangXi
 */
@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class AppRegisterResultHandler {

//	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(AppRegisterException.class)
	public AppRegisterResult handleRuntimeException(Exception e){
		log.warn(e.getMessage());
		return AppRegisterResult.valueOf(e.getMessage());
	}


}
