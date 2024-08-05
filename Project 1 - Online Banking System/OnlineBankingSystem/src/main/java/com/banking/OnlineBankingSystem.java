/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.banking;


import static com.banking.MySQLConnection.createDatabaseAndTables;
import static com.banking.MySQLConnection.createTrigger;

/**
 *
 * @author Ashu
 */
public class OnlineBankingSystem {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new HomePage().setVisible(true);
        });
        createDatabaseAndTables();
        createTrigger();
    }
    
    
    

    
    
}
