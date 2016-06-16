package ch.heigvd.gen.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ch.heigvd.gen.R;
import ch.heigvd.gen.communications.RequestDELETE;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Group;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.utilities.Utils;

/**
 * Adapter used by the GroupEditActivity to display the member's list of the group
 */
public class GroupMemberListAdapter extends ArrayAdapter<User> implements IRequests {


    private final static String TAG = GroupMemberListAdapter.class.getSimpleName();

    private final List<User> users;

    private final Group group;

    private Activity a;

    /**
     * Adapter's constructor
     *
     * @param a     the current activity
     * @param res   the ressource's id
     * @param users the group's members to display
     */
    public GroupMemberListAdapter(final Activity a, final int res, final Group group, final List<User> users) {
        super(a, res, users);
        this.users = users;
        this.a = a;
        this.group = group;
    }

    /**
     * Get the number of members in the group
     *
     * @return the message's count
     */
    @Override
    public int getCount() {
        return users.size();
    }

    /**
     * Get a single member
     *
     * @param position the member's position
     * @return the actual User
     */
    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    /**
     * Renders the view of each member of the group
     *
     * @param position    the groups's position
     * @param convertView the group's view
     * @param parent      the view's parent
     * @return the updated View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.members_list_item, parent, false);

        TextView memberName = (TextView) convertView.findViewById(R.id.member_name);
        memberName.setText(user.getUsername());
        if (isAdmin() && user.getId() != Utils.getId(a)) {
            ImageView remove = (ImageView) convertView.findViewById(R.id.member_remove);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        new RequestDELETE(new ICallback<String>() {
                            @Override
                            public void success(String result) {
                                Log.i(TAG, "Success : " + result);
                            }

                            @Override
                            public void failure(Exception ex) {
                                try {
                                    Utils.showAlert(a, new JSONObject(ex.getMessage()).getString("err"));
                                } catch (JSONException e) {
                                    Utils.showAlert(a, ex.getMessage());
                                    Log.e(TAG, e.getMessage());
                                }
                                Log.e(TAG, ex.getMessage());
                            }
                        }, Utils.getToken(a), BASE_URL + GET_GROUP + group.getId() + GET_MEMBER + user.getId()).execute();
                    } catch (Exception ex) {
                        Log.e(TAG, ex.getMessage());
                    }
                }
            });
        } else {
            ImageView remove = (ImageView) convertView.findViewById(R.id.member_remove);
            remove.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    /**
     * Determines if the users is the administrator of the group
     *
     * @return true if he is the administrator of the group
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