package com.local.study.myImpl;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class DoubleLinkedListWithTail {

    private AtomicInteger count = new AtomicInteger();

    class Node{
        Node prev;
        Node next;
        int value;
        Node(int value){
            this.value = value;
        }
    }
    private Node head;
    private Node tail;

    public void addLast(int i){
        Node node = new Node(i);
        if (head == null){
            head = tail = node;
        }else {
            //修改三个指针
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        count.incrementAndGet();
    }

    public void addBefore(int i){
        Node node = new Node(i);
        if (head == null) head = tail = node;
        head.prev = node;
        node.next = head;
        head = node;
    }

    public int get(int index){
        if (index < 0 || index > count.get()) throw new ArrayIndexOutOfBoundsException();
        Node node;
        if (index <= (count.get() >> 1)){
            node = head;
            for (int i = 0; i < index ; i++) {
                node = node.next;
            }
            System.out.println("left");
        }else {
            node = tail;
            for (int i = count.get() - 1; i > index ; i--) {
                node = node.prev;
            }
            System.out.println("right");
        }
        return node.value;
    }

    public boolean remove(int v){
        if (head == null) return false;
        for (Node node = head; node != null; node = node.next){
            if (node.value == v){
                Node next = node.next;
                Node prev = node.prev;
                //check if node is head or tail
                if (prev == null){
                    head = next;
                }else {
                    prev.next = next;
                    node.prev = null; // unlink node to prev
                }

                if (next == null){
                    tail = prev;
                }else {
                    next.prev = prev;
                    node.next = null; // unlink
                }
                System.out.println("removed");
                return true;
            }
        }
        return false;

    }

    public void getAll(){
        if (head == null){
            System.out.println("empty list");
            return;
        }
        for (Node node = head; node != null; node = node.next){
            System.out.println(node.value);
        }
    }

    @Test
    public void t(){
        DoubleLinkedListWithTail list = new DoubleLinkedListWithTail();

        list.addLast(1);
        list.addLast(4);
        list.addLast(5);
        list.addLast(7);
        list.addLast(9);
//        System.out.println(list.get(1));
//        System.out.println(list.get(4));
//        System.out.println(list.get(3));
        list.addBefore(12);
        list.addBefore(14);
        list.addBefore(17);
        list.getAll();


    }

}
