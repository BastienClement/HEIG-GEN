package ch.heigvd.gen.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Space;
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

public class GroupAddMemberListAdapter extends ArrayAdapter<User> implements IRequests{


    private final static String TAG = GroupAddMemberListAdapter.class.getSimpleName();

    private final List<User> users;

    private final Group group;

    private Activity a;

    /**
     * TODO
     *
     * @param a
     * @param res
     * @param users
     */
    public GroupAddMemberListAdapter(final Activity a, final int res, final Group group, final List<User> users) {
        super(a, res, users);
        this.users = users;
        this.a = a;
        this.group = group;
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    public int getCount() {
        return users.size();
    }

    /**
     * TODO
     *
     * @param position
     * @return
     */
    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);
        // TODO: check who send the message ?



        if(group.findMemberById(user.getId()) == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.add_members_list_item, parent, false);
            TextView contactName = (TextView) convertView.findViewById(R.id.user_name);
            contactName.setText(user.getUsername());
        }
        else{
            convertView = new Space(getContext());
        }

        return convertView;
    }

    private boolean isAdmin(){
        int id = Utils.getId(a);
        for(User user : users){
            if(user.getId() == id){
                return user.isAdmin();
            }
        }
        return false;
    }
}