package ch.heigvd.gen.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Space;
import android.widget.TextView;

import java.util.List;

import ch.heigvd.gen.R;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Group;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.utilities.Utils;

/**
 * Adapter used by the GroupAddMemberActivity to display the user's list of contacts in order to add
 * them to the current group, it adds a checkbox next to each contact's name
 */
public class GroupAddMemberListAdapter extends ArrayAdapter<User> implements IRequests {


    private final static String TAG = GroupAddMemberListAdapter.class.getSimpleName();

    private final List<User> users;

    private final Group group;

    private Activity a;

    /**
     * Adapter's constructor
     *
     * @param a     the current activity
     * @param res   the ressource's id
     * @param users the contacts to display
     */
    public GroupAddMemberListAdapter(final Activity a, final int res, final Group group, final List<User> users) {
        super(a, res, users);
        this.users = users;
        this.a = a;
        this.group = group;
    }

    /**
     * Get the number of contacts in the list
     *
     * @return the contact's count
     */
    @Override
    public int getCount() {
        return users.size();
    }

    /**
     * Get a single contact
     *
     * @param position the contact's position
     * @return the contact
     */
    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    /**
     * Renders the view of each contact in the list
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);

        if (group.findMemberById(user.getId()) == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.add_members_list_item, parent, false);
            TextView contactName = (TextView) convertView.findViewById(R.id.user_name);
            contactName.setText(user.getUsername());
        } else {
            convertView = new Space(getContext());
        }

        return convertView;
    }

    /**
     * Determines if the user is the administrator of the current group
     *
     * @return true if the user is the administrator
     */
    private boolean isAdmin() {
        int id = Utils.getId(a);
        for (User user : users) {
            if (user.getId() == id) {
                return user.isAdmin();
            }
        }
        return false;
    }
}