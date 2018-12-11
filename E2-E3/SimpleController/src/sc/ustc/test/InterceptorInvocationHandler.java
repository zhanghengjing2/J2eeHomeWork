package sc.ustc.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * Interceptor拦截器的代理类
 * @author zhang
 *
 */
public class InterceptorInvocationHandler<T> implements InvocationHandler {

	private T target;//invocationHandler持有的被代理对象
	public InterceptorInvocationHandler(T target)
	{
		this.target=target;
	}
	/**
     * proxy:代表动态代理对象
     * method：代表正在执行的方法
     * args：代表调用目标方法时传入的实参
     */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		System.out.println("代理执行" +method.getName() + "方法");
		
		Object result = method.invoke(target, args);
		
		return result;
		//return null;
	}

}
