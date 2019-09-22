package ng.com.dayma.paymentdummy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import ng.com.dayma.paymentdummy.data.PaymentOpenHelper;

import static ng.com.dayma.paymentdummy.ScheduleListActivity.POSITION_NOT_SET;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String MONTH_POSITION = "ng.com.dayma.paymentdummy.MONTH_POSITION";
    private ScheduleRecyclerAdapter mScheduleRecyclerAdapter;
    private RecyclerView mRecyclerItems;
    private GridLayoutManager mGridLayoutManager;
    private MonthRecyclerAdapter mMonthRecyclerAdapter;
    private PaymentOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // create an instance of the openhelper
        mDbOpenHelper = new PaymentOpenHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // passing false means if the settings already has a value, don't pass the default into it
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initialDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScheduleRecyclerAdapter.notifyDataSetChanged();
        updateNavHeader();
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close(); // close the helper when activity is destroy
        super.onDestroy();
    }

    private void updateNavHeader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textUserName = (TextView) headerView.findViewById(R.id.text_user_name);
        TextView textEmailAddress = (TextView) headerView.findViewById(R.id.text_user_email_address);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = pref.getString("user_display_name", "");
        String emailAddress = pref.getString("user_email_address", "");

        textUserName.setText(userName);
        textEmailAddress.setText(emailAddress);
    }

    private void initialDisplayContent() {
        mRecyclerItems = (RecyclerView) findViewById(R.id.list_schedules);
        mGridLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.schedule_grid_span));

        List<ScheduleInfo> schedules = DataManager.getInstance().getSchedules();
        mScheduleRecyclerAdapter = new ScheduleRecyclerAdapter(this, schedules);

        List<MonthInfo> months = DataManager.getInstance().getMonths();
        mMonthRecyclerAdapter = new MonthRecyclerAdapter(this, months);


        displaySchedules();

    }

    private void displaySchedules() {
        mRecyclerItems.setLayoutManager(mGridLayoutManager);
        mRecyclerItems.setAdapter(mScheduleRecyclerAdapter);

        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        selectNavigationMenuItem(R.id.nav_schedules);

    }

    private void displayMonths() {
        mRecyclerItems.setLayoutManager(mGridLayoutManager);
        mRecyclerItems.setAdapter(mMonthRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_months);

    }

    private void selectNavigationMenuItem(int id) {
        // allow the menu to be checked once selected
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(id).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_schedules) {
            displaySchedules();
        } else if (id == R.id.nav_months) {
            displayMonths();
        } else if (id == R.id.nav_share) {
            handleSelection(R.string.nav_share_message);
        } else if (id == R.id.nav_send) {
            handleSelection(R.string.nav_send_message);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleSelection(int message_id) {
        View view = findViewById(R.id.list_schedules);
        Snackbar.make(view, message_id, Snackbar.LENGTH_LONG).show();

    }

}
