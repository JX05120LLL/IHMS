package com.star.annotation;

import com.star.model.dto.query.base.QueryDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PagerAspect {

    /**
     * 环绕通知，用于处理带有@Pager注解的方法
     *
     * @param joinPoint 连接点
     * @param pager     注解实例
     * @return 原方法执行的结果
     * @throws Throwable 异常
     */
    @Around("@annotation(pager)")
    public Object handlePageableParams(ProceedingJoinPoint joinPoint, Pager pager) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof QueryDto) {
                QueryDto queryDTO = (QueryDto) arg;
                // 不需要做任何转换，MyBatis Plus 分页插件需要的是页码从1开始
                // 如果 current 为 null 或 size 为 null，设置默认值
                if (queryDTO.getCurrent() == null) {
                    queryDTO.setCurrent(1);
                }
                if (queryDTO.getSize() == null) {
                    queryDTO.setSize(10);
                }
            }
        }
        return joinPoint.proceed(args);
    }
}
