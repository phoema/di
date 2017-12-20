package com.izhiliao.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Aspect
@Component
/**
 * 针对controller切面
 * @author jiahh 2017年2月10日
 *
 */
public class ControllerAspect {

	@Autowired
	IPListCache iPListCache;
	@Pointcut("execution(public * com.izhiliao.web.PatentController.cutword(..)) || execution(public * com.izhiliao.web.PatentController.smartSearch(..)) ")
	//@Pointcut("execution(public * com.izhiliao.web.PatentController.*(..))")
	public void executeService() {
		System.out.println("executeService()");

	}
//	@Pointcut("execution(public * com.dipub.web.api..*.*(..))")
//	public void searchpoint() {
//		System.out.println("executeService()");
//
//	}
	@Before("executeService()")
	public void doBefore(JoinPoint joinPoint) throws Throwable {
		long accessTime = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		String accessMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
		String accessArgs = Arrays.toString(joinPoint.getArgs());
		String ip = null;
		// 接收到请求，记录请求内容
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();
			ip = request.getRemoteAddr();
			log.info("IP : " + request.getRemoteAddr());
			
		}
		if(!iPListCache.getIPMap().containsKey(ip)){
			log.info("未授权IP访问 : " + ip);
			throw new Exception("未授权IP访问 : " + ip);
			//return;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		String infoString = objectMapper.writeValueAsString(accessTime);
		log.info("ACCESS_INFO : " + infoString);

	}

	@AfterReturning(returning = "ret", pointcut = "executeService()")
	public void doAfterReturning(JoinPoint joinPoint,Object ret) throws Throwable {
		// 处理完请求，返回内容
		log.debug("RESPONSE : " + ret);
		if (ret != null && (ret.toString().contains("\"error\":")|| !ret.toString().replaceAll(" ", "").contains("\"errorCode\":\"000000\"") )) {
			String accessMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
			String accessArgs = Arrays.toString(joinPoint.getArgs());
			log.error("\r\n accessMethod : " + accessMethod + "\r\n accessArgs : " + accessArgs + "\r\n RESPONSE : " + ret) ;
		}
	}
	/**
	 * 后置异常通知 定义一个名字，该名字用于匹配通知实现方法的一个参数名，当目标方法抛出异常返回后，将把目标方法抛出的异常传给通知方法；
	 * throwing 限定了只有目标方法抛出的异常与通知方法相应参数异常类型时才能执行后置异常通知，否则不执行，
	 * 对于throwing对应的通知方法参数为Throwable类型将匹配任何异常。
	 * 
	 * @param joinPoint
	 * @param exception
	 */
	@AfterThrowing(value = "executeService()", throwing = "exception")
	public void doAfterThrowingAdvice(JoinPoint joinPoint, Throwable exception) {
		// 目标方法名：
		log.error(joinPoint.getSignature().getName());
		if (exception instanceof NullPointerException) {
			log.error("发生了空指针异常!!!!!");
		} else {
			log.error("发生了" + exception.getClass() + "异常!!!!!" + exception.getMessage());
		}
	}

}

/**
 * 任意公共方法的执行： execution（public * *（..）） 任何一个名字以“set”开始的方法的执行： execution（*
 * set*（..）） AccountService接口定义的任意方法的执行： execution（*
 * com.xyz.service.AccountService.*（..）） 在service包中定义的任意方法的执行： execution（*
 * com.xyz.service.*.*（..）） 在service包或其子包中定义的任意方法的执行： execution（*
 * com.xyz.service..*.*（..）） 在service包中的任意连接点（在Spring AOP中只是方法执行）：
 * within（com.xyz.service.*） 在service包或其子包中的任意连接点（在Spring AOP中只是方法执行）：
 * within（com.xyz.service..*） 实现了AccountService接口的代理对象的任意连接点 （在Spring
 * AOP中只是方法执行）： this（com.xyz.service.AccountService） 'this'在绑定表单中更加常用：-
 * 请参见后面的通知一节中了解如何使得代理对象在通知体内可用。 实现AccountService接口的目标对象的任意连接点 （在Spring
 * AOP中只是方法执行）： target（com.xyz.service.AccountService） 'target'在绑定表单中更加常用：-
 * 请参见后面的通知一节中了解如何使得目标对象在通知体内可用。 任何一个只接受一个参数，并且运行时所传入的参数是Serializable
 * 接口的连接点（在Spring AOP中只是方法执行） args（java.io.Serializable） 'args'在绑定表单中更加常用：-
 * 请参见后面的通知一节中了解如何使得方法参数在通知体内可用。 请注意在例子中给出的切入点不同于 execution(*
 * *(java.io.Serializable))：
 * args版本只有在动态运行时候传入参数是Serializable时才匹配，而execution版本在方法签名中声明只有一个
 * Serializable类型的参数时候匹配。 目标对象中有一个 @Transactional 注解的任意连接点 （在Spring AOP中只是方法执行）
 * 
 * @target（org.springframework.transaction.annotation.Transactional） 
 *                                                                   '@target'在绑定表单中更加常用：-
 *                                                                   请参见后面的通知一节中了解如何使得注解对象在通知体内可用
 *                                                                   。
 *                                                                   任何一个目标对象声明的类型有一个 @Transactional
 *                                                                   注解的连接点
 *                                                                   （在Spring
 *                                                                   AOP中只是方法执行
 *                                                                   ）：
 * @within（org.springframework.transaction.annotation.Transactional） 
 *                                                                   '@within'在绑定表单中更加常用：-
 *                                                                   请参见后面的通知一节中了解如何使得注解对象在通知体内可用
 *                                                                   。
 *                                                                   任何一个执行的方法有一个 @Transactional
 *                                                                   注解的连接点
 *                                                                   （在Spring
 *                                                                   AOP中只是方法执行）
 * @annotation（org.springframework.transaction.annotation.Transactional） 
 *                                                                       '@annotation'在绑定表单中更加常用：-
 *                                                                       请参见后面的通知一节中了解如何使得注解对象在通知体内可用
 *                                                                       。
 *                                                                       任何一个只接受一个参数
 *                                                                       ，
 *                                                                       并且运行时所传入的参数类型具有@Classified
 *                                                                       注解的连接点（
 *                                                                       在Spring
 *                                                                       AOP中只是方法执行
 *                                                                       ）
 * @args（com.xyz.security.Classified） '@args'在绑定表单中更加常用：-
 *                                    请参见后面的通知一节中了解如何使得注解对象在通知体内可用。
 *                                    任何一个在名为'tradeService'的Spring bean之上的连接点
 *                                    （在Spring AOP中只是方法执行）： bean（tradeService）
 *                                    任何一个在名字匹配通配符表达式'*Service'的Spring
 *                                    bean之上的连接点 （在Spring AOP中只是方法执行）：
 *                                    bean（*Service）
 **/
