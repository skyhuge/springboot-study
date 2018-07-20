package com.local.study.controller;

import com.local.study.annotation.Watcher;
import com.local.study.async.DeferredResultWithWatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class AsynController {

    private ExecutorService executor = Executors.newFixedThreadPool(5);

    private static Random random = new Random();

    @GetMapping("sync")
    @Watcher(api = "sync")
    public String s(){

        return "sync";
    }

    @GetMapping("async")
    @Watcher(api = "async")
    public DeferredResult c(){
        DeferredResult<String> deferredResult = new DeferredResult<>(100 * 1000L);
        deferredResult.onTimeout(new Runnable() {
            @Override
            public void run() {
                deferredResult.setResult("timeout");
            }
        });
        executor.submit(new Runnable() {
            @Override
            public void run() {
                int i = random.nextInt(100) * 100;
                System.out.println("snooze : "+i);
                try {
                    Thread.sleep(i);
                    deferredResult.setResult("done");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        return deferredResult;
    }

    @GetMapping("w")
    @Watcher(api = "w")
    public DeferredResultWithWatcher w(){
        DeferredResultWithWatcher<String> result =
                new DeferredResultWithWatcher<>(System.currentTimeMillis(), 6 * 1000L);
        result.onTimeout(new Runnable() {
            @Override
            public void run() {
                result.setResult("timeout");
            }
        });

        executor.submit(new Runnable() {
            @Override
            public void run() {
                int i = random.nextInt(100) * 100;
                System.out.println("snooze: "+i);
                if (i > 5000) throw new RuntimeException("ex");
                try {
                    Thread.sleep(i);
                    result.setResult("done");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        return result;
    }
}
