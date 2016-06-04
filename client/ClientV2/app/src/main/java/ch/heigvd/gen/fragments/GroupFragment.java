package ch.heigvd.gen.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import ch.heigvd.gen.R;
import ch.heigvd.gen.activities.GroupCreateActivity;
import ch.heigvd.gen.activities.GroupDiscussionActivity;
import ch.heigvd.gen.adapters.GroupListAdapter;
import ch.heigvd.gen.communications.RequestGET;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.ICustomCallback;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Group;
import ch.heigvd.gen.models.Message;
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
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_group, container, false);
        listView = (ListView) v.findViewById(R.id.groups_list);

        loadGroups();

        return v;

    }

    @Override
    public void onStart() {
        super.onStart();

        adapter = new GroupListAdapter(getActivity(), R.layout.groups_list_item, Group.groups);


        // fill listview
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);


        // handle click on group
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // get contact
                final Group item = (Group) parent.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), GroupDiscussionActivity.class);
                Bundle b = new Bundle();
                b.putString("group_name", item.getGroupname());
                b.putInt("group_id", item.getId());
                intent.putExtras(b);
                startActivity(intent);

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
                            Group group = new Group(jsonGroup.getInt("id"), jsonGroup.getString("title"), jsonGroup.getBoolean("unread"));
                            Group.groups.add(group);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // TODO : Order by last message
                    loadMessages();
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
     * TODO
     *
     */
    private void loadMessages(){
        for(final Group group : Group.groups) {
            new RequestGET(new ICallback<String>() {
                @Override
                public void success(String result) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result);
                        for (int i = jsonArray.length() - 1; i >= 0; i--) {
                            JSONObject jsonMessage = jsonArray.getJSONObject(i);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            group.addMessage(new Message(jsonMessage.getInt("user"), jsonMessage.getString("text"), sdf.parse(jsonMessage.getString("date")), jsonMessage.getInt("id")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Group.sortGroups();
                    adapter.notifyDataSetChanged();
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
            }, Utils.getToken(getActivity()), BASE_URL + GET_GROUP + group.getId() + GET_MESSAGES).execute();
        }
    }

    /**
     * To update the group list
     */
    @Override
    public void update() {
        Group.sortGroups();
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_groups, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add) {
            // start contact create group activity
            Intent intent = new Intent(getActivity(), GroupCreateActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
