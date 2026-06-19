package com.guardian.security;

import com.guardian.common.BusinessException;

public final class SecurityContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private SecurityContext() {
    }

    public static void set(LoginUser loginUser) {
        HOLDER.set(loginUser);
    }

    public static LoginUser get() {
        LoginUser loginUser = HOLDER.get();
        if (loginUser == null) {
            throw new BusinessException(401, "用户未登录");
        }
        return loginUser;
    }

    public static Long userId() {
        return get().getUserId();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
