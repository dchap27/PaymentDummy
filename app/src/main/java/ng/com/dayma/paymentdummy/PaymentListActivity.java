package ng.com.dayma.paymentdummy;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.List;

public class PaymentListActivity extends AppCompatActivity {

    public static final int POSITION_NOT_SET = -1;
    public static final String SCHEDULE_POSITION = "ng.com.dayma.paymentdummy.SCHEDULE_POSITION";

//    private ArrayAdapter<PaymentInfo> mAdapterPayments;
    private List<PaymentInfo> mPayments;
    private boolean mIsNewSchedule;
    private ScheduleInfo mSchedule;
    private PaymentRecyclerAdapter mPaymentRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        readDisplayStateValues();
        initializeDisplayContent();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentListActivity.this, PaymentActivity.class);
                intent.putExtra(PaymentActivity.SCHEDULE_INFO, mSchedule.getScheduleId());
                // open PaymentActivity as intent for new payment
                startActivity(intent);
            }
        });

    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        //get value that was put into the intent
        int position = intent.getIntExtra(SCHEDULE_POSITION, POSITION_NOT_SET);
        mSchedule = DataManager.getInstance().getSchedules().get(position);
        mIsNewSchedule = position == POSITION_NOT_SET;
        if(mIsNewSchedule){
            createNewSchedule();
        }else {
            mPayments = DataManager.getInstance().getPayments(mSchedule);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mAdapterPayments.notifyDataSetChanged();
        mPaymentRecyclerAdapter.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {
//        final ListView listPayments = (ListView) findViewById(R.id.list_payments);
//
//        List<PaymentInfo> payments = DataManager.getInstance().getPayments();
//
//        mAdapterPayments = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mPayments);
//
//        listPayments.setAdapter(mAdapterPayments);
//
//        listPayments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(PaymentListActivity.this, PaymentActivity.class);
////                PaymentInfo payment = (PaymentInfo) listPayments.getItemAtPosition(position);
//                intent.putExtra(PaymentActivity.SCHEDULE_INFO, mSchedule.getScheduleId());
//                intent.putExtra(PaymentActivity.PAYMENT_POSITION, position);
//                startActivity(intent);
//            }
//        });
        final RecyclerView recyclerPayments = (RecyclerView) findViewById(R.id.list_payments);
        final LinearLayoutManager paymentsLayoutManager = new LinearLayoutManager(this);
        recyclerPayments.setLayoutManager(paymentsLayoutManager);

        List<PaymentInfo> payments = DataManager.getInstance().getPayments();
        mPaymentRecyclerAdapter = new PaymentRecyclerAdapter(this, mPayments);
        recyclerPayments.setAdapter(mPaymentRecyclerAdapter);

    }

    private void createNewSchedule() {
    }

}
