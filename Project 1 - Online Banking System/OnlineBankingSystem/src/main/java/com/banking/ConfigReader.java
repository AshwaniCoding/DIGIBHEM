/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banking;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ashu
 */
public class ConfigReader {
    
    static Properties properties;
    
    public static void initConfig(){
        try {
            properties = new Properties();
            properties.load(new FileInputStream("src\\main\\java\\com\\resources\\config\\config.properties"));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("File not found");
        }
    }
    
    public static String getConfigValue(String key){
        return properties.getProperty(key);
    }
    
    public static void setConfigValue(String key, String value){
        properties.setProperty(key, value);
    }
    
    
}
