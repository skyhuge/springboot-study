package com.local.study.datasource;

public interface MyFilterChain {

    PooledConnection getConnection(MyDataSource dataSource);
    MyDataSource getDataSource();
    int getFilterSize();
}
