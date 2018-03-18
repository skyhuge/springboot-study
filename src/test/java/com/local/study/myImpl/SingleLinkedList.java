package com.local.study.myImpl;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/*
    // 单向链表，可以在head前后添加元素，head始终指向链表中的第一个元素
    // 缺点是每次都必须从头开始往后遍历
    //
    // ----------------
    // | value | next  |
    // ----------------
 */
public class SingleLinkedList {

    private AtomicInteger count = new AtomicInteger();


    class Node{
        int value;
        Node next;
        Node(int value){
            this.value = value;
        }

    }
    private Node head; // just a pointer , do not store value


    public void addBefore(Node node){
        node.next = head;
        head = node;
        count.incrementAndGet();
    }

    public void addLast(Node node){
        if (head == null) {
            head = node;
            count.incrementAndGet();
            return;
        }
        Node tmp = head;
        while (tmp.next != null){
            tmp = tmp.next;
        }
        tmp.next = node;
        count.incrementAndGet();
    }

    public int get(int i){
        if (i > count.get()) throw new ArrayIndexOutOfBoundsException();
        Node tmp = head;
        for (int j = 1; j <= i; j++) {
            if (j == 1) continue;
            tmp = tmp.next;
        }
        return tmp.value;
    }

    public void getAll(){
        if (head == null) {
            System.out.println("empty list");
            return;
        }
        Node tmp = head;
        while (tmp.next != null){
            System.out.println(tmp.value);
            tmp = tmp.next;
        }
        System.out.println(tmp.value);

    }

    @Test
    public void a(){
        SingleLinkedList list = new SingleLinkedList();
        list.addLast(new Node(1));
        list.addLast(new Node(3));
        list.addLast(new Node(30));
        list.addBefore(new Node(2));
        list.addBefore(new Node(4));
        list.addBefore(new Node(40));
        list.addLast(new Node(50));
        list.getAll();
    }
}
