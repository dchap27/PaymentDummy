package ng.com.dayma.paymentdummy;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.ScheduleInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentOpenHelper;

public class ScheduleListActivity extends AppCompatActivity {

    public static final String MONTH_ID = "ng.com.dayma.paymentdummy.MONTH_ID";
    public static final int ID_NOT_SET = -1;
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
        loadScheduleData();
//        mMonthSchedulesAdapter.notifyDataSetChanged();
    }

    private void loadScheduleData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        final String[] scheduleColumns = {
                ScheduleInfoEntry.COLUMN_MONTH_ID,
                ScheduleInfoEntry._ID,
                ScheduleInfoEntry.COLUMN_SCHEDULE_JAMAAT,
                ScheduleInfoEntry.COLUMN_SCHEDULE_IS_COMPLETE,
                ScheduleInfoEntry.COLUMN_SCHEDULE_TITLE,
                ScheduleInfoEntry.COLUMN_SCHEDULE_ID

        };
        String selection = ScheduleInfoEntry.COLUMN_MONTH_ID + "=?";
        String[] selectionArgs = { mMonID };
        String scheduleOrder = ScheduleInfoEntry.COLUMN_MONTH_ID + "," + ScheduleInfoEntry.COLUMN_SCHEDULE_JAMAAT;
        Cursor cursor = db.query(ScheduleInfoEntry.TABLE_NAME, scheduleColumns,
                selection, selectionArgs, null, null, scheduleOrder);
        mMonthSchedulesAdapter.changeCursor(cursor);

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

}
