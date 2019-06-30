/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author stan
 */
public class SortedProperties extends Properties {

    private static final long serialVersionUID = 1L;

    @Override
    public Set<Object> keySet() {
        return Collections.unmodifiableSet(new TreeSet<Object>(super.keySet()));
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {

        Set<Map.Entry<Object, Object>> set1 = super.entrySet();
        Set<Map.Entry<Object, Object>> set2 = new LinkedHashSet<Map.Entry<Object, Object>>(set1.size());

        Iterator<Map.Entry<Object, Object>> iterator = set1.stream().sorted(new Comparator<Map.Entry<Object, Object>>() {

            @Override
            public int compare(java.util.Map.Entry<Object, Object> o1, java.util.Map.Entry<Object, Object> o2) {
                return o1.getKey().toString().compareTo(o2.getKey().toString());
            }
        }).iterator();

        while (iterator.hasNext())
            set2.add(iterator.next());

        return set2;
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
        }

};
