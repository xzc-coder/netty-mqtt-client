package io.github.netty.mqtt.client.plugin;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * cglib拦截器实现
 * @author: xzc-coder
 */
public class CglibMethodInterceptor extends BaseMethodInterceptor implements MethodInterceptor {


    public CglibMethodInterceptor(List<Interceptor> interceptors, Object target) {
        super(interceptors, target);
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        //是否拦截该方法
        boolean invoke = this.invokeInterceptMethod(method);
        Method targetMethod = getTargetMethod(method);
        if (invoke) {
            //创建方法调用器
            Invocation invocation = new Invocation(this.target, targetMethod, args, new InterceptorChain(this.interceptors));
            //执行方法
            return invocation.proceed();
        } else {
            //不拦截则调用代理对象的方法
            return targetMethod.invoke(this.target, args);
        }
    }

    /**
     * 获取代理对象的方法
     *
     * @param method 原始的方法
     * @return 代理对象方法
     * @throws NoSuchMethodException
     */
    private Method getTargetMethod(Method method) throws NoSuchMethodException {
        return target.getClass().getMethod(method.getName(), method.getParameterTypes());
    }
}
