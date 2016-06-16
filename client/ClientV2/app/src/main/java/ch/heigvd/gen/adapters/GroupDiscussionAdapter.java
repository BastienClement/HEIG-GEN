package ch.heigvd.gen.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.heigvd.gen.R;
import ch.heigvd.gen.models.Message;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.utilities.Utils;

/**
 * Adapter used to display the messages in a group discussion
 */
public class GroupDiscussionAdapter extends ArrayAdapter<Message> {

    private final List<Message> messages;

    private Activity a;

    /**
     * Adapter's constructor
     *
     * @param a        the current activity
     * @param res      the ressource's id
     * @param messages the messages to display
     */
    public GroupDiscussionAdapter(final Activity a, final int res, final List<Message> messages) {
        super(a, res, messages);
        this.messages = messages;
        this.a = a;
    }

    /**
     * Get the number of messages in the discussion
     *
     * @return the message's count
     */
    @Override
    public int getCount() {
        return messages.size();
    }

    /**
     * Get a single message
     *
     * @param position the messages position
     * @return the actual Message
     */
    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    /**
     * Defines a specific layout to display sent and received messages differently, sent messages
     * are displayed on the right side of the screen while recieved messages are displayed on the
     * right
     *
     * @param position    the message's position
     * @param convertView the message's view
     * @param parent      the view's parent
     * @return the updated view
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message message = getItem(position);

        // if own message
        if (message.getFrom() == Utils.getId(a)) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_own_message_list_item, parent, false);
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_other_message_list_item, parent, false);
            TextView message_tv_user = (TextView) convertView.findViewById(R.id.group_item_message_user);
            if (message_tv_user != null)
                message_tv_user.setText(User.findById(message.getFrom()).getUsername() + " says : ");
        }

        TextView message_tv = (TextView) convertView.findViewById(R.id.group_item_message);
        if (message_tv != null) {
            message_tv.setText(message.getContent());
        }

        TextView message_tv_date = (TextView) convertView.findViewById(R.id.group_item_message_date);
        if (message_tv_date != null) {
            message_tv_date.setText(message.getDate().toString());
        }

        return convertView;
    }

}