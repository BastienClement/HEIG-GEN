package ch.heigvd.gen.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.heigvd.gen.R;
import ch.heigvd.gen.activities.ContactDiscussionActivity;
import ch.heigvd.gen.adapters.GroupListAdapter;
import ch.heigvd.gen.communications.RequestGET;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.ICustomCallback;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Group;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.utilities.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment implements IRequests, ICustomCallback{
    ListView listView;
    GroupListAdapter adapter;

    private final static String TAG = GroupFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group, container, false);
        listView = (ListView) v.findViewById(R.id.groups_list);

        return v;

    }

    @Override
    public void onStart() {
        super.onStart();

        adapter = new GroupListAdapter(getActivity(), R.layout.groups_list_item, Group.groups);

        loadGroups();

        // fill listview
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);


        // handle click on group
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // get contact
                final Group item = (Group) parent.getItemAtPosition(position);

                // start group chat activity

                /**
                 * TODO When GroupChatActivity is created

                Intent intent = new Intent(getActivity(), GroupChatActivity.class);
                Bundle b = new Bundle();
                b.putString("group_name", item.getGroupname());
                b.putInt("group_id", item.getId());
                intent.putExtras(b);
                startActivity(intent);
                 */
            }
        });
    }

    /**
     * TODO
     */
    private void loadGroups() {
        try {
            Log.i(TAG, "Token : " + Utils.getToken(getActivity()));
            new RequestGET(new ICallback<String>() {
                @Override
                public void success(String result) {
                    adapter.clear();
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonGroup = jsonArray.getJSONObject(i);
                            Group group = new Group(jsonGroup.getInt("id"), jsonGroup.getString("title"), jsonGroup.getString("last_massage"), jsonGroup.getBoolean("unread"));
                            Group.groups.add(group);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG, "Success : " + result);
                }

                @Override
                public void failure(Exception ex) {
                    try {
                        Utils.showAlert(getActivity(), new JSONObject(ex.getMessage()).getString("err"));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(getActivity()), BASE_URL + GET_GROUPS).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

    }

    /**
     * To update the group list
     */
    @Override
    public void update() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
