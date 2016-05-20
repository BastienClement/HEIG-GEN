package com.heigvd.gen.chat.Network.Query;

/**
 * Created by Antoine on 14.05.2016.
 */
public class Register extends Query {
    String usr;
    String pass;

    public Register(String usr, String pass){
        super("register");
        this.usr = usr;
        this.pass = pass;
    }

    public String getUsr() {
        return usr;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
