package com.local.study.myImpl;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class DoubleLinkedList {

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

    public void add(Node node){
        if (head == null){
            head = node;
            count.incrementAndGet();
            return;
        }

        Node tmp = head;
        while (tmp.next != null){
            tmp = tmp.next;
        }
        tmp.next = node;
        node.prev = tmp;
        count.incrementAndGet();
    }

    public int get(int index){
        if (index < 0 || index > count.get()) throw new ArrayIndexOutOfBoundsException();
        Node node = head;
        for (int i = 0; i < index ; i++) {
            node = node.next;
        }
        return node.value;
    }

    @Test
    public void t(){
        DoubleLinkedList list = new DoubleLinkedList();
        list.add(new Node(1));
        list.add(new Node(4));
        list.add(new Node(42));
        list.add(new Node(41));
        System.out.println(list.get(1));//4
        System.out.println(list.get(2));//42
        System.out.println(list.get(0));//1
        System.out.println(list.get(3));//41


    }

}
