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
import ch.heigvd.gen.models.Group;

/**
 * Adapter used by the GroupFragment to display the user's list of groups
 */
public class GroupListAdapter extends ArrayAdapter<Group> implements Filterable {

    private final List<Group> groups;
    private List<Group> filteredGroups;


    private Activity a;

    /**
     * Adapter's constructor
     *
     * @param a      the current activity
     * @param res    the ressource's id
     * @param groups the groups to display
     */
    public GroupListAdapter(final Activity a, final int res, final List<Group> groups) {

        super(a, res, groups);
        this.groups = groups;
        this.filteredGroups = groups;
        this.a = a;
    }

    /**
     * Get the number of groups in the list
     *
     * @return the group's count
     */
    @Override
    public int getCount() {
        return filteredGroups.size();
    }

    /**
     * Get a single group
     *
     * @param position the group's position
     * @return the group
     */
    @Override
    public Group getItem(int position) {
        return filteredGroups.get(position);
    }

    /**
     * Adds a textual notification if there are unread messages in the group discussion
     *
     * @param position    the group's position
     * @param convertView the group's view
     * @param parent      the view's parent
     * @return the updated view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Group group = getItem(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.groups_list_item, parent, false);

        TextView groupName = (TextView) convertView.findViewById(R.id.group_name);
        groupName.setText(group.getGroupname());
        if (group.isUnread()) {
            TextView textView = (TextView) convertView.findViewById(R.id.unread_flag_group_list);
            textView.setText("New messages");
        } else {
            TextView textView = (TextView) convertView.findViewById(R.id.unread_flag_group_list);
            textView.setText("");
        }

        return convertView;
    }

    /**
     * Filters and adds every groups inside the adapter
     *
     * @return the groups resulting from the filtering operation
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Group> filteredGroups;
                if (constraint != null && constraint.length() != 0) {
                    filteredGroups = new ArrayList<>();
                    for (Group group : groups) {
                        if (group.contains(constraint.toString())) {
                            filteredGroups.add(group);
                        }
                    }
                } else {
                    filteredGroups = groups;
                }
                results.count = filteredGroups.size();
                results.values = filteredGroups;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                GroupListAdapter.this.filteredGroups = (List<Group>) results.values;
                if (results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    notifyDataSetChanged();
                }
            }
        };
    }
}