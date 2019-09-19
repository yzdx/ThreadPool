package com.example.threadpl.demo.imp.pool;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 创建一个定长线程池，支持定时及周期性任务执行。
 */
public class NewScheduledThreadPoolDemo {


    public static void main(String[] args) {


        //参数 ： 核心线程数 周期执行
        ExecutorService service = Executors.newScheduledThreadPool(3);
        service.execute(new Runnable() {
            @Override
            public void run() {
                for( int i = 0 ; i < 1000 ; i++){
                    System.out.println("i=" + i);
                    if(i == 500){
                        try {
                            Thread.sleep(2000);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


    }

}
