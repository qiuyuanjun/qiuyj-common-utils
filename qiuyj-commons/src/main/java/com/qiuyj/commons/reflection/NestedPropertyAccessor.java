package com.qiuyj.commons.reflection;

import com.qiuyj.commons.ReflectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 内嵌属性访问器
 * @author qiuyj
 * @since 2018/1/5
 */
public abstract class NestedPropertyAccessor extends PropertyAccessorSupport {

  private boolean autoInstantiateNestedPropertyNullValue;

  private final Map<String, NestedProperty> nestedRootProperty;

  protected NestedPropertyAccessor() {
    autoInstantiateNestedPropertyNullValue = true;
    nestedRootProperty = new HashMap<>();
  }

  @Override
  public void setAutoInstantiateNestedPropertyNullValue(boolean autoInstantiateNestedPropertyNullValue) {
    this.autoInstantiateNestedPropertyNullValue = autoInstantiateNestedPropertyNullValue;
  }

  @Override
  protected boolean doSetProperty(String property, Object value) {
    boolean realSetValue = false;
    if (Objects.isNull(value)) {
      // 如果设置的值是null，那么移除对应的nestedPropertyValue
      nestedRootProperty.remove(property);
      realSetValue = true;
    }
    else {
      NestedProperty nestedProperty = nestedRootProperty.get(property);
      if (Objects.nonNull(nestedProperty)) {
        nestedProperty.getRoot().setProperty(nestedProperty.getNestedPropertyName(), value);
      }
      else {
        realSetValue = true;
      }
    }
    if (realSetValue) {
      doSetNestedProperty(property, value);
    }
    return realSetValue;
  }

  protected abstract void doSetNestedProperty(String nestedProperty, Object value);

  @Override
  protected Object doGetProperty(String property) {
    NestedProperty nestedProperty = nestedRootProperty.get(property);
    Object propertyValue;
    if (Objects.nonNull(nestedProperty)) {
      propertyValue = nestedProperty.getRoot().getProperty(nestedProperty.getNestedPropertyName());
    }
    else {
      propertyValue = doGetNestedProperty(property);
    }
    return propertyValue;
  }

  protected abstract Object doGetNestedProperty(String property);

  @Override
  protected String resolvePropertyName(String propertyName) {
    propertyName = super.resolvePropertyName(propertyName);
    int dotIdx = propertyName.indexOf(PropertyAccessor.NESTED_PROPERTY_SEPERATOR_STRING);
    if (dotIdx > 0) {
      String realPropertyName = propertyName.substring(0, dotIdx);
      Object realPropertyValue = getProperty(realPropertyName);
      if (Objects.isNull(realPropertyValue)) {
        if (!autoInstantiateNestedPropertyNullValue) {
          throw new IllegalStateException("Not support null nested property value");
        }
        else {
          realPropertyValue = ReflectionUtils.instantiateClass(getPropertyType(realPropertyName));
          setProperty(realPropertyName, realPropertyValue);
        }
      }
      propertyName = propertyName.substring(dotIdx + 1);
      NestedProperty nestedProperty = nestedRootProperty.get(realPropertyName);
      if (Objects.isNull(nestedProperty)) {
        PropertyAccessor nestedPropertyAccessor;
        if (realPropertyValue instanceof Map<?, ?>) {
          nestedPropertyAccessor = new MapWrapperImpl((Map<?, ?>) realPropertyValue);
        }
        else if (realPropertyValue instanceof Collection<?>) {
          nestedPropertyAccessor = new CollectionWrapperImpl((Collection<?>) realPropertyValue);
        }
        else {
          nestedPropertyAccessor = new BeanWrapperImpl<>(realPropertyValue);
        }
        nestedProperty = new NestedProperty(nestedPropertyAccessor, propertyName);
        nestedRootProperty.put(realPropertyName, nestedProperty);
      }
      else {
        nestedProperty.setNestedPropertyName(propertyName);
      }
      propertyName = realPropertyName;
    }
    else if (dotIdx == 0) {
      propertyName = propertyName.substring(1);
    }
    return propertyName;
  }
}