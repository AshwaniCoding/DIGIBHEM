/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Ashu
 */
public class AccountInfo {

    public static class Details {
        
        Transactions transactions = new Transactions();

        private String name;
        private String fatherName;
        private String motherName;
        private Date dob;
        private String gender;
        private String aadharNumber;
        private String email;
        private String phoneNumber;
        private String country;
        private String accountNumber;
        private Float balance;
        private String ifscCode;

        public Details(String username, String fatherName, String motherName, Date dob, String gender, String aadharNumber, String email, String phoneNumber, String country, String accountNumber, Float balance, String ifscCode) {
            this.name = username;
            this.fatherName = fatherName;
            this.motherName = motherName;
            this.dob = dob;
            this.gender = gender;
            this.aadharNumber = aadharNumber;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.country = country;
            this.accountNumber = accountNumber;
            this.balance = balance;
            this.ifscCode = ifscCode;
        }
        
        //Funtion to get firstName from Fullname
        public String getFirstName() {
            String[] nameParts = name.trim().split("\\s+");
            return nameParts[0];
        }
        
        //Function to get last 4 digits from accountnumber
        public String getAccountNumberLast4Digits() {
            return accountNumber.substring(accountNumber.length() - 4);
        }
        
        //Function to return the balance to the Home Page when click on show balance
        public Float getAccountBalance() {
            return balance;
        }
        
        //Function to set the correct format after getting date from database
        public String getDate() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM/y");
            String dobString = dateFormat.format(dob);
            return dobString;
        }
        
        //Function to set all the values from SQL to Account Page 
        public void setDetailsOnLabel() {
            HomePage.welcomeName.setText("Hi, " + getFirstName());
            HomePage.homeAccountNumberLabel.setText("XXXX " + getAccountNumberLast4Digits());
            HomePage.accountUsername.setText(name);
            HomePage.accountNumber.setText(accountNumber);
            HomePage.ifscLabel.setText(ifscCode);
            HomePage.personalNameLabel.setText(name);
            HomePage.personalDOB.setText(getDate());
            HomePage.fatherLabel.setText(fatherName);
            HomePage.motherLabel.setText(motherName);
            HomePage.personalCountryLabel.setText(country);
            HomePage.editContact.setText(phoneNumber);
            HomePage.editEmail.setText(email);
            HomePage.personalAadharLabel.setText(aadharNumber);
            HomePage.homeAccountBalance.setText("* * * *");
            
            transactions.getTwotransactionData();
        }

    }
    
    //Function to get all the values related to Account Page from SQL
    public Details getAccountInfoByUsername(String username) {

        Details info = null;
        String query = "SELECT u.full_name, u.father_name, u.mother_name, u.dob, u.gender, u.aadhar, u.email, u.mobile, u.country, "
                + "a.account_number, a.balance, a.IFSC_code "
                + "FROM Users u "
                + "JOIN Accounts a ON u.user_id = a.user_id "
                + "WHERE u.username = ?";

        try (Connection connection = MySQLConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String uname = rs.getString("full_name");
                String fatherName = rs.getString("father_name");
                String motherName = rs.getString("mother_name");
                Date dob = rs.getDate("dob");
                String gender = rs.getString("gender");
                String aadharNumber = rs.getString("aadhar");
                String email = rs.getString("email");
                String phoneNumber = rs.getString("mobile");
                String country = rs.getString("country");
                String accountNumber = rs.getString("account_number");
                Float balance = rs.getFloat("balance");
                String ifscCode = rs.getString("IFSC_code");

                info = new Details(uname, fatherName, motherName, dob, gender, aadharNumber, email, phoneNumber, country, accountNumber, balance, ifscCode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Not get the data");
        }
        return info;
    }

}
