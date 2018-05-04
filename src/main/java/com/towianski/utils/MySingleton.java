/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

/**
 *
 * @author stan
 */
public class MySingleton {
    
    private MySingleton() {}

    private static class LazyHolder {
        static final MySingleton INSTANCE = new MySingleton();
    }

    public static MySingleton getInstance() {
        return LazyHolder.INSTANCE;
    }
}
