package ng.com.dayma.paymentdummy;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.MemberInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.PaymentInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentOpenHelper;

public class PaymentActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String PAYMENT_ID = "ng.com.dayma.paymentdummy.PAYMENT_ID";
    public static final int ID_NOT_SET = -1;
    public static final String SCHEDULE_INFO = "ng.com.dayma.paymentdummy.SCHEDULE_INFO";

    public static final String ORIGINAL_PAYMENT_ID = "ng.com.dayma.paymentdummy.ORIGINAL_PAYMENT_ID";
    public static final String ORIGINAL_MONTH_PAID = "ng.com.dayma.paymentdummy.ORIGINAL_MONTH_PAID";
    public static final String ORIGINAL_PAYMENT_RECEIPT_NO = "ng.com.dayma.paymentdummy.ORIGINAL_PAYMENT_RECEIPT_NO";
    public static final String ORIGINAL_PAYMENT_CHANDAAM = "ng.com.dayma.paymentdummy.ORIGINAL_PAYMENT_CHANDAAM";
    public static final String ORIGINAL_PAYMENT_WASIYYAT = "ng.com.dayma.paymentdummy.ORIGINAL_PAYMENT_WASIYYAT";
    public static final String ORIGINAL_PAYMENT_TAHRIKJADID = "ng.com.dayma.paymentdummy.ORIGINAL_PAYMENT_TAHRIKJADID";
    public static final String ORIGINAL_PAYMENT_WAQFJADID = "ng.com.dayma.paymentdummy.ORIGINAL_PAYMENT_WAQFJADID";
    public static final String ORIGINAL_SCHEDULE_ID = "ng.com.dayma.paymentdummy.ORIGINAL_SCHEDULE_ID";
    public static final int LOADER_PAYMENTS = 0;
    public static final int LOADER_MEMBERS = 1;
    private PaymentInfo mPayment = new PaymentInfo(DataManager.getInstance().getSchedules().get(0),-1,null,null,null);

    private Spinner mSpinnerMonthPaid;
    private boolean mIsNewPayment;
    private EditText mTextReceiptNo;
    private EditText mTextChandaAm;
    private EditText mTextWasiyyat;
    private EditText mTextJalsaSalana;
    private EditText mTextTahrikJadid;
    private EditText mTextWaqfJadid;
    private EditText mTextWelfare;
    private int mPaymentId;
    private boolean mIsCancelling;
    private MonthInfo mMonth;
    private ScheduleInfo mSchedule;
    private Spinner mSpinnerChandaNo;
    private int mOriginalChandaNo;
    private String mOriginalMonthPaid;
    private String mOriginalPaymentReceiptNo;
    private float mOriginalPaymentChandaAm;
    private float mOriginalPaymentWasiyyat;
    private float mOriginalPaymentTahrikJadid;
    private float mOriginalPaymentWaqfJadid;
    private String mOriginalScheduleId;
    private TextView mTextChandaNo;
    private PaymentOpenHelper mDbOpenHelper;
    private Cursor mPaymentCursor;
    private int mScheduleIdPos;
    private int mChandaNoPos;
    private int mFullnamePos;
    private int mLocalReceiptPos;
    private int mMonthPaidPos;
    private int mChandaPos;
    private int mWasiyyatPos;
    private int mJalsaPos;
    private int mTarikiPos;
    private int mWaqfPos;
    private int mWelfarePos;
    private int mScholarshipPos;
    private int mMaryamPos;
    private int mTablighPos;
    private int mZakatPos;
    private int mSadakatPos;
    private int mFitranaPos;
    private int mMosqueDonationPos;
    private int mMtaPos;
    private int mCentinaryPos;
    private int mWasiyyatHissanPos;
    private int mMiscellaneousPos;
    private int mSubtotalPos;
    private SimpleCursorAdapter mAdapterMemberIds;
    private boolean mMembersQueriesFinished;
    private boolean mPaymentQueriesFinished;
    private String mScheduleId;
    private boolean mIsSaving;
    private EditText mTextMaryam;
    private EditText mTextScholarship;
    private EditText mTextTabligh;
    private EditText mTextCentinary;
    private EditText mTextFitrana;
    private EditText mTextSadakat;
    private EditText mTextZakat;
    private EditText mTextMosqueDonation;
    private EditText mTextMta;
    private EditText mTextWasiyatHissan;
    private EditText mTextMiscellaneous;


    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new PaymentOpenHelper(this);

        mSpinnerMonthPaid = (Spinner) findViewById(R.id.spinner_monthpaid);
        mSpinnerChandaNo = (Spinner) findViewById(R.id.spinner_payment_text);

        mAdapterMemberIds = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[] {MemberInfoEntry.COLUMN_MEMBER_ID},
                new int[] {android.R.id.text1}, 0);

        mAdapterMemberIds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerChandaNo.setAdapter(mAdapterMemberIds);

        ArrayList<String> monthsYear = DataManager.getInstance().getMonthsOfTheYear();
        ArrayAdapter<String> adapterMonths =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthsYear);
        adapterMonths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMonthPaid.setAdapter(adapterMonths);


        readDisplayStateValue();
        //        loadMemberData();
        getLoaderManager().initLoader(LOADER_MEMBERS, null, this);

        if(savedInstanceState == null) {
            saveOriginalPaymentValues(); // when first created
        } else {
            restoreOriginalPaymentValues(savedInstanceState); // restore the values when activity is recreated
        }

        // get references to the views
        mTextChandaNo = (TextView) findViewById(R.id.text_payer_title);
        mTextReceiptNo = (EditText) findViewById(R.id.text_receiptno);
        mTextChandaAm = (EditText) findViewById(R.id.text_chandaam);
        mTextWasiyyat = (EditText) findViewById(R.id.text_wasiyyat);
        mTextTahrikJadid = (EditText) findViewById(R.id.text_tahrik);
        mTextWaqfJadid = (EditText) findViewById(R.id.text_waqf);
        mTextWelfare = (EditText) findViewById(R.id.text_welfare);
        mTextScholarship = (EditText) findViewById(R.id.text_scholarship);
        mTextMaryam = (EditText) findViewById(R.id.text_maryam_fund);
        mTextTabligh = (EditText) findViewById(R.id.text_tabligh);
        mTextZakat = (EditText) findViewById(R.id.text_zakat);
        mTextSadakat = (EditText) findViewById(R.id.text_sadakat);
        mTextFitrana = (EditText) findViewById(R.id.text_fitrana);
        mTextMosqueDonation = (EditText) findViewById(R.id.text_mosque_donation);
        mTextMta = (EditText)  findViewById(R.id.text_mta);
        mTextCentinary = (EditText) findViewById(R.id.text_centinary_khilafat);
        mTextWasiyatHissan = (EditText) findViewById(R.id.text_wasiyyathissan);
        mTextMiscellaneous = (EditText) findViewById(R.id.text_miscellaneous);
        mTextJalsaSalana = (EditText) findViewById(R.id.text_jalsa_salana);

        if(!mIsNewPayment)
            // populate the views with values if not a new payment
            getLoaderManager().initLoader(LOADER_PAYMENTS, null, this);
