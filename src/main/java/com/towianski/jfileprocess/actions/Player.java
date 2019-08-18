/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

/**
 *
 * @author stan
 */
public interface Player {

    public void go();   // cannot use start because runnable uses start to call run :-(

    public void stop();

    public void pause();

    public void restart();

}
