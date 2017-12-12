package com.qiuyj.tools.mybatis.build.customer;

import com.qiuyj.tools.mybatis.SqlInfo;
import com.qiuyj.tools.mybatis.build.ParameterResolver;
import com.qiuyj.tools.mybatis.build.SqlProvider;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author qiuyj
 * @since 2017/12/11
 */
public class OracleBatchInsertParameterObjectResolver implements CustomizedParameterObjectResolver {
  private static OracleBatchInsertParameterObjectResolver INSTANCE = new OracleBatchInsertParameterObjectResolver();
  private static volatile SqlInfo sqlInfo;
  private static volatile Configuration config;

  private final OracleBatchInsertTypeHandler batchInsertTypeHandler;

  private OracleBatchInsertParameterObjectResolver() {
    batchInsertTypeHandler = new OracleBatchInsertTypeHandler();
  }

  public static OracleBatchInsertParameterObjectResolver getInstance() {
    return INSTANCE;
  }

  @Override
  public List<ParameterMapping> resolveParameterObject(Configuration config, SqlInfo sqlInfo, Object paramObj, SqlNode sqlNode) {
    List list = (List) ParameterResolver.resolveParameter(paramObj).getParameterValues()[0];
    if (list.isEmpty())
      throw new IllegalArgumentException("Method batchInsert parameter can not be an empty list");
    else {
      this.sqlInfo = sqlInfo;
      this.config = config;
      String join = " INTO " + sqlInfo.getTableName();
      StringBuilder eachBuilder = new StringBuilder();
      StringJoiner joiner = new StringJoiner(",", "(", ")");
      List<ParameterMapping> instanceParameterMappings = new ArrayList<>(sqlInfo.getFieldCount());
      ParameterMapping canonic = new ParameterMapping.Builder(
          config,
          "list",
          batchInsertTypeHandler
      ).build();
      for (String column : sqlInfo.getAllColumnsWithoutAlias()) {
        joiner.add(column);
        instanceParameterMappings.add(canonic);
      }
      eachBuilder.append(joiner.toString());
      eachBuilder.append(" VALUES ");
      joiner = new StringJoiner(",", "(", ")");
      List<ParameterMapping> parameterMappings = new ArrayList<>(sqlInfo.getFieldCount() * list.size());
      for (int idx = 0; idx < sqlInfo.getFieldCount(); idx++) {
        joiner.add(SqlProvider.PREPARE_FLAG);
        parameterMappings.addAll(instanceParameterMappings);
      }
      eachBuilder.append(joiner.toString());
      joiner = new StringJoiner(join);
      for (int idx = 0; idx < list.size(); idx++) {
        joiner.add(eachBuilder.toString());
      }
      String sql = join + joiner.toString() + " SELECT 1 FROM DUAL";
      resetStaticSqlNode((StaticTextSqlNode) sqlNode, sql);
      return parameterMappings;
    }
  }

  public static final class OracleBatchInsertTypeHandler extends BaseTypeHandler<List> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List parameter, JdbcType jdbcType) throws SQLException {
      // 首先判断当前占位符是属于List集合的第几个元素
      int idx = i - 1;
      int instanceIdx = idx / OracleBatchInsertParameterObjectResolver.sqlInfo.getFieldCount();
      // 得到当前设置的值是实体类里面属性的第几个位置
      int parameterIdx = idx % OracleBatchInsertParameterObjectResolver.sqlInfo.getFieldCount();
      // 得到当前设置的实体类
      Object obj = parameter.get(instanceIdx);
      // 得到对应实体类的反射对象（MetaObject）方便后面获取值
      MetaObject objMeta = OracleBatchInsertParameterObjectResolver.config.newMetaObject(obj);
      String name = OracleBatchInsertParameterObjectResolver.sqlInfo.getPropertyColumnMappings().get(parameterIdx).getJavaClassPropertyName();
      ps.setObject(i, objMeta.getValue(name));
    }

    @Override
    public List getNullableResult(ResultSet rs, String columnName) throws SQLException {
      return null;
    }

    @Override
    public List getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
      return null;
    }

    @Override
    public List getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
      return null;
    }
  }
}
