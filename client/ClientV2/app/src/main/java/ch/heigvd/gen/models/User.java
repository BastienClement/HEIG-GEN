package ch.heigvd.gen.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Model reprensenting an user
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
     * User constructor
     *
     * @param id the user's id
     * @param username the user's username
     * @param admin true if the user is an administrator
     * @param unread true if there are unread messages in the user's discussion
     */
    public User(int id, String username, boolean admin, boolean unread) {
        this.id = id;
        this.username = username;
        this.admin = admin;
        this.unread = unread;
        messages = new ArrayList<>();
    }

    /**
     * Find if there are unread messages
     *
     * @return true if there are unread messages
     */
    public boolean isUnread() {
        return unread;
    }

    /**
     * Set if there are unread messages
     *
     * @param unread rue if there are unread messages
     */
    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    /**
     * Get the list of messages
     *
     * @return the messages
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * Adds a message to the user's message list
     *
     * @param message the message to add
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /**
     * Get the unread messages
     *
     * @return the messages
     */
    public int getUnreadMessages() {
        return unreadMessages;
    }

    /**
     *  Set the unread messages
     *
     * @param unreadMessages the messages
     */
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

    /**
     * Used to filter and search through usernames
     *
     * @param s a string to search
     * @return true if the username contains the String
     */
    public boolean contains(String s) {
        return s == null || s.isEmpty() || username.toLowerCase().startsWith(s.toLowerCase());
    }

    /**
     * Compare user names
     *
     * @param u the user to compare usernames with
     * @return 0 if they are the same
     */
    public int compareTo(User u) {
        return username.compareTo(u.getUsername());
    }


    public String toString(){
        return username;
    }

    /**
     * Delete an User
     *
     * @param user_id the user's id
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
     * Find an User
     *
     * @param user_id the user's id
     * @return the User found
     */
    public static User findById(int user_id) {
        for(User user : users){
            if(user.getId() == user_id){
                return user;
            }
        }
        return null;
    }

    /**
     * Sort users
     */
    public static void sortUsers(){
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                if(user1.getMessages().size() == 0){
                    return 1;
                } else if(user2.getMessages().size() == 0){
                    return -1;
                }
                return user2.getMessages().get(user2.getMessages().size() - 1).getDate().compareTo(user1.getMessages().get(user1.getMessages().size() - 1).getDate());
            }
        });
    }
}
