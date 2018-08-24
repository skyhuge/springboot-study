package com.local.study.datasource;

public class ConnectionHolder {

    private MyDataSource dataSource;

    private PooledConnection connection;

    public ConnectionHolder(MyDataSource dataSource, PooledConnection connection){
        this.dataSource = dataSource;
        this.connection = connection;
    }

    public MyDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(MyDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PooledConnection getConnection() {
        return connection;
    }

    public void setConnection(PooledConnection connection) {
        this.connection = connection;
    }
}
