package ch.heigvd.gen.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.heigvd.gen.R;
import ch.heigvd.gen.models.User;

/**
 * Adapter used by the ContactFragment to display the user's list of contacts
 */
public class ContactListAdapter extends ArrayAdapter<User> implements Filterable {

    private final List<User> users;
    private List<User> filteredUsers;

    private Activity a;

    /**
     * Adapter's constructor
     *
     * @param a     the current activity
     * @param res   the ressource's id
     * @param users the contacts to display
     */
    public ContactListAdapter(final Activity a, final int res, final List<User> users) {
        super(a, res, users);
        this.users = users;
        this.filteredUsers = users;
        this.a = a;
    }

    /**
     * Get the number of contacts in the list
     *
     * @return the contact's count
     */
    @Override
    public int getCount() {
        return filteredUsers.size();
    }

    /**
     * Get a single contact
     *
     * @param position the contact's position
     * @return the contact
     */
    @Override
    public User getItem(int position) {
        return filteredUsers.get(position);
    }

    /**
     * Renders the view of each contact in the list, adds a textual notification if there are unread
     * messages in the discussion with this contact
     *
     * @param position    the contact's position
     * @param convertView the contact's view
     * @param parent      the view's parent
     * @return the updated view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.contacts_list_item, parent, false);

        TextView contactName = (TextView) convertView.findViewById(R.id.contact_name);
        contactName.setText(user.getUsername());
        if (user.isUnread()) {
            TextView textView = (TextView) convertView.findViewById(R.id.unread_flag_contact_list);
            textView.setText("New messages");
        } else {
            TextView textView = (TextView) convertView.findViewById(R.id.unread_flag_contact_list);
            textView.setText("");
        }

        return convertView;
    }

    /**
     * Filters and adds every contacts inside the adapter
     *
     * @return the contacts resulting from the filtering operation
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<User> filteredUsers;
                if (constraint != null && constraint.length() != 0) {
                    filteredUsers = new ArrayList<>();
                    for (User user : users) {
                        if (user.contains(constraint.toString())) {
                            filteredUsers.add(user);
                        }
                    }
                } else {
                    filteredUsers = users;
                }
                results.count = filteredUsers.size();
                results.values = filteredUsers;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ContactListAdapter.this.filteredUsers = (List<User>) results.values;
                if (results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    notifyDataSetChanged();
                }
            }
        };
    }
}