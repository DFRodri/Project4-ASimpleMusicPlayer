package com.example.android.dasmusicplayer;

//Custom Class to create the custom Object Credits that hold two elements
//creditName and creditURL
//I'm not sure on this description but I believe that this is it?
public class Credits {

    private String newCreditName;
    private String newCreditURL;

    //method to create the custom Object
    public Credits(String creditName, String creditURL) {
        newCreditName = creditName;
        newCreditURL = creditURL;
    }

    //get methods to retrieve their values
    public String getNewCreditName() {
        return newCreditName;
    }

    public String getNewCreditURL() {
        return newCreditURL;
    }

}
