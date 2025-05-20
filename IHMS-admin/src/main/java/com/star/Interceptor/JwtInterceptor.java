package com.star.Interceptor;

import com.star.context.LocalThreadHolder;
import com.star.context.UserContext;
import com.star.mapper.UserMapper;
import com.star.common.CommonResult;
import com.star.common.Result;
import com.star.model.entity.User;
import com.star.utils.JwtUtil;
import com.alibaba.fastjson2.JSONObject;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;

/**
 * token拦截器，做请求拦截
 * 思路：用户登录成功后，会得到后端生成的 token，前端会将token存储于本地
 * 随后的接口请求，都会在协议头带上token
 * 所有请求执行之前，都会被该拦截器拦截：token校验通过则正常放行请求，否则直接返回
 *
 *  
 */
public class JwtInterceptor implements HandlerInterceptor, ApplicationContextAware {

    private static ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JwtInterceptor.applicationContext = applicationContext;
    }

    /**
     * 前置拦截
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     * @return boolean true ： 放行；false拦截
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestMethod = request.getMethod();
        // 放行预检请求
        if ("OPTIONS".equals(requestMethod)) {
            return true;
        }
        String requestURI = request.getRequestURI();
        // 登录及错误等请求不做拦截
        if (requestURI.contains("/login") || requestURI.contains("/error") || 
            requestURI.contains("/file") || requestURI.contains("/register")) {
            return true;
        }
        
        // 放行Knife4j相关路径
        if (requestURI.contains("/doc.html") || 
            requestURI.contains("/webjars") || 
            requestURI.contains("/swagger-resources") || 
            requestURI.contains("/v2/api-docs") || 
            requestURI.contains("/v3/api-docs") ||
            requestURI.endsWith("/favicon.ico")) {
            return true;
        }
        
        // 放行大模型测试接口
        if (requestURI.contains("/api/llm/test")) {
            return true;
        }
        
        String token = request.getHeader("token");
        Claims claims = JwtUtil.fromToken(token);
        // 解析不成功，直接退回！访问后续资源的可能性都没有！
        if (claims == null) {
            Result<String> error = CommonResult.error("身份认证异常，请先登录");
            response.setContentType("application/json;charset=UTF-8");
            Writer stream = response.getWriter();
            // 将失败信息输出
            stream.write(JSONObject.toJSONString(error));
            stream.flush();
            stream.close();
            return false;
        }
        Integer userId = claims.get("id", Integer.class);
        Integer roleId = claims.get("role", Integer.class);
        
        // 将解析出来的用户ID、用户角色放置于LocalThread中，当前线程可用
        LocalThreadHolder.setUserId(userId, roleId);
        
        // 同时初始化UserContext
        if (applicationContext != null) {
            try {
                UserMapper userMapper = applicationContext.getBean(UserMapper.class);
                User user = userMapper.getByActive(User.builder().id(userId).build());
                if (user != null) {
                    UserContext.setUser(user);
                }
            } catch (Exception e) {
                // 日志记录错误，但不影响请求继续
                System.err.println("初始化UserContext失败: " + e.getMessage());
            }
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成后清理线程变量，防止内存泄漏
        LocalThreadHolder.clear();
        UserContext.clear();
    }
}
