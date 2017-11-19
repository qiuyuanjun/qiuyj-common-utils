package com.qiuyj.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 反射工具类
 * @author qiuyj
 * @since 2017/11/18
 */
public abstract class ReflectionUtils {

  /**
   * 得到泛型的实际所代表的类型
   */
  public static Class<?>[] getParameterizedTypesAsClass(Class<?> cls) {
    Set<Class<?>> sets = getParameterizedTypesAsClassAsSet(cls);
    return sets.toArray(new Class<?>[sets.size()]);
  }

  /**
   * 得到泛型的实际所代表的类型
   */
  public static Set<Class<?>> getParameterizedTypesAsClassAsSet(Class<?> cls) {
    Objects.requireNonNull(cls);
    Set<Class<?>> sets = new LinkedHashSet<>();
    addParameterizedTypes(cls.getGenericSuperclass(), sets);
    Type[] inters = cls.getGenericInterfaces();
    for (Type inter : inters) {
      addParameterizedTypes(inter, sets);
    }
    return sets;
  }

  private static void addParameterizedTypes(Type type, Set<Class<?>> sets) {
    if (type instanceof ParameterizedType) {
      sets.addAll(
          Arrays.stream(((ParameterizedType) type).getActualTypeArguments())
                .filter(t -> t instanceof Class<?>)
                .<Class<?>>map(t -> (Class<?>) t)
                .collect(Collectors.toList())
      );
    }
  }

  /**
   * 判断是否有给定参数类型的构造函数，如果有返回true，否则返回false
   */
  public static boolean hasConstructor(Class<?> cls, Class<?>... paramTypes) {
    return Objects.nonNull(getConstructorIfAvaliable(cls, paramTypes));
  }

  /**
   * 得到给定参数类型的构造函数，如果该构造函数不存在，那么返回null
   */
  public static Constructor<?> getConstructorIfAvaliable(Class<?> cls, Class<?>... paramTypes) {
    Objects.requireNonNull(cls);
    Constructor<?> ctor = null;
    try {
      ctor = cls.getDeclaredConstructor(paramTypes);
    } catch (NoSuchMethodException e) {
      // ignore
    }
    return ctor;
  }

  /**
   * 得到给定参数类型的构造函数，如果该构造函数不存在，那么抛出异常
   */
  public static Constructor<?> getConstructor(Class<?> cls, Class<?>... paramTypes) {
    Objects.requireNonNull(cls);
    Constructor<?> ctor;
    try {
      ctor = cls.getDeclaredConstructor(paramTypes);
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException("Can not find constructor with parameter types: " + Arrays.toString(paramTypes));
    }
    return ctor;
  }

  /**
   * 得到默认的无参构造函数，如果没有，那么返回null
   */
  public static Constructor<?> getDefaultConstructorIfAvaliable(Class<?> cls) {
    return getConstructorIfAvaliable(cls);
  }

  /**
   * 得到默认的无参构造函数，如果没有，那么抛出异常
   */
  public static Constructor<?> getDefaultConstructor(Class<?> cls) {
    try {
      return getConstructor(cls);
    } catch (IllegalStateException e) {
      throw new IllegalStateException("There are no default constructor in: " + cls, e);
    }
  }

  /**
   * 得到给定的方法Method对象，该方法会遍历所有的父类的方法
   * 如果在父类里面仍然没有得到Method对象，那么抛出异常
   */
  public static Method getDeclaredMethod(Class<?> cls, String methodName, Class<?>... paramTypes) {
    while (Objects.nonNull(cls)) {
      try {
        return cls.getDeclaredMethod(methodName, paramTypes);
      } catch (NoSuchMethodException e) {
        // ignore;
      }
      cls = cls.getSuperclass();
    }
    throw new IllegalStateException("Cannot find method: " + methodName + " with parameter: " + Arrays.toString(paramTypes));
  }
}