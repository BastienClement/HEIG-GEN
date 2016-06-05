package ch.heigvd.gen.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.heigvd.gen.R;
import ch.heigvd.gen.models.Group;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.utilities.Utils;

public class GroupMemberListAdapter extends ArrayAdapter<User>{

    private final List<User> users;

    private Activity a;

    /**
     * TODO
     *
     * @param a
     * @param res
     * @param users
     */
    public GroupMemberListAdapter(final Activity a, final int res, final List<User> users) {
        super(a, res, users);
        this.users = users;
        this.a = a;
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
        if (isAdmin()) {
            ImageView remove = (ImageView)convertView.findViewById(R.id.member_remove);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("wtf");
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