/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.boot.server;

/**
 *
 * @author stan
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
public class WebStaticPagesExample {

    public static void main (String[] args) {
        SpringApplication app =
                  new SpringApplication(WebStaticPagesExample.class);
        app.run(args);
    }
}