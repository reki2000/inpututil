package util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public class AppBase {

	protected static void debug(String msg) {
		System.out.println("DEBUG: " + msg);
	}
	
	protected String exec(String methodName, Map<String, Object> input) {
		debug("searching in class:" + this.getClass().getName());
		for (Method method : this.getClass().getMethods()) {
			if (method.getName().equals(methodName)) {
				Collection<Object> args = Extractor.extract(input, method);
				try {
					return (String) method.invoke(this, args.toArray());
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return "NG";
	}
	
}
