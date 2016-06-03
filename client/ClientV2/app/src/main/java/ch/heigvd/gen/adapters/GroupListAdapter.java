package ch.heigvd.gen.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.heigvd.gen.R;
import ch.heigvd.gen.models.Group;
import ch.heigvd.gen.models.User;

/**
 * Created by guillaume on 03.06.16.
 */
public class GroupListAdapter  extends ArrayAdapter<Group> {

    private final List<Group> groups;

    private Activity a;

    /**
     * TODO
     *
     * @param a
     * @param res
     * @param groups
     */
    public GroupListAdapter(final Activity a, final int res, final List<Group> groups) {

        super(a, res, groups);
        this.groups = groups;
        this.a = a;
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    public int getCount() {
        return groups.size();
    }

    /**
     * TODO
     *
     * @param position
     * @return
     */
    @Override
    public Group getItem(int position) {
        return groups.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Group group = getItem(position);
        // TODO: check who send the message ?

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.groups_list_item, parent, false);

        TextView groupName = (TextView)convertView.findViewById(R.id.contact_name);
        groupName.setText(group.getGroupname());
        if (group.isUnread()) {
            TextView textView = (TextView)convertView.findViewById(R.id.unread_flag);
            textView.setText("New messages");
        } else {
            TextView textView = (TextView)convertView.findViewById(R.id.unread_flag);
            textView.setText("");
        }

        return convertView;
    }
}