package com.star.context;

import com.star.model.entity.User;

/**
 * 用户上下文管理类
 * 用于存储和获取当前登录用户信息
 */
public class UserContext {

    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置当前线程中的用户
     *
     * @param user 用户信息
     */
    public static void setUser(User user) {
        USER_THREAD_LOCAL.set(user);
    }

    /**
     * 获取当前线程中的用户
     *
     * @return 用户信息
     */
    public static User getUser() {
        return USER_THREAD_LOCAL.get();
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     */
    public static Integer getUserId() {
        User user = getUser();
        return user != null ? user.getId() : null;
    }

    /**
     * 清除当前线程中的用户信息
     */
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
} 