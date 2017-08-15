package com.Cecilia.vote.test;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Cecilia on 2017/8/3.
 */
public class test {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        test t = new test();
        try {
           boolean flag =  t.userRegister(sc);
            System.out.println(flag);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean userRegister(Scanner sc) throws IOException {

        //tempStr用于存储临时输入数据，防止用户输入的错误信息丢失而再次重新输入
        String username = "", usernick = "", password = "", password1 = "", tempStr;
        System.out.println("用户注册开始，请依次输入用户信息，不能有空格————");
        while (true) {
            System.out.println("请输入用户名（1-16位）："+username);

            System.out.println("请输入用户名（1-16位）：");
            tempStr = sc.nextLine().trim();
            if (tempStr.length() > 0) {
                username = tempStr;
            }
            System.out.println("请输入用户昵称（1-16位）："+usernick);
            tempStr = sc.nextLine().trim();
            if (tempStr.length() > 0) {
                usernick = tempStr;
            }
            System.out.println("请输入用户密码（1-16位）："+password);
            tempStr = sc.nextLine().trim();
            if (tempStr.length() > 0) {
                password = tempStr;
            }
            System.out.println("请再次输入确认密码（1-16位）："+password1);
            tempStr = sc.nextLine().trim();
            if (tempStr.length() > 0) {
                password1 = tempStr;
            }

        }
    }
}