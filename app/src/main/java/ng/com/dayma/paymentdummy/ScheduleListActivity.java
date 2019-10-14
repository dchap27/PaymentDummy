package ng.com.dayma.paymentdummy;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.List;

import ng.com.dayma.paymentdummy.data.PaymentOpenHelper;
import ng.com.dayma.paymentdummy.data.PaymentProviderContract;

public class ScheduleListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String MONTH_ID = "ng.com.dayma.paymentdummy.MONTH_ID";
    public static final int ID_NOT_SET = -1;
    public static final int LOADER_SCHEDULES = 0;
    private List<MonthInfo> mMonths;
    private List<ScheduleInfo> mSchedules;
    private ArrayAdapter<ScheduleInfo> mAdaptermonths;
    private RecyclerView mRecyclerItems;
    private ScheduleRecyclerAdapter mMonthSchedulesAdapter;
    private String mMonID;
    private PaymentOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new PaymentOpenHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initializeDisplayContent();
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_SCHEDULES, null, this);
    }

    private void loadScheduleData() {
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

        CursorLoader loader = new CursorLoader(this, uri, scheduleColumns, null, null, scheduleOrder);
    }

    private void initializeDisplayContent() {

        Intent intent = getIntent();
        mMonID = intent.getStringExtra(MONTH_ID);
        MonthInfo month = DataManager.getInstance().getMonth(mMonID);

        mRecyclerItems = (RecyclerView) findViewById(R.id.list_monthschedules);
//        List<ScheduleInfo> schedules = DataManager.getInstance().getSchedules(month);
        mMonthSchedulesAdapter = new ScheduleRecyclerAdapter(this, null);

        GridLayoutManager mMonthSchedulesLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerItems.setLayoutManager(mMonthSchedulesLayoutManager);
        mRecyclerItems.setAdapter(mMonthSchedulesAdapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_SCHEDULES){
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
            String selection = PaymentProviderContract.Schedules.COLUMN_MONTH_ID + "=?";
            String[] selectionArgs = { mMonID };
            String scheduleOrder = PaymentProviderContract.Schedules.COLUMN_MONTH_ID + "," +
                    PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME;

            loader = new CursorLoader(this, uri, scheduleColumns, selection, selectionArgs, scheduleOrder);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_SCHEDULES){
            mMonthSchedulesAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_SCHEDULES)
            mMonthSchedulesAdapter.changeCursor(null);
    }
}
