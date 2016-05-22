package ch.heigvd.gen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.heigvd.gen.R;
import ch.heigvd.gen.models.User;

public class ContactListViewAdapter extends ArrayAdapter<User> implements Filterable {

    private final List<User> allUser;
    private List<User> filteredUser;

    public ContactListViewAdapter(final Context c, final int res, final List<User> users) {
        super(c, res, users);
        allUser = users;
        filteredUser = users;
    }

    @Override
    public int getCount() {
        return filteredUser.size();
    }

    @Override
    public User getItem(int position) {
        return filteredUser.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item, parent, false);
        }

        final User user = getItem(position);
        TextView username = (TextView) convertView.findViewById(R.id.item_username);
        if (username != null) {
            username.setText(user.getUsername());
        }

        LinearLayout line = (LinearLayout) convertView.findViewById(R.id.item_line);
        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement on click behavior !
            }
        });
        return convertView;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<User> filteredUsers;
                if (constraint != null && constraint.length() != 0) {
                    filteredUsers = new ArrayList<>();
                    for (User user : allUser) {
                        if (user.contains(constraint.toString())) {
                            filteredUsers.add(user);
                        }
                    }
                } else {
                    filteredUsers = allUser;
                }
                results.count = filteredUsers.size();
                results.values = filteredUsers;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ContactListViewAdapter.this.filteredUser = (List<User>) results.values;
                if (results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    notifyDataSetChanged();
                }
            }
        };
    }
}
