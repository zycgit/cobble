package net.hasor.cobble.dynamic.anno;
import net.hasor.cobble.dynamic.MethodInterceptor;
import net.hasor.cobble.dynamic.MethodInvocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonAopInterceptor implements MethodInterceptor {
    private Map<String, List<String>> callInfo = new HashMap<>();

    public Map<String, List<String>> getCallInfo() {
        return callInfo;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        invocation.getMethod();
        invocation.getArguments();
        invocation.getThis();
        //
        String methodName = invocation.getMethod().getName();
        List<String> stringList = callInfo.get(methodName);
        if (stringList == null) {
            stringList = new ArrayList<>();
            callInfo.put(methodName, stringList);
        }
        //
        try {
            stringList.add("BEFORE");
            Object proceed = invocation.proceed();
            stringList.add("AFTER");
            return proceed;
        } catch (Exception e) {
            stringList.add("THROW");
            throw e;
        }
    }
}
