package com.local.study.utils;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * see FileAlterationObserver for more about FileFilter
 * see JDK WatcherService
 */
public class FileMonitorUtils {


    public static void main(String[] args) throws Exception {
        FileMonitor fileMonitor = new FileMonitorUtils().new FileMonitor();
        String path = "/Users/doraemoner/IdeaProjects/hystrix-test/target/classes/config";
        fileMonitor.monitor(path, new FileAlterationMonitor(5000),
                new FileMonitorUtils().new FileListener());
    }


    class FileListener implements FileAlterationListener {

        @Override
        public void onStart(FileAlterationObserver fileAlterationObserver) {
            System.out.println("observer start :" + System.currentTimeMillis());
        }

        @Override
        public void onDirectoryCreate(File file) {

        }

        @Override
        public void onDirectoryChange(File file) {

        }

        @Override
        public void onDirectoryDelete(File file) {

        }

        @Override
        public void onFileCreate(File file) {
            System.out.println("file created " + file.getName());
        }

        @Override
        public void onFileChange(File file) {

        }

        @Override
        public void onFileDelete(File file) {

        }

        @Override
        public void onStop(FileAlterationObserver fileAlterationObserver) {

        }
    }

    class FileMonitor {

        public void monitor(String path, FileAlterationMonitor monitor, FileAlterationListener listener) throws Exception {
            FileAlterationObserver observer = new FileAlterationObserver(path);
            observer.addListener(listener);
            monitor.addObserver(observer);
            monitor.start();
        }

    }
}

