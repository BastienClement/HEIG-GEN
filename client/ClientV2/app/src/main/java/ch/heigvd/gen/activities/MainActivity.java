package ch.heigvd.gen.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ch.heigvd.gen.R;
import ch.heigvd.gen.adapters.ViewPagerAdapter;
import ch.heigvd.gen.communications.RequestGET;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.ICustomCallback;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.services.EventService;
import ch.heigvd.gen.utilities.Utils;


public class MainActivity extends AppCompatActivity implements IRequests, ICustomCallback{

    private final static String TAG = MainActivity.class.getSimpleName();

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.fragment_contact)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.fragment_group)));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        setOwnId();

        EventService.getInstance().start(this);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((ICustomCallback) ((ICustomCallback) pagerAdapter.getItem(tab.getPosition()))).update();
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //Do nothing here
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //Do nothing here
            }
        });

    }

    private void setOwnId() {
        try {
            RequestGET get = new RequestGET(new ICallback<String>() {
                @Override
                public void success(String result) {
                    try{
                        JSONObject json = new JSONObject(result);
                        Utils.setId(MainActivity.this, json.getInt("id"));
                        Log.i(TAG, "Id : " + json.getString("id"));
                    } catch (Exception ex){
                        Log.e(TAG, ex.getMessage());
                    }
                    Log.i(TAG, "Success : " + result);
                }

                @Override
                public void failure(Exception ex) {
                    try {
                        Utils.showAlert(MainActivity.this, new JSONObject(ex.getMessage()).getString("err"));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    Log.e(TAG, ex.getMessage());
                }
            }, Utils.getToken(this), BASE_URL + GET_SELF);
            get.execute();
            get.get();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * Do nothing so the user can't go back to the login activity with the back button after the login
     */
    @Override
    public void onBackPressed() {
        // Do nothing
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        EventService.getInstance().removeActivity();
        EventService.getInstance().stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventService.getInstance().setActivity(this);
    }

    @Override
    public void update() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                if(fragments!=null)

                {
                    for (Fragment fragment : fragments) {
                        ((ICustomCallback) fragment).update();
                    }
                }
            }
        });
    }
}
