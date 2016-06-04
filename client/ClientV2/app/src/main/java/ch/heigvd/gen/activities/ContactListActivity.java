package ch.heigvd.gen.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import ch.heigvd.gen.R;
import ch.heigvd.gen.adapters.ContactDiscussionAdapter;
import ch.heigvd.gen.adapters.ContactListAdapter;
import ch.heigvd.gen.adapters.ViewPagerAdapter;
import ch.heigvd.gen.communications.RequestGET;
import ch.heigvd.gen.interfaces.ICallback;
import ch.heigvd.gen.interfaces.ICustomCallback;
import ch.heigvd.gen.interfaces.IRequests;
import ch.heigvd.gen.models.Message;
import ch.heigvd.gen.models.User;
import ch.heigvd.gen.services.EventService;
import ch.heigvd.gen.utilities.Utils;

/**
 * TODO
 * TODO : Mettre toutes les String dans les fichiers de ressources fait pour
 * TODO : Commenter/indenter/Trier imports, compléter la javadoc
 * TODO : Mettre tous les éléments json et les requêtes dans IJSONKEYS et IREQUESTS
 * TODO : Faire des logs un peu mieux
 *
 *
 * TODO : Faire un bouton de déconnexion
 * TODO : Faire que les dialogues dans une discussion soient joli + afficher l'heure du message et le jour et le nom de l'utilisateur qui a envoyé pour les discussions de groupe
 *
 * TODO : Trier les utilisateurs par liste de derniers message
 *
 * TODO : Faire les report/blocage d'utilisateur et report de message dans groupe
 * TODO : Faire les discussion de groupe
 */
public class ContactListActivity extends AppCompatActivity implements IRequests{

    ArrayAdapter adapter = null;

    private final static String TAG = ContactListActivity.class.getSimpleName();

    /**
     * TODO
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.fragment_contact)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.fragment_group)));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                EventService.getInstance().setActivity((ICustomCallback) pagerAdapter.getItem(tab.getPosition()), ContactListActivity.this);
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




    /**
     * TODO
     *
     * @param view
     */
    public void addContact(final View view){
        // start contact search activity
        Intent intent = new Intent(ContactListActivity.this, ContactSearchActivity.class);
        startActivity(intent);
    }

    /**
     * TODO
     *
     * @param view
     */
    public void createGroup(final View view){
        // start contact create group activity
        Intent intent = new Intent(ContactListActivity.this, CreateGroupActivity.class);
        startActivity(intent);
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

}
