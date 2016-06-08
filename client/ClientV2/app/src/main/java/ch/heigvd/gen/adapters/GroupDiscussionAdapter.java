package ch.heigvd.gen.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ch.heigvd.gen.R;
import ch.heigvd.gen.models.Group;
import ch.heigvd.gen.models.Message;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.utilities.Utils;

public class GroupDiscussionAdapter extends ArrayAdapter<Message>{

    private final List<Message> messages;

    private Activity a;

    /**
     * TODO
     *
     * @param a
     * @param res
     * @param messages
     */
    public GroupDiscussionAdapter(final Activity a, final int res, final List<Message> messages) {
        super(a, res, messages);
        this.messages = messages;
        this.a = a;
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    public int getCount() {
        return messages.size();
    }

    /**
     * TODO
     *
     * @param position
     * @return
     */
    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message message = getItem(position);
        // TODO: check who send the message ?

        // if own message :
        if (message.getFrom() == Utils.getId(a)) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_own_message_list_item, parent, false);
        } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_other_message_list_item, parent, false);
                TextView message_tv_user = (TextView) convertView.findViewById(R.id.group_item_message_user);
                if (message_tv_user != null) message_tv_user.setText(User.findById(message.getFrom()).getUsername() + " says : ");
        }

        TextView message_tv = (TextView) convertView.findViewById(R.id.group_item_message);
        if (message_tv != null) {
            message_tv.setText(message.getContent());
        }

        TextView message_tv_date = (TextView) convertView.findViewById(R.id.group_item_message_date);
        if (message_tv_date != null) {
            message_tv_date.setText(message.getDate().toString());
        }

        //User.findById(message.getFrom()).getUsername();

        //LinearLayout line = (LinearLayout) convertView.findViewById(R.id.item_line);
        return convertView;
    }

}