package ng.com.dayma.paymentdummy;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.PaymentInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentOpenHelper;

public class PaymentListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ID_NOT_SET = null;
    public static final String SCHEDULE_ID = "ng.com.dayma.paymentdummy.SCHEDULE_ID";
    public static final int LOADER_PAYMENTS = 0;
    private final String TAG = getClass().getSimpleName();

    //    private ArrayAdapter<PaymentInfo> mAdapterPayments;
    private List<PaymentInfo> mPayments;
    private boolean mIsNewSchedule;
    private ScheduleInfo mSchedule;
    private PaymentRecyclerAdapter mPaymentRecyclerAdapter;
    private String mScheID;
    private SQLiteOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "**********onCreate*********");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new PaymentOpenHelper(this);

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
        mScheID = intent.getStringExtra(SCHEDULE_ID);
        mSchedule = DataManager.getInstance().getSchedule(mScheID);
        mIsNewSchedule = mScheID == ID_NOT_SET;
        if(mIsNewSchedule){
            createNewSchedule();
        }else {
            mPayments = DataManager.getInstance().getPayments(mSchedule);
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "**********onResume*********");
        super.onResume();
        getLoaderManager().restartLoader(LOADER_PAYMENTS, null, this);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "**********onPause*********");
        super.onPause();
        // update the totalAmount on Schedule table
        updateTotalAmountOnSchedule();
    }


    private void updateTotalAmountOnSchedule() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        String[] projection = {
                PaymentInfoEntry.COLUMN_SCHEDULE_ID,
                PaymentInfoEntry.COLUMN_PAYMENT_SUBTOTAL,
                PaymentInfoEntry.COLUMN_MEMBER_CHANDANO,
        };
        String selection = PaymentInfoEntry.COLUMN_SCHEDULE_ID + "=?";
        String[] selectionArgs = { mScheID };
        Cursor paymentsCursor = db.query(PaymentInfoEntry.TABLE_NAME, projection,selection,selectionArgs,
                null, null, null);

        int amountPos = paymentsCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_SUBTOTAL);
        int chandaNoPos = paymentsCursor.getColumnIndex(PaymentInfoEntry.COLUMN_MEMBER_CHANDANO);

        ArrayList<Integer> payers = new ArrayList<>();
        double totalAmount = 0;

        while(paymentsCursor.moveToNext()){
            double amount = paymentsCursor.getFloat(amountPos);
            totalAmount = totalAmount + amount;
            int payer = paymentsCursor.getInt(chandaNoPos);
            if(payers.contains(payer))
                continue;
            payers.add(payer);
        }
        paymentsCursor.close();

        SQLiteDatabase db1 = mDbOpenHelper.getWritableDatabase();
        // selection criteria for row to update
        final String rowScheduleSelection = PaymentDatabaseContract.ScheduleInfoEntry.COLUMN_SCHEDULE_ID + "=?";
        final String[] rowScheduleSelectionArgs = {mScheID};

        ContentValues values = new ContentValues();
        values.put(PaymentDatabaseContract.ScheduleInfoEntry.COLUMN_SCHEDULE_TOTALAMOUNT, totalAmount);
        values.put(PaymentDatabaseContract.ScheduleInfoEntry.COLUMN_SCHEDULE_TOTALPAYERS, payers.size());
        db1.update(PaymentDatabaseContract.ScheduleInfoEntry.TABLE_NAME, values, rowScheduleSelection, rowScheduleSelectionArgs);

    }

    private void loadPaymentsData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        String[] paymentColumns = {
                PaymentInfoEntry._ID,
                PaymentInfoEntry.COLUMN_MEMBER_CHANDANO,
                PaymentInfoEntry.COLUMN_MEMBER_FULLNAME,
                PaymentInfoEntry.COLUMN_PAYMENT_LOCALRECEIPT,
                PaymentInfoEntry.COLUMN_PAYMENT_MONTHPAID,
                PaymentInfoEntry.COLUMN_PAYMENT_CHANDAAM,
                PaymentInfoEntry.COLUMN_PAYMENT_CHANDAWASIYYAT,
                PaymentInfoEntry.COLUMN_PAYMENT_JALSASALANA,
                PaymentInfoEntry.COLUMN_PAYMENT_TARIKIJADID,
                PaymentInfoEntry.COLUMN_PAYMENT_WAQFIJADID,
                PaymentInfoEntry.COLUMN_PAYMENT_WELFAREFUND,
                PaymentInfoEntry.COLUMN_PAYMENT_SCHOLARSHIP,
                PaymentInfoEntry.COLUMN_PAYMENT_SUBTOTAL,
                PaymentInfoEntry.COLUMN_SCHEDULE_ID
        };
        String selection = PaymentInfoEntry.COLUMN_SCHEDULE_ID + "=?";
        String[] selectionArgs = { mScheID };
        String paymentOrderby = PaymentInfoEntry.COLUMN_MEMBER_FULLNAME;
        Cursor cursor = db.query(PaymentInfoEntry.TABLE_NAME, paymentColumns, selection, selectionArgs,
                null, null, paymentOrderby);
        mPaymentRecyclerAdapter.changeCursor(cursor);
    }

    private void initializeDisplayContent() {

        final RecyclerView recyclerPayments = (RecyclerView) findViewById(R.id.list_payments);
        final LinearLayoutManager paymentsLayoutManager = new LinearLayoutManager(this);
        recyclerPayments.setLayoutManager(paymentsLayoutManager);

        mPaymentRecyclerAdapter = new PaymentRecyclerAdapter(this, null);
        recyclerPayments.setAdapter(mPaymentRecyclerAdapter);

    }

    private void createNewSchedule() {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_PAYMENTS){
            loader = createPaymentsLoader();
        }
        return loader;
    }

    private CursorLoader createPaymentsLoader() {
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

                String[] paymentColumns = {
                        PaymentInfoEntry._ID,
                        PaymentInfoEntry.COLUMN_MEMBER_CHANDANO,
                        PaymentInfoEntry.COLUMN_MEMBER_FULLNAME,
                        PaymentInfoEntry.COLUMN_PAYMENT_LOCALRECEIPT,
                        PaymentInfoEntry.COLUMN_PAYMENT_MONTHPAID,
                        PaymentInfoEntry.COLUMN_PAYMENT_CHANDAAM,
                        PaymentInfoEntry.COLUMN_PAYMENT_CHANDAWASIYYAT,
                        PaymentInfoEntry.COLUMN_PAYMENT_JALSASALANA,
                        PaymentInfoEntry.COLUMN_PAYMENT_TARIKIJADID,
                        PaymentInfoEntry.COLUMN_PAYMENT_WAQFIJADID,
                        PaymentInfoEntry.COLUMN_PAYMENT_WELFAREFUND,
                        PaymentInfoEntry.COLUMN_PAYMENT_SCHOLARSHIP,
                        PaymentInfoEntry.COLUMN_PAYMENT_SUBTOTAL,
                        PaymentInfoEntry.COLUMN_SCHEDULE_ID
                };
                String selection = PaymentInfoEntry.COLUMN_SCHEDULE_ID + "=?";
                String[] selectionArgs = { mScheID };
                String paymentOrderby = PaymentInfoEntry.COLUMN_MEMBER_FULLNAME + "," + PaymentInfoEntry.COLUMN_PAYMENT_SUBTOTAL;
                return db.query(PaymentInfoEntry.TABLE_NAME, paymentColumns, selection, selectionArgs,
                        null, null, paymentOrderby);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_PAYMENTS)
            mPaymentRecyclerAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_PAYMENTS)
            mPaymentRecyclerAdapter.changeCursor(null);
    }
}
