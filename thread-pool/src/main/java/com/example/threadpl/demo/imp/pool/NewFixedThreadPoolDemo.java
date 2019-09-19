package com.example.threadpl.demo.imp.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 *  线程池
 *  以及带返回值的线程池FutureTask And Future创建
 *
 */
public class NewFixedThreadPoolDemo {
    public static void main(String[] args){
        //创建固定大小的线程池。
        ExecutorService fixedThreadPool  = Executors.newFixedThreadPool(5);

        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                for ( int i = 0 ; i < 50 ; i++){
                    System.out.println("i=" + i);
                    if(i == 30){
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }




}
