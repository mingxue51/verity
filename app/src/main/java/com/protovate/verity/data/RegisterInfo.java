package com.protovate.verity.data;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Yan on 5/26/2015.
 */

public class RegisterInfo implements Serializable {
    public String email;
    public String password;
    public String firstName;
    public String lastName;
    public File photoFile;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public void setPhotoFile(File photoFile) {
        this.photoFile = photoFile;
    }
}