package com.Cecilia.vote.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * 测试
 * Created by Cecilia on 2017/8/6.
 */
public class test1 {

    /**
     * 用户投票功能
     */
    //1.根据已结束和正在进行中来与用户进行交互,分别使用两个集合表示已结束和正在进行的；
    //2.如果是已结束的投票，如果是匿名投票，那么不能查看投票明细，反之，可以查看投票和投票明细；
    //3.如果是正在进行的投票，那么可以查看投票、添加选项、查看明细；

    public static void main(String[] args) {
        String username = "Captain";
        String str = "Captain\t教主大人\t123456\tmanager";
        System.out.println(username.length());
        System.out.println(str.substring(0, username.length()));

        System.out.println();

        String str1 = "000007\tCecilia\t2017-08-11 09:37:43\t今天晚上去哪里玩\t未结束\t不匿名\t用户不可添加";
        String str2 = "000003\tCecilia\t2017-08-09 21:43:26\t你最喜欢的生活是什么样子？\t未结束\t匿名\t用户可添加";
        String str3 = "000009\tcc\t2017-08-11 10:34:39\t今天晚上吃啥？\t未结束\t不匿名\t用户可添加";
        List<String> list = new ArrayList<>();
        String[] arr = str1.split("\t");
        System.out.println("000007字符串转成数字：" + Integer.valueOf(arr[0]));

//        list.add(str1);
//        list.add(str2);
//        list.add(str3);
//        //Collections.sort(list, comparator());
//        System.out.println("排序后的结果为：");
//        for (String s : list) {
//            System.out.println(s);

        writeObject();
        }

        /**
         * 排序构造器,根据VoteBean对象的投票编号进行排序
         * @return 排序的构造器对象
         */
        private static Comparator<String> comparator () {
            Comparator<String> comparator = new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {

                    String[] arr1 = o1.split("\t");
                    int number1 = Integer.valueOf(arr1[0]);
                    String[] arr2 = o2.split("\t");
                    int number2 = Integer.valueOf(arr2[0]);

                    return (number1 == number2) ? 0 : (number1 < number2) ? -1 : (1);
                }
            };
            return comparator;
        }

    public static void writeObject() {
        try {

            HashMap<String,String> map = new HashMap<String,String>();
            map.put("name", "foolfish");

            FileOutputStream outStream = new FileOutputStream("E:/1.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);

            objectOutputStream.writeObject(map);
            outStream.close();
            System.out.println("successful");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
