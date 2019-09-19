package com.example.threadpl.demo.imp.po;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * 测试类
 */
public class Client {

    public static void main(String[] args) {

        //第一种  实现Thread 类
        MyThread01 myThread01 = new MyThread01();
        myThread01.start();

        //第二种   实现Runable接口
        Thread thread = new Thread(new MyThread02()) ;
        thread.start();

        //第三种实现callBack接口，通过线程池ExecutorService的submit方法执行
        MyThread3 myThread3 = new MyThread3();
        ExecutorService service = Executors.newFixedThreadPool(3);
        service.submit(myThread3);
    }




}
