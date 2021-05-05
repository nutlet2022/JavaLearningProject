package com.jdbctools.test;

import com.jdbctools.enity.Account;
import com.jdbctools.enity.Student;
import com.jdbctools.utils.C3p0Tools;
import com.jdbctools.utils.JDBCTools;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {
//        C3p0Tools c3p0Tools = new C3p0Tools();
//        Connection connection =c3p0Tools.getConnection();
//
//        String sql = "select * from account where id = 1";
//        PreparedStatement statement = connection.prepareStatement(sql);
//        ResultSet resultSet = statement.executeQuery();
//
//        if (resultSet.next()) {
//            int id = resultSet.getInt(1);
//            String name = resultSet.getString(2);
//            Account account = new Account(id,name);
//            System.out.println(account);
//        }

//        c3p0Tools.release(connection,statement,resultSet);
        //返回的是代理对象，实现对连接对象的复用
//        System.out.println(connection);


        Connection connection = C3p0Tools.getConnection();
        String sql = "select * from students where age = 20";
//        JDBCTools<Account> jdbcTools = new JDBCTools<>();
//        Account bean = jdbcTools.getBean(connection,sql,Account.class);
//        System.out.println(bean);

        JDBCTools<Student> jdbcTools = new JDBCTools<>();
        List<Student> students = jdbcTools.getBeans(connection,sql,Student.class);
        for (Student student : students) {
            System.out.println(student);
        }
    }
}
