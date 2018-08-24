package com.local.study.datasource;

import javax.sql.PooledConnection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyDataSource extends MyAbstractDataSource {

    private String vendor;

    private String version;

    private List<MyFilter> filters = new CopyOnWriteArrayList<>();

    private ConnectionHolder[] holder ;

    private static final int max = 10;

    private static final int min = 2;

    private CountDownLatch latch = new CountDownLatch(2);

    private AtomicInteger poolSize = new AtomicInteger();

    private final Lock lock = new ReentrantLock();

    private final Condition empty = lock.newCondition();

    private final Condition full = lock.newCondition();

    @Override
    public PooledConnection getPooledConnection() throws SQLException {
        // TODO: 2018/8/23 empty.signal
        return null;
    }

    @Override
    public PooledConnection getPooledConnection(String user, String password) throws SQLException {
        return null;
    }

    public com.local.study.datasource.PooledConnection getConn(){
        if (filters.size() > 0){
            // TODO: 2018/8/24 how does tomcat filter do?
            MyFilterChainImpl chain = new MyFilterChainImpl(this);
            return chain.getConnection(this);
        }
        return getConnInternal();
    }

    public com.local.study.datasource.PooledConnection getConnInternal(){
        synchronized (this){
            for (ConnectionHolder connectionHolder : holder) {
                if (connectionHolder.getConnection() != null) {
                    return connectionHolder.getConnection();
                }
            }
        }
        return null;
    }

    public void initDataSource(){
        holder = new ConnectionHolder[max];
        for (int i = 0; i < 5; i++) {
            com.local.study.datasource.PooledConnection conn = new com.local.study.datasource.PooledConnection();
            ConnectionHolder holder = new ConnectionHolder(this,conn);
            this.holder[i] = holder;
            poolSize.incrementAndGet();
        }
        startCreateThread();
        startDestroyThread();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startCreateThread(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(;;){
                    try {
                        lock.lockInterruptibly();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    try {

                        if (poolSize.get() < max){
                            holder[poolSize.incrementAndGet()] =
                                    new ConnectionHolder(MyDataSource.this,new com.local.study.datasource.PooledConnection());
                        }else {
                            full.await();
                        }
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        lock.unlock();
                    }

                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        latch.countDown();
    }

    private void startDestroyThread(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(;;){
                    try {
                        lock.lockInterruptibly();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    try {
                        if (poolSize.get() < min){
                            empty.await();
                        }else {
                            // TODO: 2018/8/23 remove conn per 30s
                        }
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        latch.countDown();
    }


    public void addFilter(MyFilter filter){
        if (null == filter) return;
        filter.init(this);
        this.filters.add(filter);
    }

    public List<MyFilter> getFilters(){
        return filters;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
