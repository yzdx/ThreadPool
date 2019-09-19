package com.example.threadpl.demo.imp.po;

import java.util.concurrent.Callable;

public class MyThread3 implements Callable {
    @Override
    public Object call() throws Exception {
        for ( int i = 0 ; i < 30 ; i++ ){
            String ranway = "MyThread3 ------->call()" ;
            System.out.println("i====="+ i + ranway);
        }

        return true;
    }
}
