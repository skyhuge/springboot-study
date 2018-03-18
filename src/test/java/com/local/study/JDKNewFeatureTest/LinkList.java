package com.local.study.JDKNewFeatureTest;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

public class LinkList {

    private static class Node{
        Node next;
        Node prev;
        int value;

        Node(int value){
            this.value = value;
        }
    }

    Node head;
    Node tail;

    private void add(Node node){
        if (head ==null){
            head = tail = node;
        }else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }

    }

    private static int d(int i){
        int c = 1;
        while (c < i){
            c <<= 1;
            System.out.println(c);
        }
        return c;
    }

    private static void t(){
        HashedWheelTimer wheelTimer = new HashedWheelTimer(2, TimeUnit.SECONDS);
        wheelTimer.newTimeout(new Task(),40,TimeUnit.SECONDS);
    }

    static class Task implements TimerTask{

        @Override
        public void run(Timeout timeout) throws Exception {
            System.out.println(timeout.isExpired());
            System.nanoTime();
        }
    }

    private int i;



    public static void main(String[] args) {

//        d(6);
//        t();
        System.out.println(1<< 2);
        System.out.println(19 % 8);
        System.out.println(19 & 7);
        System.out.println((17 % 2) == (17 & 2));
    }
}
