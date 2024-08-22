/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banking;

import com.banking.AccountInfo.Details;
import static com.banking.HomePage.content;
import static com.banking.HomePage.setDateToLabel;
import static com.banking.HomePage.welcomeMainContent;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

/**
 *
 * @author Ashu
 */
public class LoginSignup {
    
    public static int currentAccountId = -1;

    //----------------------------------Signup Part----------------------
    //Function to validate the Signup first page of validation
    public boolean signupPage1Validation() {
        Border defaultBorder = HomePage.aadharText.getBorder();
        UserInfo();

        if (getFullName().equals("")) {
            HomePage.fullNameText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
        } else {
            HomePage.fullNameText.setBorder(defaultBorder);
        }

        if (getFatherName().equals("")) {
            HomePage.fatherNameText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
        } else {
            HomePage.fatherNameText.setBorder(defaultBorder);
        }

        if (getMotherName().equals("")) {
            HomePage.motherNameText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
        } else {
            HomePage.motherNameText.setBorder(defaultBorder);
        }

        if (getDob().equals(new Date(0, 0, 1))) {
            HomePage.dobText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
        } else {
            HomePage.dobText.setBorder(defaultBorder);
        }

        return !(getMotherName().equals("") || getFullName().equals("") || getFatherName().equals("") || getDob().equals(new Date(0, 0, 1)));
    }

    //Function to validate the Signup second page of validation
    public boolean signupPage2Validation() {
        Border defaultBorder = HomePage.fullNameText.getBorder();
        UserInfo();

        if (getAadhar().equals("")) {
            HomePage.aadharText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
        } else {
            HomePage.aadharText.setBorder(defaultBorder);
        }

        if (getMobile().equals("")) {
            HomePage.mobileText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
        } else {
            HomePage.mobileText.setBorder(defaultBorder);
        }

        if (getEmail().equals("") || !isValidGmail(getEmail())) {
            HomePage.emailText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
        } else {
            HomePage.emailText.setBorder(defaultBorder);
        }

        if (getUsername().equals("")) {
            HomePage.signupUsernameText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
        } else {
            HomePage.signupUsernameText.setBorder(defaultBorder);
        }

        if (getUsername().length() < 5) {
            HomePage.signupUsernameText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
            JOptionPane.showMessageDialog(null, "Username should be more than 5 characters");
        } else {
            HomePage.signupUsernameText.setBorder(defaultBorder);
        }

        if (!checkUsername()) {
            return false;
        }

        if (getPin().equals("")) {
            HomePage.pinText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
        } else {
            HomePage.pinText.setBorder(defaultBorder);
        }

        if (HomePage.cnfPinText.getPassword().equals("")) {
            HomePage.cnfPinText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
        } else {
            HomePage.cnfPinText.setBorder(defaultBorder);
        }

        return !(getUsername().equals("") || HomePage.cnfPinText.getPassword().equals("") || getPin().equals("") || getEmail().equals("") || getMobile().equals("") || getAadhar().equals("") || !isValidGmail(getEmail()));
    }

    private String fullName;
    private String fatherName;
    private String motherName;
    private String aadhar;
    private String mobile;
    private String email;
    private String username;
    private Date dob;
    private String gender;
    private String country;
    private String pin;

    public void UserInfo() {
        this.fullName = HomePage.fullNameText.getText();
        this.fatherName = HomePage.fatherNameText.getText();
        this.motherName = HomePage.motherNameText.getText();
        this.aadhar = HomePage.aadharText.getText();
        this.mobile = HomePage.mobileText.getText();
        this.email = HomePage.emailText.getText();
        this.username = HomePage.signupUsernameText.getText();
        this.dob = (Date) HomePage.dobText.getDate();
        this.gender = (String) HomePage.genderText.getSelectedItem();
        this.country = (String) HomePage.countryText.getSelectedItem();
        this.pin = new String(HomePage.pinText.getPassword());
    }

