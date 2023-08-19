package ru.ovs.jsqlmonitor;

class LoginParam {
    private String user;
    private char[] password;
    
    String getStrPassword() {
        return password==null ? "" : String.valueOf(password);
    }
    
    String getUser() {
        return user;
    }
    
    void setUser(String aUser) {
        user=aUser;
    }
    
    void setPassword(char[] aPassword) {
        Utils.wipeCharArray(password);
        password=aPassword;
    }
    
    LoginParam(String user,char[] password) {
        this.user=user;
        if (password!=null) this.password=password.clone();
    }
}
