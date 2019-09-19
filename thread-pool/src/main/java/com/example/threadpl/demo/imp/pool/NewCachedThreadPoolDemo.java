package com.example.threadpl.demo.imp.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * 可缓存的线程池
 */
public class NewCachedThreadPoolDemo {

    public static void main(String[] args) {

        ExecutorService service = Executors.newCachedThreadPool();

        service.execute(new Runnable() {
            @Override
            public void run() {
                for ( int i = 0 ; i < 100 ; i++){

                    System.out.println("i=" + i);

                    if( i == 50 ){

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
