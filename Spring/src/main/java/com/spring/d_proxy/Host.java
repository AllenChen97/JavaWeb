package com.spring.d_proxy;

public class Host implements Rent{

    @Override
    public void rent() {
        System.out.println("房子出租");
    }
}
