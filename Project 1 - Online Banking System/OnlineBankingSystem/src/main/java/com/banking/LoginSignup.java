/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banking;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

/**
 *
 * @author Ashu
 */
public class LoginSignup {

    public static class UserInfo {
        private String fullName;
        private String fatherName;
        private String motherName;
        private String aadhar;
        private String mobile;
        private String email;
        private String signupUsername;
        private Date dob;
        private String gender;
        private String country;
        private String pin;
        private String cnfPin;

        public UserInfo(String fullName, String fatherName, String motherName, String aadhar, String mobile, String email, String signupUsername, java.sql.Date dob, String gender, String country, String pin, String cnfPin) {
            this.fullName = fullName;
            this.fatherName = fatherName;
            this.motherName = motherName;
            this.aadhar = aadhar;
            this.mobile = mobile;
            this.email = email;
            this.signupUsername = signupUsername;
            this.dob = dob;
            this.gender = gender;
            this.country = country;
            this.pin = pin;
            this.cnfPin = cnfPin;
        }

        // Getter methods
        public String getFullName() { return fullName; }
        public String getFatherName() { return fatherName; }
        public String getMotherName() { return motherName; }
        public String getAadhar() { return aadhar; }
        public String getMobile() { return mobile; }
        public String getEmail() { return email; }
        public String getSignupUsername() { return signupUsername; }
        public java.sql.Date getDob() { return dob; }
        public String getGender() { return gender; }
        public String getCountry() { return country; }
        public String getPin() { return pin; }
        public String getCnfPin() { return cnfPin; }
    }

    public static void saveUserInfo(UserInfo userInfo) {
        String query = "INSERT INTO users (full_name, father_name, mother_name, aadhar, mobile, email, signup_username, dob, gender, country, pin, cnf_pin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userInfo.getFullName());
            preparedStatement.setString(2, userInfo.getFatherName());
            preparedStatement.setString(3, userInfo.getMotherName());
            preparedStatement.setString(4, userInfo.getAadhar());
            preparedStatement.setString(5, userInfo.getMobile());
            preparedStatement.setString(6, userInfo.getEmail());
            preparedStatement.setString(7, userInfo.getSignupUsername());
            preparedStatement.setDate(8, userInfo.getDob());
            preparedStatement.setString(9, userInfo.getGender());
            preparedStatement.setString(10, userInfo.getCountry());
            preparedStatement.setString(11, userInfo.getPin());
            preparedStatement.setString(12, userInfo.getCnfPin());

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new user was inserted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
