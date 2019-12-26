package ng.com.dayma.paymentdummy;

import android.app.LoaderManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;

import ng.com.dayma.paymentdummy.MyViewModels.ScheduleActivityViewModel;
import ng.com.dayma.paymentdummy.data.PaymentProviderContract;
import ng.com.dayma.paymentdummy.utils.PreferenceKeys;

public class ScheduleActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_JAMAATS = 0;
    public static final int LOADER_SCHEDULES = 1;
    public static final int ID_NOT_SET = -1;
    public static final String SCHEDULE_MID = "ng.com.dayma.paymentdummy.SCHEDULE_MID";
    private final String TAG = getClass().getSimpleName();
    private Spinner mSpinnerMonth;
    private Spinner mSpinnerYear;
    private Spinner mSpinnerJamaat;
    private boolean mJamaatListQueriesFinished;
    private ArrayAdapter<String> mAdapterJamaat;
    private boolean mIsNewSchedule;
    private Uri mScheduleUri;
    private EditText mScheduleTitle;
    private Calendar mDateTime;
    private MonthInfo mMonth;
    private boolean mIsSaving;
    private Cursor mScheduleCursor;
    private int mScheduleTitlePos;
    private int mScheduleJamaatPos;
    private int mScheduleMonthPos;
    private boolean mScheduleQueriesFinished;
    private int mId;
    private Cursor mJamaatListsCursor;
    private ArrayList<String> mMonths;
    private ArrayList<String> mYears;
    private Uri mMonthUri;
    private boolean mSavedNewMonth;
    private DataManager.LoadFromDatabase mLoadFromDatabase;
    private ScheduleActivityViewModel mViewModel;
    private boolean mPrefMultipleJamaat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "**********onCreate**********");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewModel = ViewModelProviders.of(this).get(ScheduleActivityViewModel.class);

        mScheduleTitle = (EditText) findViewById(R.id.schedule_title_edittext);
        mSpinnerMonth = (Spinner) findViewById(R.id.spinner_schedule_month);
        mSpinnerYear = (Spinner) findViewById(R.id.spinner_schedule_year);
        mSpinnerJamaat = (Spinner) findViewById(R.id.spinner_jamaat_name);


        Log.d(TAG,"Populate the Months into spinner");
        mMonths = mViewModel.getMonths();
        ArrayAdapter<String> adapterMonths =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mMonths);
        adapterMonths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMonth.setAdapter(adapterMonths);

        Log.d(TAG,"Populate the years into spinner");
        mYears = mViewModel.getYears();
        ArrayAdapter<String> adapterYears =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mYears);
        adapterYears.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerYear.setAdapter(adapterYears);

        Log.d(TAG,"Populate the jamaats into spinner");
        getLoaderManager().initLoader(LOADER_JAMAATS, null, this);

        mAdapterJamaat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mViewModel.jamaatList);
        mAdapterJamaat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerJamaat.setAdapter(mAdapterJamaat);

        if(mViewModel.isNewlyCreated && savedInstanceState != null)
            mViewModel.restoreState(savedInstanceState);

        readDisplayStateValue();
        mViewModel.isNewlyCreated = false;

        if(mIsNewSchedule) {
            initializeToCurrentMonthAndYear();
        } else {
            getLoaderManager().initLoader(LOADER_SCHEDULES, null, this);
        }

    }

    private void initializeToCurrentMonthAndYear() {
        Log.d(TAG, "Initializing to current month and year");
        mDateTime = Calendar.getInstance();
        int currentMonth = mDateTime.get(Calendar.MONTH);
        int currentYear = mDateTime.get(Calendar.YEAR);
        mSpinnerMonth.setSelection(currentMonth);
        for(int i = 0; i<mSpinnerYear.getCount(); i++){
            if(mSpinnerYear.getItemAtPosition(i).equals(currentYear)){
                mSpinnerYear.setSelection(i);
            }
        }
    }

    private void readDisplayStateValue() {
        Log.d(TAG, "Reading display state value");
        Intent intent = getIntent();
        //get value that was put into the intent
        mViewModel.mId = intent.getIntExtra(SCHEDULE_MID, ID_NOT_SET);
        mIsNewSchedule = mViewModel.mId == ID_NOT_SET;
        if(mIsNewSchedule){
            createNewSchedule();
        } else {
            mViewModel.scheduleUri = ContentUris.withAppendedId(PaymentProviderContract.Schedules.CONTENT_URI, mViewModel.mId);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null)
            mViewModel.saveState(outState);
    }

    private void createNewSchedule() {

        AsyncTask<ContentValues, Void, Uri> task = new AsyncTask<ContentValues, Void, Uri>() {

            @Override
            protected void onPreExecute() {
                String currentMonth = String.format("%1$TB", mDateTime);
                mMonth = new MonthInfo(currentMonth);
            }

            @Override
            protected Uri doInBackground(ContentValues... contentValues) {
                ContentValues insertValues = contentValues[0];
                Uri uri = getContentResolver().insert(PaymentProviderContract.Schedules.CONTENT_URI, insertValues);
                return uri;
            }

            @Override
            protected void onPostExecute(Uri uri) {
                mViewModel.scheduleUri = uri;
            }
        };
        ContentValues values = new ContentValues();
        values.put(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TITLE, "Null");
        values.put(PaymentProviderContract.Schedules.COLUMN_MONTH_ID, "Null");
        values.put(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID, "Null");
        values.put(PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME, "Null");

        task.execute(values);

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "**********onPause**********");
        super.onPause();
        if(!mIsSaving && mIsNewSchedule){
            deleteScheduleFromDatabase();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_schedule, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item1 = menu.findItem(R.id.action_save_schedule);
        MenuItem item2 = menu.findItem(R.id.action_delete_schedule);
        item1.setEnabled(true);
        item2.setEnabled(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_save_schedule){
            mIsSaving = true;
            saveSchedule();
            if (!mIsNewSchedule)
                finish();
        } else if(id == R.id.action_delete_schedule){
            deleteScheduleFromDatabase();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteScheduleFromDatabase() {
        Log.i(TAG, "Deleting Schedule: "+ mScheduleUri);
        getContentResolver().delete(mViewModel.scheduleUri, null, null);
    }

    private void saveSchedule() {
        String scheduleTitle = mScheduleTitle.getText().toString().trim();
        String month = String.valueOf(mSpinnerMonth.getSelectedItem());
        String year = String.valueOf(mSpinnerYear.getSelectedItem());
        String jamaat = String.valueOf(mSpinnerJamaat.getSelectedItem());
        String monthId = month +" " + year;
        long scheduleRowId = ContentUris.parseId(mViewModel.scheduleUri);
        String scheduleId = month + year + jamaat + scheduleRowId;

        saveScheduleToDatabase(scheduleId, monthId, jamaat, scheduleTitle);
    }

    private void saveScheduleToDatabase(String scheduleId, final String month, String jamaat, String scheduleTitle) {
        final ContentValues values = new ContentValues();
        if(mIsNewSchedule) {
            values.put(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID, scheduleId);
        }
        values.put(PaymentProviderContract.Schedules.COLUMN_MONTH_ID, month);
        values.put(PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME, jamaat);
        values.put(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TITLE, scheduleTitle);

        getContentResolver().update(mViewModel.scheduleUri, values, null, null);

        /*
        check if Month already exist in database
        */
        AsyncTask<ContentValues, Void, Uri> task2 = new AsyncTask<ContentValues, Void, Uri>() {
            @Override
            protected Uri doInBackground(ContentValues... contentValues) {
                ContentValues newValues = contentValues[0];
                String[] projection = { PaymentProviderContract.Months.COLUMN_MONTH_ID,
                        PaymentProviderContract.Months._ID };
                String selection = PaymentProviderContract.Months.COLUMN_MONTH_ID + "=?";
                String[] selectionArgs = { month };
                Cursor cursor = getContentResolver().query(PaymentProviderContract.Months.CONTENT_URI,
                        projection, selection, selectionArgs, null);
                if (cursor.getCount() == 0) {
                    Log.d(TAG, "Insert new month values");
                    Uri uri = getContentResolver().insert(PaymentProviderContract.Months.CONTENT_URI, newValues);
                    return uri;
                }
                Log.d(TAG, "Record already found for month "+ month);
                return null;
            }

            @Override
            protected void onPostExecute(Uri uri) {
                Log.d(TAG, "onPostExecute called");
                mMonthUri = uri;
                mLoadFromDatabase = new DataManager.LoadFromDatabase();
                mLoadFromDatabase.execute(ScheduleActivity.this);
                finish();
            }
        };
        if(mIsNewSchedule) {
            final ContentValues newValues = new ContentValues();
            newValues.put(PaymentProviderContract.Months.COLUMN_MONTH_ID, month);
            task2.execute(newValues);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_JAMAATS) {
            Log.d(TAG, "Loading Jamaat list");
            loader = createLoaderJamaats();
        } else if (id == LOADER_SCHEDULES){
            loader = createLoaderSchedule();
        }
        return loader;
    }

    private CursorLoader createLoaderSchedule() {
        mScheduleQueriesFinished = false;
        mScheduleUri = ContentUris.withAppendedId(PaymentProviderContract.Schedules.CONTENT_URI, mViewModel.mId);
        final String[] scheduleColumns = {
                PaymentProviderContract.Schedules.COLUMN_MONTH_ID,
                PaymentProviderContract.Schedules._ID,
                PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME,
                PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ISCOMPLETE,
                PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TITLE,
                PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID,
        };
        Log.d(TAG, "Load schedule columns");
        return new CursorLoader(this, mScheduleUri, scheduleColumns, null, null, null);
    }

    private CursorLoader createLoaderJamaats() {
        mJamaatListQueriesFinished = false;
        Uri uri = PaymentProviderContract.Members.CONTENT_URI;
        String[] projection = {
                PaymentProviderContract.Members._ID,
                PaymentProviderContract.Members.COLUMN_MEMBER_JAMAATNAME,
        };

        return new CursorLoader(this, uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_JAMAATS) {
            onFinishedJamaatList(data);
            Log.d(TAG, "Finished loading jamaat list into spinner");
            mJamaatListQueriesFinished = true;
            mSpinnerJamaat.setAdapter(mAdapterJamaat);
            if(mPrefMultipleJamaat)
                mSpinnerJamaat.setEnabled(false);
            displayScheduleWhenQueryFinishes();
        } else if (loader.getId() == LOADER_SCHEDULES){
            onFinishedSchedule(data);
        }

    }

    private void onFinishedSchedule(Cursor data) {
        mScheduleCursor = data;
        mScheduleTitlePos = mScheduleCursor.getColumnIndex(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TITLE);
        mScheduleJamaatPos = mScheduleCursor.getColumnIndex(PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME);
        mScheduleMonthPos = mScheduleCursor.getColumnIndex(PaymentProviderContract.Schedules.COLUMN_MONTH_ID);

        mScheduleCursor.moveToFirst();
        mScheduleQueriesFinished = true;
        displayScheduleWhenQueryFinishes();
    }

    private void displayScheduleWhenQueryFinishes() {
        if(mJamaatListQueriesFinished && mScheduleQueriesFinished){
            displaySchedule();
        }
    }

    private void displaySchedule() {
        String scheduleTitle = mScheduleCursor.getString(mScheduleTitlePos);
        String jamaat = mScheduleCursor.getString(mScheduleJamaatPos);
        String month = mScheduleCursor.getString(mScheduleMonthPos);

        mScheduleTitle.setText(scheduleTitle);
        int jamaatIndex = mViewModel.jamaatList.indexOf(jamaat);
        mSpinnerJamaat.setSelection(jamaatIndex);
        mSpinnerJamaat.setEnabled(false);
        String[] monthAndYear = month.split(" ");
        String mon = monthAndYear[0];
        int year = Integer.valueOf(monthAndYear[1]);
        int monthIndex = mMonths.indexOf(mon);
        int yearIndex = mYears.indexOf(year);
        mSpinnerMonth.setSelection(monthIndex);
        mSpinnerYear.setSelection(yearIndex);

    }

    private void onFinishedJamaatList(Cursor data) {
        mJamaatListsCursor = data;
        int jamaatNamePos = mJamaatListsCursor.getColumnIndex(PaymentProviderContract.Members.COLUMN_MEMBER_JAMAATNAME);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String prefJamaat = sharedPref.getString(PreferenceKeys.JAMAAT_NAME_PREF, "");
        mPrefMultipleJamaat = sharedPref.getBoolean(PreferenceKeys.KEY_ENABLE_MULTIPLE_JAMAAT, false);
        if(mPrefMultipleJamaat) {
            while (mJamaatListsCursor.moveToNext()) {
                String jamaatName = mJamaatListsCursor.getString(jamaatNamePos);
                if (!(mViewModel.jamaatList).contains(jamaatName)) {
                    mViewModel.jamaatList.add(jamaatName);
                }
            }
            Log.d(TAG, "Multiple jamaat lists loaded into spinner");
        } else {
            while (mJamaatListsCursor.moveToNext()) {
                String jamaatName = mJamaatListsCursor.getString(jamaatNamePos);
                if (prefJamaat.toUpperCase().equals(jamaatName) && !(mViewModel.jamaatList).contains(jamaatName)) {
                    mViewModel.jamaatList.add(jamaatName);
                }
            }
            Log.d(TAG, prefJamaat + " loaded into spinner");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_JAMAATS) {
            mJamaatListsCursor= null;
        } else if (loader.getId() == LOADER_SCHEDULES){
            if(mScheduleCursor != null)
                mScheduleCursor = null;
        }

    }
}
