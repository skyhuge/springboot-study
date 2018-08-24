package com.local.study.datasource;

public class LogFilter implements MyFilter {

    @Override
    public void init(MyDataSource dataSource) {
        System.out.println("LogFilter init >>> " + dataSource.getVendor() + ":" + dataSource.getVersion());
    }

    @Override
    public void destroy() {
        System.out.println("LogFilter destroy");
    }
}
