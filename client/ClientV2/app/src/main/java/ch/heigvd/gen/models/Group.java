package ch.heigvd.gen.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Model for a group
 */
public class Group implements Comparable<Group> {

    private final int id;
    private final String groupname;
    private List<Message> messages;
    private List<User> members;
    private boolean unread;

    public static List<Group> groups = new ArrayList<>();

    /**
     * Group Constructor
     *
     * @param id        the group's id
     * @param groupname the group's name
     * @param unread    true if there are unread messages
     */
    public Group(int id, String groupname, boolean unread) {
        this.id = id;
        this.groupname = groupname;
        this.unread = unread;
        messages = new ArrayList<>();
        members = new ArrayList<>();
    }

    /**
     * Check if a group already has that name
     *
     * @param name the name to check
     * @return true if a group exists with the same name
     */
    public static boolean exists(String name) {
        for (Group group : groups) {
            if (group.getGroupname().equals(name)) {
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
     * Get the list of messages
     *
     * @return the messages
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * Add a message to the group's list of messages
     *
     * @param message the message to add
     */
    public void addMessage(Message message) {
        messages.add(message);
    }


    public int getId() {
        return id;
    }


    public String getGroupname() {
        return groupname;
    }


    /**
     * Used to filter groups and search a group
     *
     * @param s
     * @return
     */
    public boolean contains(String s) {
        return s == null || s.isEmpty() || groupname.toLowerCase().startsWith(s.toLowerCase());
    }

    /**
     * Compare this group with another group
     *
     * @param g the group to compare
     * @return o if they are the same group
     */
    public int compareTo(Group g) {
        return groupname.compareTo(g.getGroupname());
    }


    public String toString() {
        return groupname;
    }

    /**
     * Delete a group
     *
     * @param group_id the id of the group to delete
     */
    public static void deleteById(int group_id) {
        for (Group group : groups) {
            if (group.getId() == group_id) {
                groups.remove(group);
                break;
            }
        }
    }

    /**
     * Find a group
     *
     * @param group_id the group's id
     * @return the group
     */
    public static Group findById(int group_id) {
        for (Group group : groups) {
            if (group.getId() == group_id) {
                return group;
            }
        }
        return null;
    }

    /**
     * Delete a member
     *
     * @param member_id the member's id
     */
    public void deleteMemberById(int member_id) {
        for (User user : members) {
            if (user.getId() == member_id) {
                members.remove(user);
                break;
            }
        }
    }


    /**
     * Sort each group alphabetically
     */
    public static void sortGroups() {
        Collections.sort(groups, new Comparator<Group>() {
            @Override
            public int compare(Group group1, Group group2) {
                if (group1.getMessages().size() == 0) {
                    return 1;
                } else if (group2.getMessages().size() == 0) {
                    return -1;
                }
                return group2.getMessages().get(group2.getMessages().size() - 1).getDate().compareTo(group1.getMessages().get(group1.getMessages().size() - 1).getDate());
            }
        });
    }

    /**
     * Check if the group contains a particular member
     *
     * @param id the member's id to look for
     * @return true if the member is in the group
     */
    public boolean hasMemberWithId(int id) {
        for (User member : members) {
            if (member.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Search for a member
     *
     * @param id the member's id to look for
     * @return the User
     */
    public User findMemberById(int id) {
        for (User user : members) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
}
