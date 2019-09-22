package ng.com.dayma.paymentdummy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static ng.com.dayma.paymentdummy.PaymentListActivity.POSITION_NOT_SET;

public class ScheduleListActivity extends AppCompatActivity {

    public static final String SCHEDULE_POSITION = "ng.com.dayma.paymentdummy.SCHEDULE_POSITION";
    public static final int POSITION_NOT_SET = -1;
    private List<MonthInfo> mMonths;
    private List<ScheduleInfo> mSchedules;
    private ArrayAdapter<ScheduleInfo> mAdaptermonths;
    private RecyclerView mRecyclerItems;
    private ScheduleRecyclerAdapter mMonthSchedulesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    protected void onResume() {
        super.onResume();
        mMonthSchedulesAdapter.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {
//        ListView listMonths = (ListView) findViewById(R.id.list_monthschedules);
//        DataManager dm = DataManager.getInstance();
//        mMonths = dm.getMonths();
//        mSchedules = dm.getSchedules();
//
//        mAdaptermonths = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mSchedules);
//
//        listMonths.setAdapter(mAdaptermonths);
//        listMonths.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(ScheduleListActivity.this, PaymentListActivity.class);
////                PaymentInfo payment = (PaymentInfo) listPayments.getItemAtPosition(position);
//                intent.putExtra(PaymentListActivity.SCHEDULE_POSITION, position);
//                startActivity(intent);
//            }
//        });
        Intent intent = getIntent();
        int position = intent.getIntExtra(SCHEDULE_POSITION, POSITION_NOT_SET);
        MonthInfo month = DataManager.getInstance().getMonths().get(position);

        mRecyclerItems = (RecyclerView) findViewById(R.id.list_monthschedules);
        List<ScheduleInfo> schedules = DataManager.getInstance().getSchedules(month);
        mMonthSchedulesAdapter = new ScheduleRecyclerAdapter(this, schedules);

        GridLayoutManager mMonthSchedulesLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerItems.setLayoutManager(mMonthSchedulesLayoutManager);
        mRecyclerItems.setAdapter(mMonthSchedulesAdapter);

    }

}
