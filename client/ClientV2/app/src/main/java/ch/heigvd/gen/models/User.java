package ch.heigvd.gen.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * TODO
 */
public class User implements Comparable<User> {

    private final int id;
    private final String username;
    private final boolean admin;
    private List<Message> messages;
    private int unreadMessages;
    private boolean unread;

    public static List<User> users = new ArrayList<>();

    /**
     * TODO
     *
     * @param id
     * @param username
     * @param admin
     * @param unread
     */
    public User(int id, String username, boolean admin, boolean unread) {
        this.id = id;
        this.username = username;
        this.admin = admin;
        this.unread = unread;
        messages = new ArrayList<>();
    }

    /**
     * TODO
     *
     * @return
     */
    public boolean isUnread() {
        return unread;
    }

    /**
     * TODO
     *
     * @param unread
     */
    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    /**
     * TODO
     *
     * @return
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * TODO
     *
     * @param message
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /**
     * TODO
     *
     * @return
     */
    public int getUnreadMessages() {
        return unreadMessages;
    }

    /**
     * TODO
     *
     * @param unreadMessages
     */
    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    /**
     * TODO
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * TODO
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * TODO
     *
     * @return
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * TODO
     *
     * @param s
     * @return
     */
    public boolean contains(String s) {
        return s == null || s.isEmpty() || username.toLowerCase().contains(s.toLowerCase());
    }

    /**
     * TODO
     *
     * @param u
     * @return
     */
    public int compareTo(User u) {
        return username.compareTo(u.getUsername());
    }

    /**
     * TODO
     *
     * @return
     */
    public String toString(){
        return username;
    }

    /**
     * TODO
     *
     * @param user_id
     */
    public static void deleteById(int user_id) {
        for(User user : users){
            if(user.getId() == user_id){
                users.remove(user);
                break;
            }
        }
    }

    /**
     * TODO
     *
     * @param user_id
     * @return
     */
    public static User findById(int user_id) {
        for(User user : users){
            if(user.getId() == user_id){
                return user;
            }
        }
        return null;
    }

    public static void sortUsers(){
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                if(user1.getMessages().size() == 0 || user2.getMessages().size() == 0){
                    return 0;
                }
                return user1.getMessages().get(user1.getMessages().size() - 1).getDate().compareTo(user2.getMessages().get(user2.getMessages().size() - 1).getDate());
            }
        });
    }
}
