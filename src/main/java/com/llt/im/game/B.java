package com.llt.im.game;

import java.util.Random;

public class B {
    public static void main(String[] args) throws InterruptedException {
        while (true){
            Thread.sleep(100);
            System.out.println(new Random().nextInt(5));
        }

    }
}
