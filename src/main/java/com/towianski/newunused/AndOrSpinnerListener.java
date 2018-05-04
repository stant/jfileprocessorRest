/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.newunused;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author stan
 */
public class AndOrSpinnerListener implements ChangeListener {
    protected Calendar calendar;
    protected JSpinner mySpinner;

    public AndOrSpinnerListener( JSpinner mySpinner )
    {
        this.mySpinner = mySpinner;
    }
    
    /**
     */
    public void stateChanged(ChangeEvent e) {
        SpinnerModel myModel = mySpinner.getModel();
        if ( myModel instanceof SpinnerDateModel) {
            
        }
    }    
}
