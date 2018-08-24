package com.local.study.datasource;

public interface MyFilter {

    PooledConnection getConnection(MyFilterChain chain, MyDataSource dataSource);
    void init(MyDataSource dataSource);
    void destroy();
    void onConnect();
    void onClosed();
}
