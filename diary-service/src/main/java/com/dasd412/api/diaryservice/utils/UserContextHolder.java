package com.dasd412.api.diaryservice.utils;


public class UserContextHolder {

    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();

    public static UserContext getContext() {
        UserContext context = userContext.get();

        if (context == null) {
            context = createEmptyContext();
            setUserContext(context);
        }
        return userContext.get();
    }

    public static void setUserContext(UserContext context) {
        userContext.set(context);
    }

    private static UserContext createEmptyContext() {
        return new UserContext();
    }
}
