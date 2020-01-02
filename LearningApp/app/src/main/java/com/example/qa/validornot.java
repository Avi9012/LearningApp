package com.example.qa;
public class validornot {

    CharSequence target;

    public validornot(){

    }

    public validornot(CharSequence target)
    {
        this.target=target;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}