package util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Extract values from request parameter map, along with annotated parameters for a method.
 * extracted values will be passed to that method on invocation.
 */
public class Extractor {
	
	private static void debug(String msg) {
		System.out.println("DEBUG: " + msg);
	}
	
	public static Collection<Object> extract(final Map<String,Object> input, final Method method) {
		List<Object> args = new ArrayList<Object>();
		
		final Annotation[][] annotations = method.getParameterAnnotations();
		final Class<?>[] parameterTypes = method.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			final Class<?> requiredType = parameterTypes[i];
			debug("checking param #" + i + " type:" + requiredType + " annotations:" + annotations[i].length);
			
			for (Annotation annon : annotations[i]) {
				debug("checking param #" + i + " annotation" + annon);
				final Class<?> annonType = annon.annotationType();
				if (annonType != Param.class && annonType != OptionalParam.class) {
					continue;
				}

				final String paramName = (annonType == Param.class) 
						? ((Param)annon).value() 
						: ((OptionalParam)annon).value();
				
				Object paramValue = input.get(paramName);
				Object value = checkValue(paramValue, paramName, requiredType, annonType == OptionalParam.class);
				
				debug("found param " + paramName + " type:" + requiredType.getName() + " value:" + value);
				args.add(value);
				break;
			}
		}
		return args;
	}

	/**
	 * Check if given string is compatible to given class, then convert it to the class
	 * @return
	 */
	private static Object checkValue(Object paramValue, String paramName, Class<?> requiredType, boolean isOptional) {
		if (paramValue == null) {
			if (!isOptional) {
				throw new IllegalArgumentException("param " + paramName + 
						" has incompatible type. expected:" + requiredType.getName() + 
						" found: null");
			} 
			debug("found param " + paramName + " type:" + requiredType.getName() + " value: null");
			return null;
		}

		Object value =  
			  (paramValue instanceof String)   ? stringToX((String)paramValue, requiredType) 
			: (paramValue instanceof String[]) ? stringArrayToX((String[])paramValue, requiredType)
			: null;

		if (value == null) {
			throw new IllegalArgumentException("param " + paramName + 
					" has unknown value: " + paramValue);
		}

		return value;
	}
	
	/**
	 * Translate to various types from represented in String form
	 * 
	 * @param value
	 * @param x
	 * @return
	 */
	private static Object stringToX(String value, Class<?> x) {
		if (value == null) {
			return null;
		}
		if (x == String.class) {
			return value;
		}
		if (x == Integer.class || x == int.class) {
			return Integer.valueOf(value);
		}
		if (x == Long.class || x == long.class) {
			return Long.valueOf(value);
		}
		if (x == Boolean.class || x == boolean.class) {
			return Boolean.valueOf(value);
		}

		throw new IllegalArgumentException("incompatible type. expected:" + x.getName() + 
					" found: " + value.getClass().getName());
	}
	
	/**
	 * Translate to various array types from represented in String form
	 * @param value
	 * @param x
	 * @return
	 */
	private static Object stringArrayToX(String[] values, Class<?> x) {
		if (values == null) {
			return null;
		}
		
		if (x.isArray()) {
			return stringArrayToXArray(values, x.getComponentType());
		} else {
			throw new IllegalArgumentException("incompatible array input param for argument class: " + x.getName());
		}
	}
	
	private static Object[] stringArrayToXArray(String[] values, Class<?> x) {
		if (x == String.class) {
			return values;
		}
		if (x == Integer.class || x == int.class) {
			Integer[] result = new Integer[values.length];
			for (int i=0; i < values.length; i++) {
				result[i] = Integer.valueOf(values[i]);
			}
			return result;
		}
		if (x == Long.class || x == long.class) {
			Long[] result = new Long[values.length];
			for (int i=0; i < values.length; i++) {
				result[i] = Long.valueOf(values[i]);
			}
			return result;
		}
		if (x == Boolean.class || x == boolean.class) {
			Boolean[] result = new Boolean[values.length];
			for (int i=0; i < values.length; i++) {
				result[i] = Boolean.valueOf(values[i]);
			}
			return result;
		} else {
			throw new IllegalArgumentException("incompatible array input param for argument class: " + x.getName());
		}
	}
}
