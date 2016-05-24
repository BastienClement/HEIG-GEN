package ch.heigvd.gen.models;

import java.util.List;

public class User implements Comparable<User> {

    private final int id;
    private final String username;
    private List<User> contacts;

    public User(final int id, final String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean contains(String s) {
        return s == null || s.isEmpty() || username.toLowerCase().contains(s.toLowerCase());
    }

    public int compareTo(User u) {
        return username.compareTo(u.getUsername());
    }

    public String toString(){ return username; }
}
