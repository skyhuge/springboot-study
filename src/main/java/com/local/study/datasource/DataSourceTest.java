package com.local.study.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import java.util.Arrays;

public class DataSourceTest {

    public static void main(String[] args) throws Exception{
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setUrl("jdbc:mysql://localhost:3306/renren_security?useUnicode=true&characterEncoding=UTF-8");

//        DruidPooledConnection connection = dataSource.getConnection();
        Integer[] ints = new Integer[]{1,2,34,5,9};
        int count = 2;
        int len = ints.length;
        System.arraycopy(ints,2,ints,0,len - 2);
        Arrays.fill(ints,len - 2,len,null);
        for (Integer anInt : ints) {
            System.out.println(anInt);
        }
    }
}
