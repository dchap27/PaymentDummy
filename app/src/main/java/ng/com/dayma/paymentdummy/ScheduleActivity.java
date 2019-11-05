package ng.com.dayma.paymentdummy;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import ng.com.dayma.paymentdummy.data.PaymentProviderContract;

public class ScheduleActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_JAMAATS = 0;
    public static final int ID_NOT_SET = -1;
    public static final String SCHEDULE_MID = "ng.com.dayma.paymentdummy.SCHEDULE_MID";
    private final String TAG = getClass().getSimpleName();
    private Spinner mSpinnerMonth;
    private Spinner mSpinnerYear;
    private Spinner mSpinnerJamaat;
    private boolean mJamaatListQueriesFinished;
    private ArrayList<String> mJamaatList;
    private ArrayAdapter<String> mAdapterJamaat;
    private boolean mIsNewSchedule;
    private Uri mScheduleUri;
    private EditText mScheduleTitle;
    private Calendar mDateTime;
    private MonthInfo mMonth;
    private boolean mIsSaving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "**********onCreate**********");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mSpinnerMonth = (Spinner) findViewById(R.id.spinner_schedule_month);
        mSpinnerYear = (Spinner) findViewById(R.id.spinner_schedule_year);
        mSpinnerJamaat = (Spinner) findViewById(R.id.spinner_jamaat_name);

        Log.d(TAG,"Populate the Months into spinner");
        ArrayList<String> months = DataManager.getInstance().getMonthsOnly();
        ArrayAdapter<String> adapterMonths =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        adapterMonths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMonth.setAdapter(adapterMonths);

        Log.d(TAG,"Populate the years into spinner");
        ArrayList<String> years = DataManager.getInstance().getYearsOnly();
        ArrayAdapter<String> adapterYears =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        adapterYears.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerYear.setAdapter(adapterYears);

        Log.d(TAG,"Populate the jamaats into spinner");
        mJamaatList = new ArrayList<>();

        getLoaderManager().initLoader(LOADER_JAMAATS, null, this);
        mAdapterJamaat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mJamaatList);
        mAdapterJamaat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerJamaat.setAdapter(mAdapterJamaat);

        readDisplayStateVale();

        mScheduleTitle = (EditText) findViewById(R.id.schedule_title_edittext);

        if(mIsNewSchedule)
            initializeToCurrentMonthAndYear();


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

    private void readDisplayStateVale() {
        Log.d(TAG, "Reading display state value");
        Intent intent = getIntent();
        //get value that was put into the intent
        int mId = intent.getIntExtra(SCHEDULE_MID, ID_NOT_SET);
        mIsNewSchedule = mId == ID_NOT_SET;
        if(mIsNewSchedule){
            createNewSchedule();
        } else {
            mScheduleUri = ContentUris.withAppendedId(PaymentProviderContract.Schedules.CONTENT_URI, mId);
        }
    }

    private void createNewSchedule() {

        AsyncTask<ContentValues, Void, Uri> task = new AsyncTask<ContentValues, Void, Uri>() {

            private MonthInfo mMonth;

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
                mScheduleUri = uri;
            }
        };
        ContentValues values = new ContentValues();
        values.put(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TITLE, "Null-");
        values.put(PaymentProviderContract.Schedules.COLUMN_MONTH_ID, "Null-");
        values.put(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID, "Null-");
        values.put(PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME, "Null-");

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
            finish();
        } else if(id == R.id.action_delete_schedule){
            deleteScheduleFromDatabase();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteScheduleFromDatabase() {
        Log.i(TAG, "Deleting Schedule: "+ mScheduleUri);
        getContentResolver().delete(mScheduleUri, null, null);
    }

    private void saveSchedule() {
        String scheduleTitle = mScheduleTitle.getText().toString().trim();
        String month = String.valueOf(mSpinnerMonth.getSelectedItem());
        String year = String.valueOf(mSpinnerYear.getSelectedItem());
        String jamaat = String.valueOf(mSpinnerJamaat.getSelectedItem());
        String monthId = month +" " + year;
        long scheduleRowId = ContentUris.parseId(mScheduleUri);
        String scheduleId = monthId + year + jamaat + scheduleRowId;

        saveScheduleToDatabase(scheduleId, monthId, jamaat, scheduleTitle);
    }

    private void saveScheduleToDatabase(String scheduleId, String month, String jamaat, String scheduleTitle) {
        final ContentValues values = new ContentValues();
        values.put(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID, scheduleId);
        values.put(PaymentProviderContract.Schedules.COLUMN_MONTH_ID, month);
        values.put(PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME, jamaat);
        values.put(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TITLE, scheduleTitle);

        getContentResolver().update(mScheduleUri, values, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_JAMAATS) {
            mJamaatListQueriesFinished = false;
            Log.d(TAG, "Loading Jamaat list");
            loader = createLoaderJamaats();
        }
        return loader;
    }

    private CursorLoader createLoaderJamaats() {
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
            mJamaatListQueriesFinished = true;
            mSpinnerJamaat.setAdapter(mAdapterJamaat);
        }

    }

    private void onFinishedJamaatList(Cursor data) {
        Cursor jamaatListsCursor = data;
        int jamaatNamePos = jamaatListsCursor.getColumnIndex(PaymentProviderContract.Members.COLUMN_MEMBER_JAMAATNAME);
        while(jamaatListsCursor.moveToNext()){
            String jamaatName = jamaatListsCursor.getString(jamaatNamePos);
            if(!mJamaatList.contains(jamaatName)){
                mJamaatList.add(jamaatName);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_JAMAATS)
            mJamaatList= null;

    }
}
