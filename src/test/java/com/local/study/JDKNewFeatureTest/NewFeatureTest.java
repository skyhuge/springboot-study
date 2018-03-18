package com.local.study.JDKNewFeatureTest;

import com.local.study.utils.IOUtils;
import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

public class NewFeatureTest {

    @Test
    public void timeTest(){
        System.out.println(LocalDate.now().minusDays(7));
        System.out.println(LocalTime.now());
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")));
    }

    @Test
    public void foreachTest(){
        Map<Integer,String> map = new HashMap<>();

        for (int i = 0; i < 5; i++) {
            map.put(i, i+"0");
        }
        for (Integer k:map.keySet()) {
//            System.out.println(k);
        }
//        map.forEach((k, v)-> System.out.println(k + " : " + v));
        Stream.of(map).forEach((p)-> System.out.println(p));
//        Stream.of(map).filter((p)->)

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(String.valueOf(i));
        }
        System.out.println(list.stream().filter((p) -> p.contains("2"))
                .limit(2).count());
    }

    @Test
    public void optionalTest(){
        String a = "";
        Optional<String> str = Optional.ofNullable(a);
        Optional<String> hello = Optional.of("hello");
        String s = "/Users/doraemoner/IdeaProjects/study/src/main/java/com/local/study/utils";
        for(String name : IOUtils.getFiles(s)){
            System.out.println(name);
        }
    }



    class Obj{//对象头8bytes，压缩后的指针4bytes，不足8的倍数还要考虑padding
        public boolean b; //1
        public byte c;//1
        public char d;//2
        public short s;//2
        public int i;//4
        public long l;//8
        public double o;//8
        public float f;//4
        public String string;//4
        public Object object;//4
        //
        // 8+4+30+4+4 = 50 >> 64
    }

    class Simple{
        private String s = new String("e");
        private String t = new String("abc");
        private byte[] b;
    }

    @Test
    public void unsafeTest() throws Exception{
        String s = new String();
        byte[] b = new byte[0];
    }

    public static void main(String[] args) throws Exception{
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        Field[] fields = Obj.class.getDeclaredFields();
        TreeSet<Long> set = new TreeSet<>();
        for (Field field : fields){
            field.setAccessible(true);
            System.out.println(field.getName() + ":" + unsafe.objectFieldOffset(field));
            set.add(unsafe.objectFieldOffset(field));
        }
        System.out.println(set.last() - set.first());
    }




}