    // Getter methods
    public String getFullName() {
        return fullName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public String getAadhar() {
        return aadhar;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public Date getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getCountry() {
        return country;
    }

    public String getPin() {
        return pin;
    }

    //Function to insert values of signup page to sql
    public void saveUserInfo() {
        String query = "INSERT INTO users (full_name, father_name, mother_name, aadhar, mobile, email, username, dob, gender, country, pin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = MySQLConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, getFullName());
            preparedStatement.setString(2, getFatherName());
            preparedStatement.setString(3, getMotherName());
            preparedStatement.setString(4, getAadhar());
            preparedStatement.setString(5, getMobile());
            preparedStatement.setString(6, getEmail());
            preparedStatement.setString(7, getUsername());
            preparedStatement.setDate(8, convertUtilToSql(getDob()));
            preparedStatement.setString(9, getGender());
            preparedStatement.setString(10, getCountry());
            preparedStatement.setString(11, getPin());

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Registered Successfully");
                clearSignup();
                HomePage.initialization();
            } else {
                JOptionPane.showMessageDialog(null, "Not Registered");
            }
        } catch (SQLException e) {
        }
    }

    //Function to validate whether username already exists or not
    private boolean checkUsername() {
        ResultSet rs;
        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection connection = MySQLConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, getUsername());
            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Username already exists!");
                return false;
            }

        } catch (Exception e) {
        }

        return true;
    }

    //Function to convert util date to sql date as util date is not supported in sql
    public static java.sql.Date convertUtilToSql(java.util.Date utilDate) {
        if (utilDate != null) {
            return new java.sql.Date(utilDate.getTime());
        }
        return null;
    }

    //To check whether written email is correct
    public static boolean isValidGmail(String email) {
        return email.toLowerCase().endsWith("@gmail.com");
    }

    //Function to clear all the values after successful signup
    public void clearSignup() {
        HomePage.fullNameText.setText("");
        HomePage.fatherNameText.setText("");
        HomePage.motherNameText.setText("");
        HomePage.aadharText.setText("");
        HomePage.mobileText.setText("");
        HomePage.emailText.setText("");
        HomePage.signupUsernameText.setText("");
        HomePage.dobText.setDate(new Date(0, 0, 1));
        HomePage.genderText.setSelectedItem(0);
        HomePage.countryText.setSelectedItem(0);
        HomePage.pinText.setText("");
        HomePage.cnfPinText.setText("");
    }
    
    //Function to clear all the values from signin page
    public void clearSignin() {
        HomePage.loginUsernameText.setText("");
        HomePage.passwordText.setText("");
    }

    //----------------------------------Login Part----------------------
    private String loginUsername;
    private String loginPin;

    public void loginInfo() {
        this.loginUsername = HomePage.loginUsernameText.getText();
        this.loginPin = new String(HomePage.passwordText.getPassword());
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public String getLoginPassword() {
        return loginPin;
    }

    public boolean loginValidation() {
        loginInfo();
        Border defaultBorder = HomePage.fullNameText.getBorder();
        if (getLoginUsername().equals("")) {
            HomePage.loginUsernameText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
        } else {
            HomePage.loginUsernameText.setBorder(defaultBorder);
        }

        if (getLoginPassword().equals("")) {
            HomePage.passwordText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));
        } else {
            HomePage.passwordText.setBorder(defaultBorder);
        }

        return true;
    }

    
    public void loginUser() {
    ResultSet rs = null;
    String query = "SELECT * FROM users WHERE username = ? AND pin = ?";

    try (Connection connection = MySQLConnection.getConnection(); 
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

        preparedStatement.setString(1, getLoginUsername());
        preparedStatement.setString(2, getLoginPassword());

        rs = preparedStatement.executeQuery();

        if (rs.next()) {
            // Retrieve and store the user_id and account_id
            int userId = rs.getInt("user_id");
            String getAccountIdQuery = "SELECT account_id FROM Accounts WHERE user_id = ?";
            
            try (PreparedStatement accountStmt = connection.prepareStatement(getAccountIdQuery)) {
                accountStmt.setInt(1, userId);
                ResultSet accountRs = accountStmt.executeQuery();
                
                if (accountRs.next()) {
                    LoginSignup.currentAccountId = accountRs.getInt("account_id"); // Store account ID
                } else {
                    JOptionPane.showMessageDialog(null, "No account found for this user.");
                    return;
                }
            }

            JOptionPane.showMessageDialog(null, "Login Successful");
            content.setSelectedIndex(1);
            welcomeMainContent.setSelectedIndex(0);
            setDateToLabel();
        } else {
            JOptionPane.showMessageDialog(null, "Invalid username or PIN");
        }

    } catch (Exception e) {
        e.printStackTrace(); // Print the stack trace for debugging purposes
        JOptionPane.showMessageDialog(null, "An error occurred during login");
    } finally {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace(); // Print the stack trace for debugging purposes
            }
        }
    }
}
}
