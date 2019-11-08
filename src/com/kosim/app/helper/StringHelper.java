/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kosim.app.helper;


/**
 *
 * @author user65
 */
public class StringHelper {
    
    public static final String NOZERO = "123456789";
    public static final String NUMERIC = "0123456789";
    public static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" + NUMERIC;
    
    public static final String RandomString(String dictionary, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<n; i++) {
            char character = dictionary.charAt((int) (dictionary.length() * Math.random()));
            
            sb.append(character);
        }
        
        return sb.toString();
    }
}