//            loadPaymentData();
    }

    private void loadMemberData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        String[] selectionColumns = {
                MemberInfoEntry._ID,
                MemberInfoEntry.COLUMN_MEMBER_ID,
                MemberInfoEntry.COLUMN_MEMBER_CHANDANO,
                MemberInfoEntry.COLUMN_MEMBER_FULLNAME,
                MemberInfoEntry.COLUMN_MEMBER_JAMAATNAME
        };
        Cursor cursor = db.query(MemberInfoEntry.TABLE_NAME,selectionColumns, null,
                null,null, null, MemberInfoEntry.COLUMN_MEMBER_FULLNAME);
        mAdapterMemberIds.changeCursor(cursor);

    }

    private void loadPaymentData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        String selection = PaymentInfoEntry._ID + " =? ";

        String[] selectionArgs = {
                Integer.toString(mPaymentId)
        };

        String[] paymentColumns = {
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
                PaymentInfoEntry.COLUMN_PAYMENT_MARYAMFUND,
                PaymentInfoEntry.COLUMN_PAYMENT_TABLIGH,
                PaymentInfoEntry.COLUMN_PAYMENT_ZAKAT,
                PaymentInfoEntry.COLUMN_PAYMENT_SADAKAT,
                PaymentInfoEntry.COLUMN_PAYMENT_FITRANA,
                PaymentInfoEntry.COLUMN_PAYMENT_MOSQUEDONATION,
                PaymentInfoEntry.COLUMN_PAYMENT_MTA,
                PaymentInfoEntry.COLUMN_PAYMENT_CENTINARYKHILAFAT,
                PaymentInfoEntry.COLUMN_PAYMENT_WASIYYATHISSANJAIDAD,
                PaymentInfoEntry.COLUMN_PAYMENT_MISCELLANEOUS,
                PaymentInfoEntry.COLUMN_PAYMENT_SUBTOTAL
        };
        mPaymentCursor = db.query(PaymentInfoEntry.TABLE_NAME, paymentColumns, selection,
                selectionArgs, null, null, null);
        // get the positions of the columns
        mChandaNoPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_MEMBER_CHANDANO);
        mFullnamePos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_MEMBER_FULLNAME);
        mLocalReceiptPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_LOCALRECEIPT);
        mMonthPaidPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MONTHPAID);
        mChandaPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_CHANDAAM);
        mWasiyyatPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_CHANDAWASIYYAT);
        mJalsaPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_JALSASALANA);
        mTarikiPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_TARIKIJADID);
        mWaqfPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_WAQFIJADID);
        mWelfarePos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_WELFAREFUND);
        mScholarshipPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_SCHOLARSHIP);
        mMaryamPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MARYAMFUND);
        mTablighPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_TABLIGH);
        mZakatPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_ZAKAT);
        mSadakatPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_SADAKAT);
        mFitranaPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_FITRANA);
        mMosqueDonationPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MOSQUEDONATION);
        mMtaPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MTA);
        mCentinaryPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_CENTINARYKHILAFAT);
        mWasiyyatHissanPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_WASIYYATHISSANJAIDAD);
        mMiscellaneousPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MISCELLANEOUS);
        mSubtotalPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_SUBTOTAL);

        mPaymentCursor.moveToNext();
        displayPayments();
    }

    private void restoreOriginalPaymentValues(Bundle savedInstanceState) {
        mOriginalChandaNo = Integer.parseInt(savedInstanceState.getString(ORIGINAL_PAYMENT_ID));
        mOriginalScheduleId = savedInstanceState.getString(ORIGINAL_SCHEDULE_ID);
        mOriginalMonthPaid = savedInstanceState.getString(ORIGINAL_MONTH_PAID);
        mOriginalPaymentReceiptNo = savedInstanceState.getString(ORIGINAL_PAYMENT_RECEIPT_NO);
        mOriginalPaymentChandaAm = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_CHANDAAM));
        mOriginalPaymentWasiyyat = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_WASIYYAT));
        mOriginalPaymentTahrikJadid = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_TAHRIKJADID));
        mOriginalPaymentWaqfJadid = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_WAQFJADID));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_PAYMENT_ID, String.valueOf(mOriginalChandaNo));
        outState.putString(ORIGINAL_SCHEDULE_ID, mOriginalScheduleId);
        outState.putString(ORIGINAL_MONTH_PAID, mOriginalMonthPaid);
        outState.putString(ORIGINAL_PAYMENT_RECEIPT_NO, String.valueOf(mOriginalPaymentReceiptNo));
        outState.putString(ORIGINAL_PAYMENT_CHANDAAM, String.valueOf(mOriginalPaymentChandaAm));
        outState.putString(ORIGINAL_PAYMENT_WASIYYAT, String.valueOf(mOriginalPaymentWasiyyat));
        outState.putString(ORIGINAL_PAYMENT_TAHRIKJADID, String.valueOf(mOriginalPaymentTahrikJadid));
        outState.putString(ORIGINAL_PAYMENT_WAQFJADID, String.valueOf(mOriginalPaymentWaqfJadid));
    }

    private void saveOriginalPaymentValues() {
        if(mIsNewPayment)
            return;
        mOriginalChandaNo = mPayment.getChandaNo();
        mOriginalMonthPaid = mPayment.getMonthPaid();
        mOriginalPaymentReceiptNo = mPayment.getReceiptNo();
        mOriginalPaymentChandaAm = mPayment.getChandaAm();
        mOriginalPaymentWasiyyat = mPayment.getWasiyyat();
        mOriginalPaymentTahrikJadid = mPayment.getTahrikJadid();
        mOriginalPaymentWaqfJadid = mPayment.getWaqfJadid();
        mOriginalScheduleId = mPayment.getSchedule().getScheduleId();
    }

    private void displayPayments() {

        int chandaNo = mPaymentCursor.getInt(mChandaNoPos);
        String fullname = mPaymentCursor.getString(mFullnamePos);
        String localReceipt = mPaymentCursor.getString(mLocalReceiptPos);
        String monthPaid = mPaymentCursor.getString(mMonthPaidPos);
        float chandaAm = mPaymentCursor.getFloat(mChandaPos);
        float wasiyyat = mPaymentCursor.getFloat(mWasiyyatPos);
        float jalsaSalana = mPaymentCursor.getFloat(mJalsaPos);
        float tarikiJadid = mPaymentCursor.getFloat(mTarikiPos);
        float waqfJadid = mPaymentCursor.getFloat(mWaqfPos);
        float welfare = mPaymentCursor.getFloat(mWelfarePos);
        float scholarship = mPaymentCursor.getFloat(mScholarshipPos);
        float maryam = mPaymentCursor.getFloat(mMaryamPos);
        float tabligh = mPaymentCursor.getFloat(mTablighPos);
        float zakat = mPaymentCursor.getFloat(mZakatPos);
        float sadakat = mPaymentCursor.getFloat(mSadakatPos);
        float fitrana = mPaymentCursor.getFloat(mFitranaPos);
        float mosqueDonation = mPaymentCursor.getFloat(mMosqueDonationPos);
        float mta = mPaymentCursor.getFloat(mMtaPos);
        float centinary = mPaymentCursor.getFloat(mCentinaryPos);
        float wasiyyatHissan = mPaymentCursor.getFloat(mWasiyyatHissanPos);
        float miscellaneous = mPaymentCursor.getFloat(mMiscellaneousPos);
        float subtotal = mPaymentCursor.getFloat(mSubtotalPos);

        List<String> monthsYear = DataManager.getInstance().getMonthsOfTheYear();

        int memberIndex = getIndexOfMemberId(chandaNo);
        mSpinnerChandaNo.setSelection(memberIndex);
        if(!mIsNewPayment){
            mTextChandaNo.setVisibility(View.VISIBLE); // show textview if not a new payment
            mSpinnerChandaNo.setVisibility(View.INVISIBLE); // hide spinner
        } else {
            mTextChandaNo.setVisibility(View.INVISIBLE); // show textview if not a new payment
            mSpinnerChandaNo.setVisibility(View.VISIBLE); // hide spinner
        }
        int monthIndex = monthsYear.indexOf(monthPaid);
        mSpinnerMonthPaid.setSelection(monthIndex);

        mTextChandaNo.setText(String.valueOf(chandaNo) + " - " + fullname);

        mTextReceiptNo.setText(String.valueOf(localReceipt));
        mTextChandaAm.setText(String.valueOf(chandaAm));
        mTextWasiyyat.setText(String.valueOf(wasiyyat));
        mTextJalsaSalana.setText(String.valueOf(jalsaSalana));
        mTextTahrikJadid.setText(String.valueOf(tarikiJadid));
        mTextWaqfJadid.setText(String.valueOf(waqfJadid));
        mTextWelfare.setText(String.valueOf(welfare));
        mTextScholarship.setText(String.valueOf(scholarship));
        mTextMaryam.setText(String.valueOf(maryam));
        mTextTabligh.setText(String.valueOf(tabligh));
        mTextZakat.setText(String.valueOf(zakat));
        mTextSadakat.setText(String.valueOf(sadakat));
        mTextFitrana.setText(String.valueOf(fitrana));
        mTextMosqueDonation.setText(String.valueOf(mosqueDonation));
        mTextMta.setText(String.valueOf(mta));
        mTextCentinary.setText(String.valueOf(centinary));
        mTextWasiyatHissan.setText(String.valueOf(wasiyyatHissan));
        mTextMiscellaneous.setText(String.valueOf(miscellaneous));
    }

    private int getIndexOfMemberId(int chandaNo) {
        // get cursor from the adapter
        Cursor cursor = mAdapterMemberIds.getCursor();
        int memberChandaNoPos = cursor.getColumnIndex(MemberInfoEntry.COLUMN_MEMBER_CHANDANO);
        int courseRowIndex = 0;

        boolean more = cursor.moveToFirst();
        while (more) {
            int memberCursorId = cursor.getInt(memberChandaNoPos);
            if(chandaNo ==(memberCursorId)){
                break;
            }
            courseRowIndex++;
            more = cursor.moveToNext();
        }
        return courseRowIndex;
    }

    private void readDisplayStateValue() {
        Intent intent = getIntent();
        //get value that was put into the intent
        mScheduleId = intent.getStringExtra(SCHEDULE_INFO);
        mSchedule = DataManager.getInstance().getSchedule(mScheduleId);
        mPaymentId = intent.getIntExtra(PAYMENT_ID, ID_NOT_SET);
        mIsNewPayment = mPaymentId == ID_NOT_SET;
        if(mIsNewPayment){
            createNewPayment();
        }
        else {
            mPayment = DataManager.getInstance().getPayment(mPaymentId);
        }

    }

    private void createNewPayment() {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                ContentValues values = new ContentValues();
                values.put(PaymentInfoEntry.COLUMN_MEMBER_CHANDANO, 0);
                values.put(PaymentInfoEntry.COLUMN_SCHEDULE_ID, mScheduleId);
                values.put(PaymentInfoEntry.COLUMN_PAYMENT_MONTHPAID, "");
                values.put(PaymentInfoEntry.COLUMN_PAYMENT_LOCALRECEIPT, "");
                values.put(PaymentInfoEntry.COLUMN_PAYMENT_CHANDAAM, 0);
                // get connection to the database
                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
                // insert new row
                mPaymentId = (int) db.insert(PaymentInfoEntry.TABLE_NAME, null, values);
                return null;
            }
        };
        task.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item1 = menu.findItem(R.id.action_next);
        MenuItem item2 = menu.findItem(R.id.action_previous);
        MenuItem item3 = menu.findItem(R.id.action_save);
