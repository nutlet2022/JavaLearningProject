package com.jdbctools.utils;

import com.jdbctools.enity.Account;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCTools <T> {
    /**
     * 普通单个对象查询
     * @param connection
     * @param sql
     * @param clazz
     * @return
     */
    public T getBean(Connection connection,String sql,Class clazz) {
        //查询sql
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            //执行sql
            statement = connection.prepareStatement(sql);
            //解析结果集
            resultSet = statement.executeQuery();

            //解析结果集
            return parseBean(resultSet, clazz);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            C3p0Tools.release(connection,statement,resultSet);
        }

        return null;
    }


    /**
     * 可传参数的对象查询
     * @param connection
     * @param sql
     * @param clazz
     * @param params
     * @return
     */
    public T getBean(Connection connection, String sql, Class clazz, Object... params) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;


        try {
            statement = connection.prepareStatement(sql);
            //填充sql参数
            fillParams(statement, params);

            resultSet = statement.executeQuery();

            //解析结果集
            return parseBean(resultSet, clazz);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            C3p0Tools.release(connection,statement,resultSet);
        }

        return null;
    }


    /**
     * 集合查询
     * @param connection
     * @param sql
     * @param clazz
     * @return
     */
    public List<T> getBeans(Connection connection, String sql, Class clazz) {
        //查询sql
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<T> list = new ArrayList<>();

        try {
            //执行sql
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            //解析结果集
            resultSet = statement.executeQuery();

            return parseBeans(resultSet,clazz);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            C3p0Tools.release(connection,statement,resultSet);
        }
        return null;
    }


    /**
     * 带有可变参数的集合查询
     * @param connection
     * @param sql
     * @param clazz
     * @param params
     * @return
     */
    public List<T> getBeans(Connection connection, String sql, Class clazz, Object... params) {
        //查询sql,
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<T> list = new ArrayList<>();

        try {
            //执行sql
            statement = connection.prepareStatement(sql);

            //填充 sql 参数
            fillParams(statement,params);

            resultSet = statement.executeQuery();

            return parseBeans(resultSet,clazz);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

         finally {
            C3p0Tools.release(connection,statement,resultSet);
        }

        return null;
    }

    /**
     * 填充 sql 参数
     * @param statement
     * @param params
     */
    public void fillParams(PreparedStatement statement, Object... params) {
        try {
            for (int i = 1; i <= params.length; i ++) {
                Object param = params[i - 1];
                //使用反射机制获取参数类型
                String typeName = param.getClass().getTypeName();

                switch (typeName) {
                    case "java.lang.Integer":
                        statement.setInt(i,(Integer)param);
                        break;
                    case "java.lang.String":
                        statement.setString(i,(String)param);
                        break;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    /**
     * 解析结果对象
     * @param resultSet
     * @param clazz
     * @return
     */
    public T parseBean(ResultSet resultSet,Class clazz) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();        //结果结合的字段数

            Object object = clazz.getConstructor(null).newInstance(null);
            if (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnType = metaData.getColumnTypeName(i);

                    Object value = null;
                    switch (columnType) {
                        case "INT":
                            value = resultSet.getInt(columnName);
                            break;
                        case "VARCHAR":
                            value = resultSet.getString(columnName);
                            break;
                    }
                    //给属性赋值
                    //获取 setter 方法
                    String methodName = "set" + columnName.substring(0,1).toUpperCase() + columnName.substring(1);

                    //因为成员是声明时为私有变量，所以采用 getdeclaredField
                    Field field = clazz.getDeclaredField(columnName);
                    Method method = clazz.getMethod(methodName,field.getType());
                    method.invoke(object, value);
                }
            }
            return (T)object;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解析结果集合
     * @param resultSet
     * @param clazz
     * @return
     */
    public List<T> parseBeans(ResultSet resultSet,Class clazz) {
        List<T> list = new ArrayList<>();
        //解析结果集
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();        //结果结合的字段数
            while (resultSet.next()) {
                Object object = clazz.getConstructor(null).newInstance(null);
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnType = metaData.getColumnTypeName(i);

                    Object value = null;
                    switch (columnType) {
                        case "INT":
                            value = resultSet.getInt(columnName);
                            break;
                        case "VARCHAR":
                            value = resultSet.getString(columnName);
                            break;
                    }
                    //给属性赋值
                    //获取 setter 方法
                    String methodName = "set" + columnName.substring(0,1).toUpperCase() + columnName.substring(1);

                    //因为成员是声明时为私有变量，所以采用 getdeclaredField
                    Field field = clazz.getDeclaredField(columnName);
                    Method method = clazz.getMethod(methodName,field.getType());
                    method.invoke(object, value);
                }
                list.add((T)object);
            }
            return list;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
