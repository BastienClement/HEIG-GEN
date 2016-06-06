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

public class GroupMemberListAdapter extends ArrayAdapter<User> implements IRequests{


    private final static String TAG = GroupMemberListAdapter.class.getSimpleName();

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
    public GroupMemberListAdapter(final Activity a, final int res, final Group group, final List<User> users) {
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

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.members_list_item, parent, false);

        TextView memberName = (TextView)convertView.findViewById(R.id.member_name);
        memberName.setText(user.getUsername());
        if (isAdmin() && user.getId() != Utils.getId(a)) {
            ImageView remove = (ImageView)convertView.findViewById(R.id.member_remove);
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
            ImageView remove = (ImageView)convertView.findViewById(R.id.member_remove);
            remove.setVisibility(View.INVISIBLE);
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