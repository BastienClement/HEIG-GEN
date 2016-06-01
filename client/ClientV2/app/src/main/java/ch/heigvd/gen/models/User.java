package ch.heigvd.gen.models;

import java.util.ArrayList;
import java.util.List;

public class User implements Comparable<User> {

    private final int id;
    private final String username;
    private final boolean admin;
    private List<Message> messages;
    private int unreadMessages;

    public static List<User> users = new ArrayList<>();

    public User(final int id, final String username, final boolean admin) {
        this.id = id;
        this.username = username;
        this.admin = admin;
        messages = new ArrayList<>();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean contains(String s) {
        return s == null || s.isEmpty() || username.toLowerCase().contains(s.toLowerCase());
    }

    public int compareTo(User u) {
        return username.compareTo(u.getUsername());
    }

    public String toString(){
        return username;
    }

    public static void deleteById(int user_id) {
        for(User user : users){
            if(user.getId() == user_id){
                users.remove(user);
                break;
            }
        }
    }

    public static User findById(int user_id) {
        for(User user : users){
            if(user.getId() == user_id){
                return user;
            }
        }
        return null;
    }
}
