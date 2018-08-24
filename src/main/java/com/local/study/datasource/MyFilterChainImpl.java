package com.local.study.datasource;

public class MyFilterChainImpl implements MyFilterChain {

    private MyDataSource dataSource;

    private int filterSize;

    private int position;

    public MyFilterChainImpl(MyDataSource dataSource){
        this.dataSource = dataSource;
        this.filterSize = this.dataSource.getFilters().size();
    }

    public PooledConnection getConnection(MyDataSource dataSource){
        if (position < filterSize){
            return nextFilter().getConnection(this,dataSource);
        }
        return dataSource.getConn();
    }

    public MyFilter nextFilter(){
        return this.dataSource.getFilters().get(position++);
    }

    @Override
    public MyDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public int getFilterSize() {
        return filterSize;
    }
}
