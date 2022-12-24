package com.dasd412.api.diaryservice.common.utils.trace;


import static com.google.common.base.Preconditions.checkNotNull;

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
        checkNotNull(context,"User context must be not null!!");
        userContext.set(context);
    }

    private static UserContext createEmptyContext() {
        return new UserContext();
    }
}
