package com.qiuyj.commons.reflection.propertyconverter;

import com.qiuyj.commons.ClassUtils;
import com.qiuyj.commons.reflection.PropertyConverter;

/**
 * @author qiuyj
 * @since 2018/1/4
 */
public class ClassPropertyConverter implements PropertyConverter {

  @Override
  public Object getConvertedPropertyValue(String strValue) {
    return ClassUtils.resolveClassName(strValue, null);
  }

  @Override
  public String asString(Object value) {
    if (value instanceof Class) {
      return ((Class<?>) value).getName();
    }
    else {
      throw new IllegalStateException("Not a Class object");
    }
  }
}