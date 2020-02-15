package com.llt.im.game;

public class A {
    {
        new Thread(()->{
            while (true){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("===>");
            }

        }).start();
    }

}
