package ch.heigvd.gen.models;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
public class Group implements Comparable<Group> {

    private final int id;
    private final String groupname;
   // private final boolean admin;
    private List<Message> messages;
    private List<User> members;
    private boolean unread;

    public static List<Group> groups = new ArrayList<>();

    /**
     * TODO
     *
     * @param id
     * @param groupname
     * @param unread
     */
    public Group(int id, String groupname, boolean unread) {
        this.id = id;
        this.groupname = groupname;
        this.unread = unread;
        messages = new ArrayList<>();
        members = new ArrayList<>();
    }

    /**
     * TODO
     *
     * @param name
     * @return
     */
    public static boolean exists(String name){
        for(Group group : groups){
            if(group.getGroupname().equals(name)){
                return true;
            }
        }
        return false;
    }


    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public boolean isUnread() {
        return unread;
    }

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

    public int getUnreadMessages() {
        return unreadMessages;
    }
     */
    /**
     * TODO
     *
     * @param unreadMessages

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }*/

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
    public String getGroupname() {
        return groupname;
    }

    /**
     * TODO
     *
     * @return

    public boolean isAdmin() {
        return admin;
    }*/

    /**
     * TODO
     *
     * @param s
     * @return
     */
    public boolean contains(String s) {
        return s == null || s.isEmpty() || groupname.toLowerCase().contains(s.toLowerCase());
    }

    /**
     * TODO
     *
     * @param g
     * @return
     */
    public int compareTo(Group g) {
        return groupname.compareTo(g.getGroupname());
    }

    /**
     * TODO
     *
     * @return
     */
    public String toString(){
        return groupname;
    }

    /**
     * TODO
     *
     * @param group_id
     */
    public static void deleteById(int group_id) {
        for(Group group : groups){
            if(group.getId() == group_id){
                groups.remove(group);
                break;
            }
        }
    }

    /**
     * TODO
     *
     * @param group_id
     * @return
     */
    public static Group findById(int group_id) {
        for(Group group : groups){
            if(group.getId() == group_id){
                return group;
            }
        }
        return null;
    }
}
