package ng.com.dayma.paymentdummy;

import android.app.LoaderManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
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

import ng.com.dayma.paymentdummy.MyViewModels.PaymentListViewModel;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.PaymentInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentOpenHelper;
import ng.com.dayma.paymentdummy.data.PaymentProviderContract;
import ng.com.dayma.paymentdummy.data.PaymentProviderContract.Payments;

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
    private int mId;
    private PaymentListViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "**********onCreate*********");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new PaymentOpenHelper(this);
        mViewModel = ViewModelProviders.of(this).get(PaymentListViewModel.class);

        readDisplayStateValues();
        if(mViewModel.isNewlyCreated && savedInstanceState != null)
            mViewModel.restoreState(savedInstanceState);
        mViewModel.isNewlyCreated = false;
        mViewModel.setScheduleId(mScheID);
        mSchedule = mViewModel.getSchedule(mScheID);
        mViewModel.setId(mSchedule.getId());
        initializeDisplayContent();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentListActivity.this, PaymentActivity.class);
                intent.putExtra(PaymentActivity.SCHEDULE_INFO, mViewModel.getScheduleId());
                // open PaymentActivity as intent for new payment
                startActivity(intent);
            }
        });

    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        //get value that was put into the intent
        mScheID = intent.getStringExtra(SCHEDULE_ID);
//        mSchedule = DataManager.getInstance().getSchedule(mScheID);
//        mId = mSchedule.getId();
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
        String[] projection = {
                Payments.COLUMN_SCHEDULE_ID,
                Payments.COLUMN_PAYMENT_SUBTOTAL,
                Payments.COLUMN_MEMBER_CHANDANO,
        };
        String selection = Payments.COLUMN_SCHEDULE_ID + "=?";
        String[] selectionArgs = { mViewModel.getScheduleId() };
        Cursor paymentsCursor = getContentResolver().query(Payments.CONTENT_URI, projection,
                selection, selectionArgs, null);

        int amountPos = paymentsCursor.getColumnIndex(Payments.COLUMN_PAYMENT_SUBTOTAL);
        int chandaNoPos = paymentsCursor.getColumnIndex(Payments.COLUMN_MEMBER_CHANDANO);

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

        // selection criteria for row to update
        final String rowScheduleSelection = PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID + "=?";
        final String[] rowScheduleSelectionArgs = {mViewModel.getScheduleId()};

        ContentValues values = new ContentValues();
        values.put(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TOTALAMOUNT, totalAmount);
        values.put(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TOTALPAYERS, payers.size());
        Uri scheduleUri = ContentUris.withAppendedId(PaymentProviderContract.Schedules.CONTENT_URI, mViewModel.getId());
        getContentResolver().update(scheduleUri, values, rowScheduleSelection, rowScheduleSelectionArgs);

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
        String[] selectionArgs = { mViewModel.getScheduleId() };
        String paymentOrderby = PaymentInfoEntry.COLUMN_MEMBER_FULLNAME;
        Cursor cursor = db.query(PaymentInfoEntry.TABLE_NAME, paymentColumns, selection, selectionArgs,
                null, null, paymentOrderby);
        mPaymentRecyclerAdapter.changeCursor(cursor);
    }

    private void initializeDisplayContent() {
        getLoaderManager().initLoader(LOADER_PAYMENTS, null, this);
        final RecyclerView recyclerPayments = (RecyclerView) findViewById(R.id.list_payments);
        final LinearLayoutManager paymentsLayoutManager = new LinearLayoutManager(this);
        recyclerPayments.setLayoutManager(paymentsLayoutManager);

        mPaymentRecyclerAdapter = new PaymentRecyclerAdapter(this, null);
        recyclerPayments.setAdapter(mPaymentRecyclerAdapter);

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

                String[] paymentColumns = {
                        PaymentProviderContract.Payments._ID,
                        PaymentProviderContract.Payments.COLUMN_MEMBER_CHANDANO,
                        PaymentProviderContract.Payments.COLUMN_MEMBER_FULLNAME,
                        PaymentProviderContract.Payments.COLUMN_PAYMENT_LOCALRECEIPT,
                        PaymentProviderContract.Payments.COLUMN_PAYMENT_MONTHPAID,
                        PaymentProviderContract.Payments.COLUMN_PAYMENT_CHANDAAM,
                        PaymentProviderContract.Payments.COLUMN_PAYMENT_CHANDAWASIYYAT,
                        PaymentProviderContract.Payments.COLUMN_PAYMENT_JALSASALANA,
                        PaymentProviderContract.Payments.COLUMN_PAYMENT_TARIKIJADID,
                        PaymentProviderContract.Payments.COLUMN_PAYMENT_WAQFIJADID,
                        PaymentProviderContract.Payments.COLUMN_PAYMENT_WELFAREFUND,
                        PaymentProviderContract.Payments.COLUMN_PAYMENT_SCHOLARSHIP,
                        PaymentProviderContract.Payments.COLUMN_PAYMENT_SUBTOTAL,
                        PaymentProviderContract.Payments.COLUMN_SCHEDULE_ID
                };
                String selection = PaymentProviderContract.Payments.COLUMN_SCHEDULE_ID + "=?";
                String[] selectionArgs = { mViewModel.getScheduleId() };
                String paymentOrderby = PaymentProviderContract.Payments.COLUMN_MEMBER_FULLNAME + "," +
                        PaymentProviderContract.Payments.COLUMN_PAYMENT_SUBTOTAL;
                return getContentResolver().query(Payments.CONTENT_URI, paymentColumns, selection, selectionArgs,
                        paymentOrderby);
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
