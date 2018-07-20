package com.local.study.async;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.function.Consumer;

public class DeferredResultWithWatcher<T> extends DeferredResult {

    private long start;


    public DeferredResultWithWatcher(long start,long timeout){
        super(timeout);
        this.start = start;
    }

    @Override
    public boolean setResult(Object result) {
        System.out.println("setResult cost:" + (System.currentTimeMillis() - start));
        return super.setResult(result);
    }

    @Override
    public void onTimeout(Runnable callback) {
        super.onTimeout(callback);
    }

    @Override
    public void onError(Consumer callback) {
        super.onError(callback);
    }
}
