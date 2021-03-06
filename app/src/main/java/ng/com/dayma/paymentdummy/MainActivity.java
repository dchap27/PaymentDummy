package ng.com.dayma.paymentdummy;

import android.Manifest;
import android.app.LoaderManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputFilter;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import ng.com.dayma.paymentdummy.MyViewModels.MainActivityViewModel;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.ScheduleInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentOpenHelper;
import ng.com.dayma.paymentdummy.data.PaymentProviderContract;
import ng.com.dayma.paymentdummy.touchhelpers.ScheduleClickAdapterListener;
import ng.com.dayma.paymentdummy.touchhelpers.ScheduleTouchHelperCallback;
import ng.com.dayma.paymentdummy.utils.CsvUtility;
import ng.com.dayma.paymentdummy.utils.PreferenceKeys;

import static android.content.Intent.EXTRA_MIME_TYPES;

public class MainActivity extends RuntimePermissionsActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>,
        ScheduleClickAdapterListener,
        SwipeRefreshLayout.OnRefreshListener {

    public static final int LOADER_SCHEDULES = 0;
    private static final int LOADER_MONTHS = 1;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 10;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 11;
    private static final int SCHEDULE_FILE_RESULT = 22;
    public final String TAG = getClass().getSimpleName();
    private ScheduleRecyclerAdapter mScheduleRecyclerAdapter;
    private RecyclerView mRecyclerItems;
    private GridLayoutManager mGridLayoutManager;
    private MonthRecyclerAdapter mMonthRecyclerAdapter;
    private PaymentOpenHelper mDbOpenHelper;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //
    private ActionMode mActionMode;
    private ActionModecallbacks mActionModecallbacks;
    private DataManager.LoadFromDatabase mLoadFromDatabase;
    private MainActivityViewModel mViewModel;
    private View mPopupDialogView;
    private Button mSaveJamaatNameDialog;
    private Button mCancelJamaatDialogAction;
    private EditText mJamaatEditText;
    private String mJamaatName;
    private SharedPreferences mSharedPref;
    private View mProgressView;
    private ProgressBar mProgressBar;
    private int mScheduleIdToWriteToCSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "**********onCreate*********");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // create an instance of the openhelper
        mDbOpenHelper = new PaymentOpenHelper(this);
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_main);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // passing false means if the settings already has a value, don't pass the default into it
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String isFirstLoad = mSharedPref.getString(PreferenceKeys.JAMAAT_INFO_FIRST_LOAD, null);
                if(isFirstLoad != null) {
                    Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                    // open ScheduleActivity for new entry
                    startActivity(intent);
                } else {
                    Snackbar.make(view, "You haven't set your jamaat in the preference settings",
                            Snackbar.LENGTH_LONG).show();
                    return;
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initialDisplayContent();
        if(mViewModel.isNewlyCreated && savedInstanceState != null){
            mViewModel.restoreState(savedInstanceState);
        }
        mViewModel.isNewlyCreated = false;
        handleDisplaySelection(mViewModel.navDrawerDisplaySelection);
        mActionModecallbacks = new ActionModecallbacks();
        isFirstUsage();
        openDrawer();

    }

    private void isFirstUsage(){
        Log.d(TAG, "Checking if this is user's first usage");

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstUsage = mSharedPref.getBoolean(PreferenceKeys.FIRST_TIME_USAGE, true);
        String isFirstLoad = mSharedPref.getString(PreferenceKeys.JAMAAT_INFO_FIRST_LOAD, null);
        mViewModel.mJamaatName = mSharedPref.getString(PreferenceKeys.JAMAAT_NAME_PREF, "");
        mViewModel.memberId = mSharedPref.getString(PreferenceKeys.MEMBER_ID, "0");

        if(isFirstUsage){
            Log.d(TAG, "IsFirstUsage: launching alert dialog");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.first_time_user_message);
            alertDialogBuilder.setTitle(R.string.welcome_title_message);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, "Onclick: launching settings screen");
                    SharedPreferences.Editor editor = mSharedPref.edit();
                    editor.putBoolean(PreferenceKeys.FIRST_TIME_USAGE, false);
                    editor.commit();
                    dialog.dismiss();
                    if(mViewModel.mJamaatName.length() <= 1 || mViewModel.memberId.length() <= 1)
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        if(!isFirstUsage && mViewModel.mJamaatName.length() <= 1){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.warning_message_jamaat_not_set);
            alertDialogBuilder.setTitle(R.string.warning_title_firstusage);
            alertDialogBuilder.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        if(!isFirstUsage && mViewModel.mJamaatName.length() > 1){
            firstDataLoading();
        }
    }

    private void loadJamaatInfoToDatabase(String jamaatName) {
        final int fileIdentifier = getResources().getIdentifier(jamaatName, "raw", this.getPackageName());
        try {
            InputStream inputstream = getResources().openRawResource(fileIdentifier);
            readDataToDatabase(inputstream, jamaatName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        switch(requestCode){
            case PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(intent.CATEGORY_OPENABLE);
                intent.setType("application/csv");
                String[] mimetypes = {"text/comma-separated-values", "text/csv"};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.putExtra(EXTRA_MIME_TYPES, mimetypes);
                }
                startActivityForResult(Intent.createChooser(intent, "Select a file"),
                        SCHEDULE_FILE_RESULT);
                break;

            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE:
                writeDataToCsV();
        }

    }

    private void writeDataToCsV() {

        AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {
            private String mMonthId;
            private String mScheduleId;
            private String mFileName;
            private AlertDialog mDialog;
            private Boolean mSuccess;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                initialiseProgressDialog(R.id.progress_bar_main_horizontal);
                alertDialogBuilder.setView(mProgressView);
                alertDialogBuilder.setCancelable(false);
                mDialog = alertDialogBuilder.create();
                mDialog.show();
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(1);
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                String[] scheduleProjection = {
                        PaymentProviderContract.Schedules._ID,
                        PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID,
                        PaymentProviderContract.Schedules.COLUMN_MONTH_ID,
                        PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME,
                };
                // publish progress
                publishProgress(2);
                String scheduleSelection = PaymentProviderContract.Schedules._ID + "=?";
                String[] scheduleArgs = { String.valueOf(mScheduleIdToWriteToCSV)};
                Cursor scheduleCursor = getContentResolver().query(PaymentProviderContract.Schedules.CONTENT_URI,
                        scheduleProjection,scheduleSelection,scheduleArgs, null);

                int scheduleIdPos = scheduleCursor.getColumnIndex(
                        PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID);
                int monthIdPos = scheduleCursor.getColumnIndex(
                        PaymentProviderContract.Schedules.COLUMN_MONTH_ID);
                mFileName = null;
                if(scheduleCursor.moveToNext()) {

                    mScheduleId = scheduleCursor.getString(scheduleIdPos);
                    String monthId = scheduleCursor.getString(monthIdPos);
                    String[] monthSplit = monthId.split(" ");
                    mMonthId = monthSplit[0].trim();
                    mFileName = mScheduleId;
                    mSuccess = true;
                    Log.d("CSVUtility", "File name reading... " + mFileName);
                }
                publishProgress(3);
                simulateLongRunningWork();
                scheduleCursor.close();
                if(!mSuccess)
                    return mSuccess;

                CsvUtility csvUtility = new CsvUtility(MainActivity.this);
                Log.d(TAG, "Writing to file");
                mSuccess = csvUtility.writeDatabaseToCSV(mFileName, mScheduleId, mMonthId);
                publishProgress(4);
                simulateLongRunningWork();
                if(mSuccess){
                    Uri uri = ContentUris.withAppendedId(PaymentProviderContract.Schedules.CONTENT_URI, mScheduleIdToWriteToCSV);
                    ContentValues values = new ContentValues();
                    values.put(PaymentProviderContract.Schedules._ID, mScheduleIdToWriteToCSV);
                    values.put(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ISCOMPLETE, 1);
                    getContentResolver().update(uri, values, null, null);
                    publishProgress(5);
                }
                return mSuccess;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                int progressValue = values[0];
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mProgressBar.setProgress(progressValue,true);
                } else
                    mProgressBar.setProgress(progressValue);

            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                View v = findViewById(R.id.list_schedules);
                mProgressBar.setVisibility(View.GONE);
                mDialog.cancel();
                if(aBoolean) {
                    Snackbar.make(v, "Schedule successfully exported to /ChandaPay/"+mMonthId +"/"+mFileName,
                            Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(v, "Something went wrong! Try again later!",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        };
        task.execute();
    }

    private void simulateLongRunningWork() {
        try {
            Thread.sleep(2000);
        } catch(Exception ex) {}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null){
            mViewModel.saveState(outState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_SCHEDULES, null, this);
        mLoadFromDatabase = new DataManager.LoadFromDatabase();
        mLoadFromDatabase.execute(this);
        updateNavHeader();

    }

    private void openDrawer() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SCHEDULE_FILE_RESULT && resultCode == RESULT_OK){
            Uri downloaddata = data.getData();
            String fileName = downloaddata.getLastPathSegment();
            String mime = MimeTypeMap.getSingleton().getFileExtensionFromUrl(String.valueOf(downloaddata));
            Log.d(TAG, "selected file Uri: "+ downloaddata);
            if(mime.toLowerCase().equals("csv")){
                Log.d(TAG, "File extension being read, "+ mime);
                try {
                    final InputStream csvFile = getContentResolver().openInputStream(downloaddata);
                    readDataToDatabaseFromIntent(csvFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void readDataToDatabaseFromIntent(final InputStream inputStream) {

        AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {

            private AlertDialog mDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setCancelable(false);
                initialiseProgressDialog(R.id.progress_bar_main_activity);
                alertDialogBuilder.setView(mProgressView);
                mDialog = alertDialogBuilder.create();
                mDialog.show();
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(1);
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                CsvUtility utility = new CsvUtility(MainActivity.this);
                publishProgress(2);
                Log.d(TAG, "reading into database");
                utility.readCSVToDatabase(mViewModel.jamaatToUpdate.toUpperCase(), inputStream);
                publishProgress(3);
                return true;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                int progressValue = values[0];
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mProgressBar.setProgress(progressValue,true);
                } else
                    mProgressBar.setProgress(progressValue);

            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                mDialog.dismiss();
                mProgressBar.setVisibility(View.GONE);
                View v = findViewById(R.id.list_schedules);
                Snackbar.make(v, String.format(
                        "%s member list added successfully!", mViewModel.jamaatToUpdate.toUpperCase()),
                        Snackbar.LENGTH_LONG).show();
            }
        };
        task.execute();
    }

    private void readDataToDatabase(final InputStream inputStream, final String jamaatName) throws FileNotFoundException {

        AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {

            private AlertDialog mDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setCancelable(false);
                initialiseProgressDialog(R.id.progress_bar_main_activity);
                alertDialogBuilder.setView(mProgressView);
                mDialog = alertDialogBuilder.create();
                mDialog.show();
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(1);
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                CsvUtility utility = new CsvUtility(MainActivity.this);
                publishProgress(2);
                Log.d(TAG, "reading into database");
                utility.readCSVToDatabase(jamaatName.toUpperCase(), inputStream);
                publishProgress(3);
                return true;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                int progressValue = values[0];
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mProgressBar.setProgress(progressValue,true);
                } else
                    mProgressBar.setProgress(progressValue);

            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                mDialog.dismiss();
                View v = findViewById(R.id.list_schedules);
                Snackbar.make(v, String.format(
                        "%s member list added successfully!", jamaatName.toUpperCase()),
                        Snackbar.LENGTH_LONG).show();
            }
        };
        task.execute();

    }

    private void loadSchedulesData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        final String[] scheduleColumns = {
                ScheduleInfoEntry.COLUMN_MONTH_ID,
                ScheduleInfoEntry._ID,
                ScheduleInfoEntry.COLUMN_MEMBER_JAMAATNAME,
                ScheduleInfoEntry.COLUMN_SCHEDULE_ISCOMPLETE,
                ScheduleInfoEntry.COLUMN_SCHEDULE_TITLE,
                ScheduleInfoEntry.COLUMN_SCHEDULE_ID

        };
        String scheduleOrder = ScheduleInfoEntry.COLUMN_MONTH_ID + "," + ScheduleInfoEntry.COLUMN_MEMBER_JAMAATNAME;
        Cursor cursor = db.query(PaymentDatabaseContract.ScheduleInfoEntry.TABLE_NAME, scheduleColumns,
                null, null, null, null, scheduleOrder);
        mScheduleRecyclerAdapter.changeCursor(cursor);
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
        TextView textMemberId = (TextView) headerView.findViewById(R.id.text_member_id_display);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = pref.getString(PreferenceKeys.USER_DISPLAY_NAME, "");
        String memberId = pref.getString(PreferenceKeys.MEMBER_ID, "");

        textUserName.setText(userName);
        textMemberId.setText("Member ID " + memberId);
    }

    private void initialDisplayContent() {
        mRecyclerItems = (RecyclerView) findViewById(R.id.list_schedules);
        mGridLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.schedule_grid_span));

        mScheduleRecyclerAdapter = new ScheduleRecyclerAdapter(this, null);

        mMonthRecyclerAdapter = new MonthRecyclerAdapter(this, null);
    }

    private void displaySchedules() {
        getLoaderManager().restartLoader(LOADER_SCHEDULES, null, this);
        mRecyclerItems.setLayoutManager(mGridLayoutManager);
        mScheduleRecyclerAdapter.setClickAdapter(this);

        mRecyclerItems.setAdapter(mScheduleRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_schedules);

    }

    private void displayMonths() {
//        loadMonthsData();
        getLoaderManager().restartLoader(LOADER_MONTHS, null, this);
        mRecyclerItems.setLayoutManager(mGridLayoutManager);
        ScheduleTouchHelperCallback callback = new ScheduleTouchHelperCallback(mMonthRecyclerAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        mMonthRecyclerAdapter.setTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerItems);
        mMonthRecyclerAdapter.setClickAdapter(this);

        mRecyclerItems.setAdapter(mMonthRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_months);

    }

    private void loadMonthsData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        final String[] monthColumns = {
                PaymentDatabaseContract.MonthInfoEntry._ID,
                PaymentDatabaseContract.MonthInfoEntry.COLUMN_MONTH_ID
        };

        Cursor cursor = db.query(PaymentDatabaseContract.MonthInfoEntry.TABLE_NAME, monthColumns, null,
                null, null, null, PaymentDatabaseContract.MonthInfoEntry.COLUMN_MONTH_ID);
        mMonthRecyclerAdapter.changeCursor(cursor);
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
            openSettingsActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == (R.id.nav_schedules) || id == R.id.nav_months ){
            handleDisplaySelection(id);
            // store the selection id to viewModel
            mViewModel.navDrawerDisplaySelection = item.getItemId();
        } else if (id == R.id.nav_settings) {
            openSettingsActivity();

        } else if (id == R.id.nav_add_new_jamaat_info){
            handleAddorUpdateJamaatInfo();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openSettingsActivity() {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    private void handleAddorUpdateJamaatInfo() {
        // Create AlertDialog Builder
        final View view = findViewById(R.id.list_schedules);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);
        boolean isMultipleJamaat = mSharedPref.getBoolean(PreferenceKeys.KEY_ENABLE_MULTIPLE_JAMAAT, false);
        final AlertDialog alertDialog;
        String dialogTitle = "Add New Jamaat";
        if(!isMultipleJamaat){
            dialogTitle = "Update Members Info";
            String memberId = mSharedPref.getString(PreferenceKeys.MEMBER_ID, "0");
            if(checkUserIsJamaatMember(memberId, mViewModel.mJamaatName)) {
                alertDialogBuilder.setMessage(
                        String.format(getString(R.string.message_warning_update_jamaat), mViewModel.mJamaatName.toUpperCase()));
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.requestAppPermissions(new
                                        String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                R.string.runtime_permissions_read_storage, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.setTitle(dialogTitle);
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            else{
                Snackbar.make(view,"You are not allowed to update " + mViewModel.mJamaatName,
                        Snackbar.LENGTH_LONG).show();
            }
        }
        else {
            initializePopUpDialog();
            alertDialogBuilder.setView(mPopupDialogView);
            alertDialogBuilder.setTitle(dialogTitle);
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            mSaveJamaatNameDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String jamaatName = mJamaatEditText.getText().toString().trim().toUpperCase();
                    if(!jamaatName.isEmpty()) {
                        Set<String> allowedJamaats = mSharedPref.getStringSet(PreferenceKeys.MULTI_SELECT_JAMAAT_PREF,null);
                        String baseJamaat = mSharedPref.getString(PreferenceKeys.JAMAAT_NAME_PREF, "");
                        if(allowedJamaats.contains(jamaatName.toLowerCase()) || baseJamaat.equals(jamaatName.toLowerCase())) {
                            mViewModel.jamaatToUpdate = jamaatName;
                            MainActivity.super.requestAppPermissions(new
                                            String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    R.string.runtime_permissions_read_storage, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                        } else {
                            Snackbar.make(view,
                                    String.format("%s is not among your lists of Jamaats", jamaatName.toUpperCase()), Snackbar.LENGTH_LONG).show();
                            return;
                        }
                    }else {
                        Snackbar.make(view, R.string.jamaat_name_warning, Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    alertDialog.cancel();
                }
            });
            mCancelJamaatDialogAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
            });
        }

    }

    private boolean checkUserIsJamaatMember(String memberId, String jamaatName) {

        String[] projection = {
                PaymentProviderContract.Members.COLUMN_MEMBER_JAMAATNAME,
                PaymentProviderContract.Members.COLUMN_MEMBER_CHANDANO
        };
        String selection = PaymentProviderContract.Members.COLUMN_MEMBER_JAMAATNAME + " = ? AND " +
                PaymentProviderContract.Members.COLUMN_MEMBER_CHANDANO + " = ?";
        String[] selectionArgs = { jamaatName.toUpperCase(), memberId };
        Cursor cursor = getContentResolver().query(PaymentProviderContract.Members.CONTENT_URI,
                projection, selection, selectionArgs,null);
        if(cursor.getCount() > 0){
            return true;
        }
        return false;
    }

    private void checkJamaatInfoExist(final String jamaatName) {

        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... strings) {
                String jamaat = strings[0];
                String[] projection = {
                        PaymentProviderContract.Members.COLUMN_MEMBER_JAMAATNAME,
                        PaymentProviderContract.Members.COLUMN_MEMBER_FULLNAME
                };
                String selection = PaymentProviderContract.Members.COLUMN_MEMBER_JAMAATNAME + " LIKE ?";
                String[] selectionArgs = { jamaat };
                Cursor cursor = getContentResolver().query(PaymentProviderContract.Members.CONTENT_URI,
                        projection, selection, selectionArgs,null);
                if(cursor.getCount() > 0){
                    cursor.close();
                    return true;
                }
                cursor.close();
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if(!aBoolean)
                    loadJamaatInfoToDatabase(jamaatName);
            }
        };
        task.execute(jamaatName);
    }

    private void handleDisplaySelection(int itemId){
        switch (itemId){
            case R.id.nav_months:
                displayMonths();
                break;
            case R.id.nav_schedules:
                displaySchedules();
                break;
            default:
                displaySchedules();

        }
    }

    private void initializePopUpDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        mPopupDialogView = layoutInflater.inflate(R.layout.jamaat_input_dialog, null);
        mJamaatEditText = (EditText) mPopupDialogView.findViewById(R.id.popup_jamaat_edit_text);
        mSaveJamaatNameDialog = (Button) mPopupDialogView.findViewById(R.id.save_input_dialog_jamaatname);
        mCancelJamaatDialogAction = (Button) mPopupDialogView.findViewById(R.id.cancel_input_dialog_jamaatname);

        // Apply the filters to control the input (alphanumeric)
        ArrayList<InputFilter> curInputFilters = new ArrayList<InputFilter>(Arrays.asList(mJamaatEditText.getFilters()));
        curInputFilters.add(0, new AlphaNumericInputFilter());
        curInputFilters.add(1, new InputFilter.AllCaps());
        InputFilter[] newInputFilters = curInputFilters.toArray(new InputFilter[curInputFilters.size()]);
        mJamaatEditText.setFilters(newInputFilters);
    }
    private void initialiseProgressDialog(int progressBarId){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        mProgressView = layoutInflater.inflate(R.layout.progress_view_dialog, null);
        mProgressBar = (ProgressBar) mProgressView.findViewById(progressBarId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_SCHEDULES) {
            Uri uri = PaymentProviderContract.Schedules.CONTENT_URI;
            final String[] scheduleColumns = {
                    PaymentProviderContract.Schedules.COLUMN_MONTH_ID,
                    PaymentProviderContract.Schedules._ID,
                    PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME,
                    PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ISCOMPLETE,
                    PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TITLE,
                    PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TOTALAMOUNT,
                    PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TOTALPAYERS,
                    PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID,

            };
            String scheduleOrder = PaymentProviderContract.Schedules.COLUMN_MONTH_ID + "," +
                    PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME;

            loader = new CursorLoader(this, uri, scheduleColumns, null, null, scheduleOrder);
            return loader;
        }
        else if(id == LOADER_MONTHS){
            loader = cursorLoaderMonths();
        }
        return loader;
    }

    private CursorLoader cursorLoaderMonths() {
        Uri uri = PaymentProviderContract.Months.CONTENT_URI;
        final String[] monthColumns = {
                PaymentProviderContract.Months._ID,
                PaymentProviderContract.Months.COLUMN_MONTH_ID
        };
        String sortOrder = PaymentProviderContract.Months.COLUMN_MONTH_ID;
        return new CursorLoader(this, uri, monthColumns, null, null, sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_SCHEDULES)
            mScheduleRecyclerAdapter.changeCursor(data);
        else if(loader.getId() == LOADER_MONTHS){
            mMonthRecyclerAdapter.changeCursor(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_SCHEDULES)
            mScheduleRecyclerAdapter.changeCursor(null);
        if(loader.getId() == LOADER_MONTHS){
            mMonthRecyclerAdapter.changeCursor(null);
        }
    }

    @Override
    public void onItemClicked(int adapterPosition, long cursorDataId) {
        enableActionMode(adapterPosition, (int) cursorDataId);
    }

    @Override
    public boolean onSingleClick() {
        if (mActionMode != null){
            return true;
        }
        return false;
    }

    private void enableActionMode(int position, int cursorIdPos) {
        if(mActionMode == null ){
            mActionMode = startActionMode(mActionModecallbacks);
        }
        togglePosition(position,cursorIdPos);
    }

    private void togglePosition(int position, int cursorIdPos) {
        mScheduleRecyclerAdapter.toggleSelection(position, cursorIdPos);
        int count = mScheduleRecyclerAdapter.getSelectedItemCount();
        if(count == 0){
            mActionMode.finish();
            mActionMode = null;
        } else {
            mActionMode.setTitle(String.valueOf(count) + " " + getString(R.string.selected_item_count));
            mActionMode.invalidate();// redraw the CAB
        }
    }

    @Override
    public void onRefresh() {
        loadInitialData();
    }

    private void loadInitialData() {
        mViewModel.mJamaatName = mSharedPref.getString(PreferenceKeys.JAMAAT_NAME_PREF, "");
        if(mViewModel.mJamaatName.length() > 1){
            firstDataLoading();
            loadAdditionalJamaatRecords();
        }
        (mRecyclerItems.getAdapter()).notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void loadAdditionalJamaatRecords() {
        boolean isMultiple = mSharedPref.getBoolean(PreferenceKeys.KEY_ENABLE_MULTIPLE_JAMAAT, false);
        if(isMultiple){
            String[] selectedPrefJamaats = mSharedPref.getStringSet(
                    PreferenceKeys.MULTI_SELECT_JAMAAT_PREF, null).toArray(new String[0]);
            for(int i =0; i< selectedPrefJamaats.length; i++){
                checkJamaatInfoExist(selectedPrefJamaats[i]);
            }
        }
    }

    private void firstDataLoading() {
        String isFirstLoad = mSharedPref.getString(PreferenceKeys.JAMAAT_INFO_FIRST_LOAD, null);
        boolean isMultipleJamaat = mSharedPref.getBoolean(PreferenceKeys.KEY_ENABLE_MULTIPLE_JAMAAT, false);
        if(isFirstLoad == null) {
            Log.d(TAG, "Initial loading of jamaat info into database");
            if (isMultipleJamaat) {
                String[] jamaats = mSharedPref.getStringSet(PreferenceKeys.MULTI_SELECT_JAMAAT_PREF, null).toArray(new String[0]);
                for (int i = 0; i < jamaats.length; i++) {
                    loadJamaatInfoToDatabase(jamaats[i]);
                    mViewModel.loadedJamaats = new ArrayList<>();
                    mViewModel.loadedJamaats.add(jamaats[i]);
                }
            }
            loadJamaatInfoToDatabase(mViewModel.mJamaatName);
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString(PreferenceKeys.JAMAAT_INFO_FIRST_LOAD, mViewModel.mJamaatName);
            editor.commit();
        }
    }

    // code for ActionMode
    private class ActionModecallbacks implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            MenuItem edit_menu = menu.findItem(R.id.edit_schedule);
            MenuItem export_menu = menu.findItem(R.id.export_schedule);
            if(mScheduleRecyclerAdapter.getSelectedItemCount() == 1){
                edit_menu.setEnabled(true);
                export_menu.setEnabled(true);
            } else {
                edit_menu.setEnabled(false);
                export_menu.setEnabled(false);
            }
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){

                case R.id.delete_schedule:
                    Log.d(TAG, "Delete Menu clicked");
                    deleteSchedule();
                    mode.finish();
                    return true;
                case R.id.edit_schedule:
                    Log.d(TAG, "Edit Menu clicked");
                    editSchedule();
                    mode.finish();
                    return true;
                case R.id.export_schedule:
                    Log.d(TAG, "Exporting schedule to CSV");
                    exportSchedule();
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mScheduleRecyclerAdapter.clearSelections();
            mActionMode = null;

        }
    }

    private void exportSchedule() {

        List selectedItemPositions = mScheduleRecyclerAdapter.getSelectedItems();
        mScheduleIdToWriteToCSV = mScheduleRecyclerAdapter.getScheduleCursorID((Integer) selectedItemPositions.get(0));
        mScheduleRecyclerAdapter.notifyDataSetChanged();
        mActionMode = null;

        MainActivity.super.requestAppPermissions(new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                R.string.runtime_permissions_write_storage, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);

    }

    private void deleteSchedule() {
        List selectedItemPositions =
                mScheduleRecyclerAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mScheduleRecyclerAdapter.removeData((Integer) selectedItemPositions.get(i));
        }
        mScheduleRecyclerAdapter.notifyDataSetChanged();
        mLoadFromDatabase.execute(this);
        Log.d(TAG, "done deleting");
        mActionMode = null;
    }

    private void editSchedule() {
        List selectedItemPositions = mScheduleRecyclerAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mScheduleRecyclerAdapter.editData((Integer) selectedItemPositions.get(i));
        }
        mScheduleRecyclerAdapter.notifyDataSetChanged();
        mActionMode = null;
    }

}
