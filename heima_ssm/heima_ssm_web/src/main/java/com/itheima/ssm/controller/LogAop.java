package com.itheima.ssm.controller;

import com.itheima.ssm.dao.SysLog;
import com.itheima.ssm.service.ISysLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
public class LogAop {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ISysLogService sysLogService;

    private Date visitTime; //开始时间
    private Class clazz;    //访问的类
    private Method method;  //访问的方法

    //前置通知产要中获取开始是境，执行的类是那一个，执行的是哪一个方法
    @Before("execution(* com.itheima.ssm.controller.*.*(..))")
    public void doBefore(JoinPoint jp) throws NoSuchMethodException {
        visitTime=new Date();   //当前时间就是访问时间
        clazz=jp.getTarget().getClass();//具体要访问的类
        String methodName=jp.getSignature().getName();  //获取访问方法的名称
        Object[] args=jp.getArgs();
        if(args==null||args.length==0){
            method=clazz.getMethod(methodName);
        }else{
            Class[] classArgs=new Class[args.length];
            for (int i=0;i<args.length;i++){
                classArgs[i]=args[i].getClass();
            }
            clazz.getMethod(methodName,classArgs);
        }
    }
    //后置通知
    @After("execution(* com.itheima.ssm.controller.*.*(..))")
    public void doAfter(JoinPoint jp) throws Exception {
        long time=new Date().getTime()-visitTime.getTime(); //获取访问的时长
        String url="";

        //获取url
        if(clazz!=null && method!=null && clazz!=LogAop.class){
            //1.获取类上的RequestMapping("/orders");
            RequestMapping classAnnotaion= (RequestMapping) clazz.getAnnotation(RequestMapping.class);
            if(classAnnotaion!=null){
                String[] classValue=classAnnotaion.value();
                //2.获取方法上的@RequestMapping(xxx)
                RequestMapping methodAnnotation=method.getAnnotation(RequestMapping.class);
                if(methodAnnotation!=null){
                    String[] methodValue=methodAnnotation.value();

                    url=classValue[0]+methodValue[0];

                    //获取访问的IP地址
                    String ip=request.getRemoteAddr();

                    //获取当前的操作者
                    SecurityContext context= SecurityContextHolder.getContext();//从上下文中获了当前登录的用户
                    User user= (User) context.getAuthentication().getPrincipal();
                    String username=user.getUsername();

                    //将日志对象相关信息封装到SysLog对象
                    SysLog sysLog=new SysLog();
                    sysLog.setExecutionTime(time);
                    sysLog.setIp(ip);
                    sysLog.setMethod("[类名] " + clazz.getName() + "[方法名] " + method.getName());
                    sysLog.setUrl(url);
                    sysLog.setUsername(username);
                    sysLog.setVisitTime(visitTime);

                    //调用Service完成操作
                    sysLogService.save(sysLog);
                }
            }
        }


    }
}
