package com.local.study.datasource;

public class StatFilter implements MyFilter {

    @Override
    public PooledConnection getConnection(MyFilterChain chain, MyDataSource dataSource) {
        PooledConnection connection = chain.getConnection(dataSource);
        if (null != connection){
            onConnect();
        }
        return connection;
    }

    @Override
    public void init(MyDataSource dataSource) {
        System.out.println("StatFilter init >>> " + dataSource.getVendor() + ":" + dataSource.getVersion());
    }

    @Override
    public void destroy() {
        System.out.println("StatFilter destroy ");
    }

    @Override
    public void onConnect() {
        System.out.println("StatFilter getConnection, go on...");
    }

    @Override
    public void onClosed() {

    }
}