//        int lastPaymentIndex = DataManager.getInstance().getPayments(mSchedule).size() - 1;
//        item1.setEnabled(mPaymentId < lastPaymentIndex);
//        item2.setEnabled(mPaymentId > 0); // if position is on index greater than startindex 0
//        if(mIsNewPayment){
//            item1.setEnabled(false); // disable next menu
//        }
        item3.setEnabled(true); // enable the button always
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_payment) {
//            sendEmail();
            deletePaymentFromDatabase();
            finish();
        } else if(id == R.id.action_cancel){
            mIsCancelling = true;
            finish();
        } else if(id == R.id.action_next){
            moveNext();
        } else if(id == R.id.action_previous){
            movePrevious();
        } else if(id == R.id.action_save) {
            mIsSaving = true;
            savePayment();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private void movePrevious() {
        savePayment();
        --mPaymentId;
        mPayment = DataManager.getInstance().getPayments(mSchedule).get(mPaymentId);

        saveOriginalPaymentValues();
        displayPayments(
        );
        invalidateOptionsMenu(); // to enable calling of onPrepareOptionsMenu again and check for
        // possible conditions to re-enable the menu item
    }

    private void moveNext() {
        savePayment(); // save the present Note we're looking at
        ++mPaymentId;
        mPayment = DataManager.getInstance().getPayments(mSchedule).get(mPaymentId);

        saveOriginalPaymentValues(); // save the next note displayed before any editing
        displayPayments(
        );
        invalidateOptionsMenu(); // to enable calling of onPrepareOptionsMenu again and check for
        // possible conditions to re-enable the menu item
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling){
            // check if we're cancelling on newpayment otherwise do nothing
            if(mIsNewPayment){
//                DataManager.getInstance().removePayment(mPaymentId);
                deletePaymentFromDatabase();
            } else {
                storePreviousPaymentValues();
            }
        } else if(!mIsSaving && mIsNewPayment) {
            deletePaymentFromDatabase(); // delete if payment is new and user is not saving
        }
    }

    private void deletePaymentFromDatabase() {
        final String selection = PaymentInfoEntry._ID + "=?";
        final String[] selectionArgs = { Integer.toString(mPaymentId)};
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
                db.delete(PaymentInfoEntry.TABLE_NAME, selection, selectionArgs);
                return null;
            }
        };
        task.execute();

    }

    private void storePreviousPaymentValues() {
        ScheduleInfo schedule = DataManager.getInstance().getSchedule(mOriginalScheduleId);
        mPayment.setSchedule(schedule);
        mPayment.setChandaNo(mOriginalChandaNo);
        mPayment.setMonthPaid(mOriginalMonthPaid);
        mPayment.setReceiptNo(mOriginalPaymentReceiptNo);
        mPayment.setChandaAm(mOriginalPaymentChandaAm);
        mPayment.setWasiyyat(mOriginalPaymentWasiyyat);
        mPayment.setTahrikJadid(mOriginalPaymentTahrikJadid);
        mPayment.setWaqfJadid(mOriginalPaymentWaqfJadid);
    }

    private void savePayment() {
        int chandaNo = selectedChandaNo();
        String fullname = getNameOfPayer(chandaNo);
        String monthPaid = String.valueOf(mSpinnerMonthPaid.getSelectedItem());
        String receiptNo = mTextReceiptNo.getText().toString();
        double chandaAm;
        double wasiyyat;
        double tahrikiJadid;
        double waqfJadid;
        double welfare;
        double jalsa;
        double scholarship;
        double maryam;
        double tabligh;
        double zakat;
        double sadakat;
        double fitrana;
        double mosqueDonation;
        double mta;
        double centinary;
        double wasiyyatHissan;
        double miscellaneous;
        if(mTextChandaAm.getText().toString().isEmpty() )
            chandaAm = 0.0;
        else {
            chandaAm = Float.valueOf(mTextChandaAm.getText().toString().trim());
        }
        if(mTextWasiyyat.getText().toString().isEmpty())
            wasiyyat = 0.0;
        else {
            wasiyyat = Float.valueOf(mTextWasiyyat.getText().toString().trim());
        }
        if(mTextTahrikJadid.getText().toString().isEmpty())
            tahrikiJadid = 0.0;
        else {
            tahrikiJadid = Float.valueOf(mTextTahrikJadid.getText().toString().trim());
        }
        if(mTextWaqfJadid.getText().toString().isEmpty())
            waqfJadid = 0.0;
        else {
            waqfJadid = Float.valueOf(mTextWaqfJadid.getText().toString().trim());
        }
        if(mTextWelfare.getText().toString().isEmpty())
            welfare = 0.0;
        else {
            welfare = Float.valueOf(mTextWelfare.getText().toString().trim());
        }
        if(mTextJalsaSalana.getText().toString().isEmpty())
            jalsa = 0.0;
        else {
            jalsa = Float.valueOf(mTextJalsaSalana.getText().toString().trim());
        }
        if(mTextMaryam.getText().toString().isEmpty())
            maryam = 0.0;
        else {
            maryam = Float.valueOf(mTextMaryam.getText().toString().trim());
        }
        if(mTextTabligh.getText().toString().isEmpty())
            tabligh = 0.0;
        else {
            tabligh = Float.valueOf(mTextTabligh.getText().toString().trim());
        }
        if(mTextZakat.getText().toString().isEmpty())
            zakat = 0.0;
        else {
            zakat = Float.valueOf(mTextZakat.getText().toString().trim());
        }
        if(mTextSadakat.getText().toString().isEmpty())
            sadakat = 0.0;
        else {
            sadakat = Float.valueOf(mTextSadakat.getText().toString().trim());
        }
        if(mTextFitrana.getText().toString().isEmpty())
            fitrana = 0.0;
        else {
            fitrana = Float.valueOf(mTextFitrana.getText().toString().trim());
        }
        if(mTextMosqueDonation.getText().toString().isEmpty())
            mosqueDonation = 0.0;
        else {
            mosqueDonation = Float.valueOf(mTextMosqueDonation.getText().toString().trim());
        }
        if(mTextMta.getText().toString().isEmpty())
            mta = 0.0;
        else {
            mta = Float.valueOf(mTextMta.getText().toString().trim());
        }
        if(mTextCentinary.getText().toString().isEmpty())
            centinary = 0.0;
        else {
            centinary = Float.valueOf(mTextCentinary.getText().toString().trim());
        }
        if(mTextWasiyatHissan.getText().toString().isEmpty())
            wasiyyatHissan = 0.0;
        else {
            wasiyyatHissan = Float.valueOf(mTextWasiyatHissan.getText().toString().trim());
        }
        if(mTextMiscellaneous.getText().toString().isEmpty())
            miscellaneous = 0.0;
        else {
            miscellaneous = Float.valueOf(mTextMiscellaneous.getText().toString().trim());
        }
        if(mTextScholarship.getText().toString().isEmpty())
            scholarship = 0.0;
        else {
            scholarship = Float.valueOf(mTextScholarship.getText().toString().trim());
        }


        double subtotal = chandaAm + wasiyyat + tahrikiJadid + waqfJadid + jalsa + welfare +
                tabligh + scholarship + maryam + zakat + fitrana + mosqueDonation +
                mta + centinary + wasiyyatHissan + sadakat + miscellaneous;

        savePaymentToDatabase(chandaNo,fullname,mScheduleId,monthPaid,receiptNo,chandaAm,wasiyyat,
                tahrikiJadid,waqfJadid,welfare,jalsa,tabligh,scholarship,maryam,zakat,fitrana,
                mosqueDonation,mta,centinary,wasiyyatHissan,sadakat,miscellaneous,subtotal);
    }

    private void savePaymentToDatabase(int chandaNo, String fullname, String schedule, String monthPaid,
                                       String receiptNo, double chandaAm, double wasiyyat, double tahrikiJadid,
                                       double waqfJadid, double welfare, double jalsa, double tabligh,
                                       double scholarship, double maryam, double zakat, double fitrana,
                                       double mosqueDonation, double mta, double centinary, double wasiyyatHissan,
                                       double sadakat, double miscellaneous, double subtotal){
        // selection criteria for row to update
        final String selection = PaymentInfoEntry._ID + "=?";
        final String[] selectionArgs = {Integer.toString(mPaymentId)};

        final ContentValues values = new ContentValues();
        values.put(PaymentInfoEntry.COLUMN_MEMBER_CHANDANO, chandaNo);
        values.put(PaymentInfoEntry.COLUMN_MEMBER_FULLNAME, fullname);
        values.put(PaymentInfoEntry.COLUMN_SCHEDULE_ID, schedule);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_MONTHPAID, monthPaid);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_LOCALRECEIPT, receiptNo);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_CHANDAAM, chandaAm);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_CHANDAWASIYYAT, wasiyyat);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_TARIKIJADID, tahrikiJadid);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_WAQFIJADID, waqfJadid);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_WELFAREFUND, welfare);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_JALSASALANA, jalsa);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_TABLIGH, tabligh);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_SCHOLARSHIP, scholarship);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_MARYAMFUND, maryam);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_ZAKAT, zakat);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_FITRANA, fitrana);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_MOSQUEDONATION, mosqueDonation);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_MTA, mta);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_CENTINARYKHILAFAT, centinary);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_WASIYYATHISSANJAIDAD, wasiyyatHissan);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_SADAKAT, sadakat);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_MISCELLANEOUS, miscellaneous);
        values.put(PaymentInfoEntry.COLUMN_PAYMENT_SUBTOTAL, subtotal);

        // get connection to the database
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.update(PaymentInfoEntry.TABLE_NAME, values, selection, selectionArgs);

    }

    private int selectedChandaNo() {
        int selectedPosition = mSpinnerChandaNo.getSelectedItemPosition();
        Cursor cursor = mAdapterMemberIds.getCursor();
        cursor.moveToPosition(selectedPosition);
        int chandaNoPos = cursor.getColumnIndex(MemberInfoEntry.COLUMN_MEMBER_ID);
        int chandaNo = cursor.getInt(chandaNoPos);
        return chandaNo;

    }

    private String getNameOfPayer(int chandaNo) {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        String[] projection = {
                MemberInfoEntry.COLUMN_MEMBER_CHANDANO,
                MemberInfoEntry.COLUMN_MEMBER_ID,
                MemberInfoEntry.COLUMN_MEMBER_JAMAATNAME,
                MemberInfoEntry.COLUMN_MEMBER_FULLNAME
        };
        String selection = MemberInfoEntry.COLUMN_MEMBER_CHANDANO + "=?";
        String[] selectionArgs = { Integer.toString(chandaNo)};
        Cursor cursor = db.query(MemberInfoEntry.TABLE_NAME, projection, selection,
                selectionArgs, null, null, null);
        // get the index column for Fullname
        int fullnamePos = cursor.getColumnIndex(MemberInfoEntry.COLUMN_MEMBER_FULLNAME);
        // move to first row of table
        cursor.moveToNext();
        String fullname = cursor.getString(fullnamePos);
        return fullname;
    }

    private void sendEmail() {
        String monthpaid = (String) mSpinnerMonthPaid.getSelectedItem();
        String subject = "Payment for " + monthpaid;
        String text = mTextChandaAm.getText().toString() + "\n" + mTextReceiptNo.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_PAYMENTS)
            loader = createLoaderPayments();
        else if(id == LOADER_MEMBERS)
            loader = createLoaderMembers();
        return loader;
    }

    private CursorLoader createLoaderMembers() {
        mMembersQueriesFinished = false;
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

                String[] selectionColumns = {
                        MemberInfoEntry._ID,
                        MemberInfoEntry.COLUMN_MEMBER_ID,
                        MemberInfoEntry.COLUMN_MEMBER_CHANDANO,
                        MemberInfoEntry.COLUMN_MEMBER_FULLNAME,
                        MemberInfoEntry.getQName(MemberInfoEntry.COLUMN_MEMBER_JAMAATNAME),
                };
                String selection = MemberInfoEntry.COLUMN_MEMBER_JAMAATNAME + "=?";
                String[] selectionArgs = { mSchedule.getJamaat()};

                return db.query(MemberInfoEntry.TABLE_NAME,selectionColumns, selection,
                        selectionArgs,null, null, MemberInfoEntry.COLUMN_MEMBER_FULLNAME);
            }
        };
    }

    private CursorLoader createLoaderPayments() {
        mPaymentQueriesFinished = false;
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

                String selection = PaymentInfoEntry._ID + " =? ";

                String[] selectionArgs = {
                        Integer.toString(mPaymentId)
                };

                String[] paymentColumns = {
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
                        PaymentInfoEntry.COLUMN_PAYMENT_MARYAMFUND,
                        PaymentInfoEntry.COLUMN_PAYMENT_TABLIGH,
                        PaymentInfoEntry.COLUMN_PAYMENT_ZAKAT,
                        PaymentInfoEntry.COLUMN_PAYMENT_SADAKAT,
                        PaymentInfoEntry.COLUMN_PAYMENT_FITRANA,
                        PaymentInfoEntry.COLUMN_PAYMENT_MOSQUEDONATION,
                        PaymentInfoEntry.COLUMN_PAYMENT_MTA,
                        PaymentInfoEntry.COLUMN_PAYMENT_CENTINARYKHILAFAT,
                        PaymentInfoEntry.COLUMN_PAYMENT_WASIYYATHISSANJAIDAD,
                        PaymentInfoEntry.COLUMN_PAYMENT_MISCELLANEOUS,
                        PaymentInfoEntry.COLUMN_PAYMENT_SUBTOTAL
                };
                return db.query(PaymentInfoEntry.TABLE_NAME, paymentColumns, selection,
                        selectionArgs, null, null, null);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_PAYMENTS)
            loadFinishedPayments(data);
        else if(loader.getId() == LOADER_MEMBERS){
            mAdapterMemberIds.changeCursor(data);
            mMembersQueriesFinished = true; // the query has finished loading in the background
            displayPaymentWhenQueriesFinished();
        }

    }

    private void loadFinishedPayments(Cursor data) {
        mPaymentCursor = data;
        // get the positions of the columns
        mChandaNoPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_MEMBER_CHANDANO);
        mFullnamePos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_MEMBER_FULLNAME);
        mLocalReceiptPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_LOCALRECEIPT);
        mMonthPaidPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MONTHPAID);
        mChandaPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_CHANDAAM);
        mWasiyyatPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_CHANDAWASIYYAT);
        mJalsaPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_JALSASALANA);
        mTarikiPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_TARIKIJADID);
        mWaqfPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_WAQFIJADID);
        mWelfarePos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_WELFAREFUND);
        mScholarshipPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_SCHOLARSHIP);
        mMaryamPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MARYAMFUND);
        mTablighPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_TABLIGH);
        mZakatPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_ZAKAT);
        mSadakatPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_SADAKAT);
        mFitranaPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_FITRANA);
        mMosqueDonationPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MOSQUEDONATION);
        mMtaPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MTA);
        mCentinaryPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_CENTINARYKHILAFAT);
        mWasiyyatHissanPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_WASIYYATHISSANJAIDAD);
        mMiscellaneousPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MISCELLANEOUS);
        mSubtotalPos = mPaymentCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_SUBTOTAL);

        mPaymentCursor.moveToNext();
        mPaymentQueriesFinished = true; // payment queries finished loading
        displayPaymentWhenQueriesFinished();

    }

    private void displayPaymentWhenQueriesFinished() {
        if(mMembersQueriesFinished && mPaymentQueriesFinished)
            displayPayments();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_PAYMENTS)
            if(mPaymentCursor != null){
                mPaymentCursor.close();
            }
        else if(loader.getId() == LOADER_MEMBERS)
            mAdapterMemberIds.changeCursor(null);

    }
}
