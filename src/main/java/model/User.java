package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String username;
    private String password;
    private Wallet wallet;
    private List<String> notifications;
    private String curNotification;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.wallet = new Wallet();
        this.notifications = new ArrayList<>();
        this.curNotification = null;
    }

    public String getUsername() {
        return username;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void addNotification(String notification) {
        curNotification = notification;
    }

    public List<String> getNotifications() {
        return notifications;
    }

    public String getLastNotification() {
        if (curNotification != null) {
            notifications.add(curNotification);
        }
        String tmp = curNotification;
        curNotification = null;
        return tmp;
    }
}
