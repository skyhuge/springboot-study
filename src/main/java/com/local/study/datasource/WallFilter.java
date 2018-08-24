package com.local.study.datasource;

public class WallFilter implements MyFilter {

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
        System.out.println("WallFilter init >>> " + dataSource.getVendor() + ":" + dataSource.getVersion());
    }

    @Override
    public void destroy() {
        System.out.println("WallFilter destroy");
    }

    @Override
    public void onConnect() {
        System.out.println("WallFilter getConnection, go on... ");
    }

    @Override
    public void onClosed() {

    }
}
