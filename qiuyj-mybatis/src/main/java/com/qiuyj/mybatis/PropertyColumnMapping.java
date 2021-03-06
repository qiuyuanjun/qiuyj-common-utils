package com.qiuyj.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author qiuyj
 * @since 2017/11/15
 */
public class PropertyColumnMapping {

  private String javaClassPropertyName;

  private String databaseColumnName;

  // @Nullable
  private Object value;

  private TypeHandler typeHandler;

  // @Nullable
  private JdbcType jdbcType;

  // @Nullable
  private Class<?> targetClass;

  public PropertyColumnMapping() {}

  public PropertyColumnMapping(String javaClassPropertyName, String databaseColumnName) {
    this.javaClassPropertyName = javaClassPropertyName;
    this.databaseColumnName = databaseColumnName;
  }

  public PropertyColumnMapping(String javaClassPropertyName, String databaseColumnName, Object value) {
    this(javaClassPropertyName, databaseColumnName);
    this.value = value;
  }

  public PropertyColumnMapping(String javaClassPropertyName, String databaseColumnName, TypeHandler typeHandler) {
    this(javaClassPropertyName, databaseColumnName);
    this.typeHandler = typeHandler;
  }

  public PropertyColumnMapping(String javaClassPropertyName, String databaseColumnName, TypeHandler typeHandler, JdbcType jdbcType) {
    this(javaClassPropertyName, databaseColumnName, typeHandler);
    this.jdbcType = jdbcType;
  }

  public PropertyColumnMapping(String javaClassPropertyName, String databaseColumnName, Class<?> targetType) {
    this(javaClassPropertyName, databaseColumnName);
    this.targetClass = targetType;
  }

  public String getJavaClassPropertyName() {
    return javaClassPropertyName;
  }

  public void setJavaClassPropertyName(String javaClassPropertyName) {
    this.javaClassPropertyName = javaClassPropertyName;
  }

  public String getDatabaseColumnName() {
    return databaseColumnName;
  }

  public void setDatabaseColumnName(String databaseColumnName) {
    this.databaseColumnName = databaseColumnName;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public TypeHandler getTypeHandler() {
    return typeHandler;
  }

  public void setTypeHandler(TypeHandler typeHandler) {
    this.typeHandler = typeHandler;
  }

  public JdbcType getJdbcType() {
    return jdbcType;
  }

  public void setJdbcType(JdbcType jdbcType) {
    this.jdbcType = jdbcType;
  }

  public Class<?> getTargetClass() {
    return targetClass;
  }

  public void setTargetClass(Class<?> targetClass) {
    this.targetClass = targetClass;
  }

  @Override
  public String toString() {
    return databaseColumnName + " AS " + javaClassPropertyName;
  }
}