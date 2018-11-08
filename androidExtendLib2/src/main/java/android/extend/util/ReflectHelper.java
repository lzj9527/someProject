package android.extend.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectHelper
{
	public static final String TAG = "ReflectHelper";

	/**
	 * 调用对象的公有方法，包括父类方法
	 * 
	 * @param object 调用者对象
	 * @param methodName 调用的方法名，与object合在一起即为 object.methodName
	 * @param types 调用方法的参数类型
	 * @param values 调用方法的参数值
	 * @return 返回 methodName所返回的对象
	 */
	public static Object invokePublicMethod(Object object, String methodName, Class<?>[] types, Object[] values)
	{
		// 注：数组类型为:基本类型+[].class,如String[]写成 new Class<?>[]{String[].class}
		Object result = null;
		try
		{
			Class<?> classz = object.getClass();
			Method method = classz.getMethod(methodName, types);
			result = method.invoke(object, values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 调用对象的方法，可以调用非公有方法，但不包括父类的方法
	 * 
	 * @param object 调用者对象
	 * @param methodName 调用的方法名，与object合在一起即为 object.methodName
	 * @param types 调用方法的参数类型
	 * @param values 调用方法的参数值
	 * @return 返回 methodName所返回的对象
	 */
	public static Object invokeDeclaredMethod(Object object, String methodName, Class<?>[] types, Object[] values)
	{
		Object result = null;
		try
		{
			Class<?> classz = object.getClass();
			Method method = classz.getDeclaredMethod(methodName, types);
			method.setAccessible(true);// 设置安全检查，设为true使得可以访问私有方法
			result = method.invoke(object, values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 调用对象的方法，可以调用非公有方法，需声明调用类名
	 * 
	 * @param object 调用者对象
	 * @param className 调用类名，指定为父类名时即可调用父类方法
	 * @param methodName 调用的方法名，与object合在一起即为 object.methodName
	 * @param types 调用方法的参数类型
	 * @param values 调用方法的参数值
	 * @return 返回 methodName所返回的对象
	 */
	public static Object invokeDeclaredMethod(Object object, String className, String methodName, Class<?>[] types,
			Object[] values)
	{
		Object result = null;
		try
		{
			Class<?> classz = Class.forName(className);
			Method method = classz.getDeclaredMethod(methodName, types);
			method.setAccessible(true);// 设置安全检查，设为true使得可以访问私有方法
			result = method.invoke(object, values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 调用类的静态方法
	 * 
	 * @param classz 调用的类
	 * @param methodName 调用的方法名
	 * @param types 调用方法的参数类型
	 * @param values 调用方法的参数值
	 * @return 返回 methodName所返回的对象
	 */
	public static Object invokeStaticMethod(Class<?> classz, String methodName, Class<?>[] types, Object[] values)
	{
		Object result = null;
		try
		{
			Method method = classz.getDeclaredMethod(methodName, types);
			method.setAccessible(true);// 设置安全检查，设为true使得可以访问私有方法
			result = method.invoke(null, values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 调用类的静态方法
	 * 
	 * @param className 调用的类名，需包含包名
	 * @param methodName 调用的方法名
	 * @param types 调用方法的参数类型
	 * @param values 调用方法的参数值
	 * @return 返回 methodName所返回的对象
	 */
	public static Object invokeStaticMethod(String className, String methodName, Class<?>[] types, Object[] values)
	{
		try
		{
			Class<?> classz = Class.forName(className);
			return invokeStaticMethod(classz, methodName, types, values);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得对象的公有成员变量，包括父类变量
	 * 
	 * @param object 调用者对象
	 * @param fieldName 公有成员变量名
	 * @return 返回 成员对象
	 */
	public static Object getPublicFieldValue(Object object, String fieldName)
	{
		Object result = null;
		try
		{
			Class<?> classz = object.getClass();
			Field field = classz.getField(fieldName);
			result = field.get(object);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获得对象的成员变量，可以获得非公有成员，但不包括父类成员
	 * 
	 * @param object 调用者对象
	 * @param fieldName 成员变量名
	 * @return 返回 成员对象
	 */
	public static Object getDeclaredFieldValue(Object object, String fieldname)
	{
		Object result = null;
		try
		{
			Class<?> classz = object.getClass();
			Field field = classz.getDeclaredField(fieldname);
			field.setAccessible(true);
			result = field.get(object);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获得对象的变量成员，可以获得非公有成员，需声明类名
	 * 
	 * @param object 调用者对象
	 * @param className 调用类名，指定为父类名时即可获取父类成员变量
	 * @param fieldName 成员变量名
	 * @return 返回 成员对象
	 */
	public static Object getDeclaredFieldValue(Object object, String className, String fieldname)
	{
		Object result = null;
		try
		{
			Class<?> classz = Class.forName(className);
			Field field = classz.getDeclaredField(fieldname);
			field.setAccessible(true);
			result = field.get(object);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获得类的静态成员变量，可以获得非公有成员，但不包括父类成员
	 * 
	 * @param classz 调用的类
	 * @param fieldName 成员变量名
	 * @return 返回 成员对象
	 */
	public static Object getStaticFieldValue(Class<?> classz, String fieldName)
	{
		Object result = null;
		try
		{
			Field field = classz.getDeclaredField(fieldName);
			field.setAccessible(true);
			result = field.get(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获得类的静态成员变量，可以获得非公有成员，但不包括父类成员
	 * 
	 * @param className 调用的类名，需包含包名
	 * @param fieldName 成员变量名
	 * @return 返回 成员对象
	 */
	public static Object getStaticFieldValue(String className, String fieldName)
	{
		try
		{
			Class<?> classz = Class.forName(className);
			return getStaticFieldValue(classz, fieldName);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取对象公有内部类或接口
	 * */
	public static Class<?> getInnerClass(Class<?> classz, String innerClassName)
	{
		Class<?>[] classes = classz.getClasses();
		for (Class<?> innerClass : classes)
		{
			if (innerClass.getSimpleName().equals(innerClassName))
			{
				return innerClass;
			}
		}
		return null;
	}

	public static Class<?> getInnerClass(Object object, String innerClassName)
	{
		return getInnerClass(object.getClass(), innerClassName);
	}

	/**
	 * 获取对象内部类，可以获取非公有内部类
	 * */
	public static Class<?> getDeclaredInnerClass(Class<?> classz, String innerClassName)
	{
		Class<?>[] classes = classz.getDeclaredClasses();
		for (Class<?> innerClass : classes)
		{
			if (innerClass.getSimpleName().equals(innerClassName))
			{
				return innerClass;
			}
		}
		return null;
	}

	public static Class<?> getDeclaredInnerClass(Object object, String innerClassName)
	{
		return getDeclaredInnerClass(object.getClass(), innerClassName);
	}

	/**
	 * 赋值对象的公有成员变量，包括父类成员
	 * 
	 * @param object 调用者对象
	 * @param fieldName 成员变量名
	 * @param value 赋值
	 * @return 设置是否成功
	 */
	public static boolean setFieldValue(Object object, String fieldName, Object value)
	{
		try
		{
			Class<?> classz = object.getClass();
			Field field = classz.getField(fieldName);
			field.set(object, value);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 赋值对象的成员变量，可设置非公有成员，但不包括父类成员
	 * 
	 * @param object 调用者对象
	 * @param fieldName 成员变量名
	 * @param value 赋值
	 * @return 设置是否成功
	 */
	public static boolean setDeclaredFieldValue(Object object, String fieldName, Object value)
	{
		try
		{
			Class<?> classz = object.getClass();
			Field field = classz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(object, value);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 赋值对象的成员变量，可设置非公有成员，需指定类名
	 * 
	 * @param object 调用者对象
	 * @param className 调用者类名，指定为父类名时即可设置父类成员变量
	 * @param fieldName 成员变量名
	 * @param value 赋值
	 * @return 设置是否成功
	 */
	public static boolean setDeclaredFieldValue(Object object, String className, String fieldName, Object value)
	{
		try
		{
			Class<?> classz = Class.forName(className);
			Field field = classz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(object, value);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 赋值类的静态成员变量，包括私有成员，但不包括父类成员
	 * 
	 * @param classz 调用的类
	 * @param fieldName 成员变量名
	 * @param value 赋值
	 * @return 设置是否成功
	 */
	public static boolean setStaticFieldValue(Class<?> classz, String fieldName, Object value)
	{
		try
		{
			Field field = classz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(null, value);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 赋值类的静态成员变量，包括私有成员，但不包括父类成员
	 * 
	 * @param className 调用的类名，需包含包名
	 * @param fieldName 成员变量名
	 * @param value 赋值
	 * @return 设置是否成功
	 */
	public static boolean setStaticFieldValue(String className, String fieldName, Object value)
	{
		try
		{
			Class<?> classz = Class.forName(className);
			return setStaticFieldValue(classz, fieldName, value);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 生成类实例
	 * 
	 * @param classz 类
	 * @param types 构造参数类型
	 * @param values 构造参数值
	 * @return 返回类实例
	 */
	public static Object newInstance(Class<?> classz, Class<?>[] types, Object[] values)
	{
		Object result = null;
		try
		{
			Constructor<?> constructor = classz.getDeclaredConstructor(types);// 定位构造方法
			constructor.setAccessible(true);// 设置安全检查，能够访问私有构造函数
			result = constructor.newInstance(values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 生成类实例
	 * 
	 * @param classz 类
	 * @return 返回类实例
	 */
	public static Object newInstance(Class<?> classz)
	{
		try
		{
			return classz.newInstance();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 生成类实例
	 * 
	 * @param className 类名，需包含包名
	 * @param types 构造参数类型
	 * @param values 构造参数值
	 * @return 返回类实例
	 */
	public static Object newInstance(String className, Class<?>[] types, Object[] values)
	{
		try
		{
			Class<?> classz = Class.forName(className);
			return newInstance(classz, types, values);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 生成类实例
	 * 
	 * @param className 类名，需包含包名
	 * @return 返回类实例
	 */
	public static Object newInstance(String className)
	{
		try
		{
			Class<?> classz = Class.forName(className);
			return newInstance(classz);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static Object newArrayInstance(Class<?> classz, int length)
	{
		return Array.newInstance(classz, length);
	}

	public static Object newArrayInstance(String className, int length)
	{
		Class<?> classz;
		try
		{
			classz = Class.forName(className);
			return newArrayInstance(classz, length);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断类是否支持指定的方法
	 * 
	 * @param classz 指定的类
	 * @param methodName 指定的方法名
	 * @param types 方法的参数类型
	 * @return boolean
	 */
	public static boolean isMethodSupported(Class<?> classz, String methodName, Class<?>[] types)
	{
		try
		{
			classz.getDeclaredMethod(methodName, types);
			return true;
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断类是否支持指定的方法
	 * 
	 * @param className 指定的类名，需包含包名
	 * @param methodName 指定的方法名
	 * @param types 方法的参数类型
	 * @return boolean
	 */
	public static boolean isMethodSupported(String className, String methodName, Class<?>[] types)
	{
		try
		{
			Class<?> classz = Class.forName(className);
			return isMethodSupported(classz, methodName, types);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断对象是否支持指定的方法
	 * 
	 * @param object 指定的对象
	 * @param methodName 指定的方法名
	 * @param argClass 方法的参数类型
	 * @return boolean
	 */
	public static boolean isMethodSupported(Object object, String methodName, Class<?>[] types)
	{
		try
		{
			Class<?> classz = object.getClass();
			return isMethodSupported(classz, methodName, types);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断指定的类是否存在
	 * 
	 * @param className 指定的类名，需包含包名
	 * @return boolean
	 */
	public static boolean isClassExistsed(String className)
	{
		try
		{
			Class.forName(className);
			return true;
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isInstanceOf(Class<?> classz, Class<?> instanceClass)
	{
		do
		{
			if (classz.equals(instanceClass))
			{
				return true;
			}
		}
		while ((classz = classz.getSuperclass()) != null);
		return false;
	}

	public static boolean isInstanceOf(String className, String instanceClassName)
	{
		try
		{
			Class<?> classz = Class.forName(className);
			Class<?> parentClass = Class.forName(instanceClassName);
			return isInstanceOf(classz, parentClass);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isInstanceOf(Object object, String instanceClassName)
	{
		try
		{
			Class<?> classz = object.getClass();
			Class<?> parentClass = Class.forName(instanceClassName);
			return isInstanceOf(classz, parentClass);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
