package com.local.study.datasource;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class PooledConnection implements javax.sql.PooledConnection{

    private AtomicInteger atom = new AtomicInteger();

    private int id;

    public PooledConnection getPooledConnection(){
        PooledConnection conn = new PooledConnection();
        conn.setId(atom.incrementAndGet());
        return conn;
    }

    public Connection getConnection(){
        return null;
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {

    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {

    }

    @Override
    public void addStatementEventListener(StatementEventListener listener) {

    }

    @Override
    public void removeStatementEventListener(StatementEventListener listener) {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
