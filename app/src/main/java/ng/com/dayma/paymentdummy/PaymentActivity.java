package ng.com.dayma.paymentdummy;

import android.app.LoaderManager;
import androidx.lifecycle.ViewModelProviders;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ng.com.dayma.paymentdummy.MyViewModels.PaymentActivityViewModel;
import ng.com.dayma.paymentdummy.data.PaymentOpenHelper;
import ng.com.dayma.paymentdummy.data.PaymentProviderContract;
import ng.com.dayma.paymentdummy.data.PaymentProviderContract.Members;

public class PaymentActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        MultiSelectSpinner.MultiSpinnerListener {
    public static final String PAYMENT_ID = "ng.com.dayma.paymentdummy.PAYMENT_ID";
    public static final int ID_NOT_SET = -1;
    private final String TAG = getClass().getSimpleName();
    public static final String SCHEDULE_INFO = "ng.com.dayma.paymentdummy.SCHEDULE_INFO";
    public static final int LOADER_PAYMENTS = 0;
    public static final int LOADER_MEMBERS = 1;
    private MultiSelectSpinner mSpinnerMonthPaid;
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
    private Uri mPaymentUri;
    private int mChandaNo;
    private float mOriginalPaymentWelfare;
    private float mOriginalPaymentScholarship;
    private float mOriginalPaymentTabligh;
    private float mOriginalPaymentMta;
    private float mOriginalPaymentMaryam;
    private float mOriginalPaymentMosqueDonation;
    private float mOriginalPaymentSadaqa;
    private float mOriginalPaymentZakat;
    private float mOriginalPaymentFitrana;
    private String mOriginalPaymentFullname;
    private float mOriginalPaymentMiscellaneous;
    private float mOriginalPaymentCentinary;
    private float mOriginalPaymentWasiyyatHissan;
    private float mOriginalPaymentJalsaSalana;
    private PaymentActivityViewModel mViewModel;


    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"*********onCreate**********");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        enableStrictMode();

        mDbOpenHelper = new PaymentOpenHelper(this);
        mViewModel = ViewModelProviders.of(this).get(PaymentActivityViewModel.class);

        mSpinnerMonthPaid = (MultiSelectSpinner) findViewById(R.id.spinner_monthpaid);
        mSpinnerChandaNo = (Spinner) findViewById(R.id.spinner_payment_text);

        Log.d(TAG,"populate the members id into spinner");
        mAdapterMemberIds = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[] {PaymentProviderContract.Members.COLUMN_MEMBER_ID},
                new int[] {android.R.id.text1}, 0);

        mAdapterMemberIds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerChandaNo.setAdapter(mAdapterMemberIds);

        Log.d(TAG,"populate months into spinner");
        mSpinnerMonthPaid.setItems(mViewModel.monthsYear, getString(R.string.for_all), this);


        readDisplayStateValue();

        if(savedInstanceState == null) {
            mViewModel.saveOriginalPaymentValues(); // when first created
        } else if(mViewModel.isNewlyCreated && savedInstanceState != null){
            Log.d(TAG, "Restoring Original values from savedInstanceState");
            mViewModel.restoreOriginalPaymentValues(savedInstanceState); // restore the values when activity is recreated
        }
        mViewModel.isNewlyCreated = false;

        getLoaderManager().initLoader(LOADER_MEMBERS, null, this);

        // get references to the views
        mTextChandaNo = (TextView) findViewById(R.id.text_payer_name);
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

        if(!mIsNewPayment){
            // populate the views with values if not a new payment
            getLoaderManager().initLoader(LOADER_PAYMENTS, null, this);
        }
        else {
            initializeMonthPaid();
        }
    }

//    private void enableStrictMode() {
//        if (BuildConfig.DEBUG){
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .build();
//            StrictMode.setThreadPolicy(policy);
//
//        }
//
//    }

    private void initializeMonthPaid() {

        String preselectedMonth;
        String[] scheduleMonth = mViewModel.mSchedule.getMonth().getMonthId().split(" ");
        String month = scheduleMonth[0].toUpperCase();
        String year = scheduleMonth[1];
        String preselect = month.substring(0,3);
        preselectedMonth = preselect + year;
        ArrayList<String> preselectMonthLst = new ArrayList<>();
        preselectMonthLst.add(preselectedMonth);
        mSpinnerMonthPaid.setSelection(preselectMonthLst);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mViewModel.saveState(outState);

    }

    private void displayPayments() {

        mChandaNo = mPaymentCursor.getInt(mChandaNoPos);
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

        List<String> monthsYear = DataManager.getInstance().getMonthsWithYear();

        int memberIndex = getIndexOfMemberId(mChandaNo);
        mSpinnerChandaNo.setSelection(memberIndex);
        if(!mIsNewPayment){
            mTextChandaNo.setVisibility(View.VISIBLE); // show textview if not a new payment
            mSpinnerChandaNo.setVisibility(View.INVISIBLE); // hide spinner
        } else {
            mTextChandaNo.setVisibility(View.INVISIBLE); // show textview if not a new payment
            mSpinnerChandaNo.setVisibility(View.VISIBLE); // hide spinner
        }
        String[] monthPaidList = monthPaid.split(",");
        ArrayList<String> monthsPaid = new ArrayList<>();
        for(int i = 0; i< monthPaidList.length; i++){
            monthsPaid.add(monthPaidList[i].trim());
        }
        int monthIndex = monthsYear.indexOf(monthPaid);
        mSpinnerMonthPaid.setSelection(monthsPaid);

        mTextChandaNo.setText(String.valueOf(mChandaNo) + " - " + fullname);

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
        int memberChandaNoPos = cursor.getColumnIndex(PaymentProviderContract.Members.COLUMN_MEMBER_CHANDANO);
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
        mViewModel.mScheduleId = intent.getStringExtra(SCHEDULE_INFO);
        mViewModel.mSchedule = DataManager.getInstance().getSchedule(mViewModel.mScheduleId);
        mViewModel.mPaymentId = intent.getIntExtra(PAYMENT_ID, ID_NOT_SET);
        mViewModel.getScheduleStatus(this, mViewModel.mScheduleId);
        mIsNewPayment = mViewModel.mPaymentId == ID_NOT_SET;
        if(mIsNewPayment){
            createNewPayment();
        }
        else {
            mViewModel.mPayment = DataManager.getInstance().getPayment(mViewModel.mPaymentId);
            Log.i(TAG, "Generating Uri for payment " + mViewModel.mPaymentId);
            mViewModel.mPaymentUri = ContentUris.withAppendedId(PaymentProviderContract.Payments.CONTENT_URI, mViewModel.mPaymentId);
        }

    }

    private void createNewPayment() {

        AsyncTask<ContentValues, Integer, Uri> task = new AsyncTask<ContentValues, Integer, Uri>() {
            private ProgressBar mProgressBar;

            @Override
            protected void onPreExecute() {
                mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_horizontal);
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(1);
            }

            @Override
            protected Uri doInBackground(ContentValues... contentValues) {
                Log.d(TAG, "doInBackground - thread: " + Thread.currentThread().getId());
                ContentValues insertValues = contentValues[0];
                Uri uri = getContentResolver().insert(PaymentProviderContract.Payments.CONTENT_URI, insertValues);
                publishProgress(2);
                simulateLongRunningWork();
                publishProgress(3);

                return uri;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                int progressValue = values[0];
                mProgressBar.setProgress(progressValue);
            }

            @Override
            protected void onPostExecute(Uri uri) {
                Log.d(TAG, "onPostExecute - thread: " + Thread.currentThread().getId());
                mViewModel.mPaymentUri = uri;
                mProgressBar.setVisibility(View.GONE);
            }
        };

        ContentValues values = new ContentValues();
        values.put(PaymentProviderContract.Payments.COLUMN_MEMBER_CHANDANO, 0);
        values.put(PaymentProviderContract.Payments.COLUMN_SCHEDULE_ID, mViewModel.mScheduleId);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_MONTHPAID, "");
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_LOCALRECEIPT, "");
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_CHANDAAM, 0);

        Log.d(TAG, "call to execute - thread: " + Thread.currentThread().getId());
        task.execute(values);

    }

    private void simulateLongRunningWork() {
        try {
            Thread.sleep(3000);
        } catch(Exception ex) {}
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
            Log.d(TAG, "delete payment selected");
            deletePaymentFromDatabase();
            finish(); // Exit the Activity
        } else if(id == R.id.action_cancel){
            mIsCancelling = true;
            finish(); // Exit the Activity
        } else if(id == R.id.action_save) {
            if (validatePaymentInputs()){
                mIsSaving = true;
                savePayment();
                Toast.makeText(this, "Payment saved", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                return false;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validatePaymentInputs() {
        View view = findViewById(R.id.payment_scrollview);
        if(mTextReceiptNo.getText().toString().isEmpty()){
            Toast.makeText(this, "Receipt Number field cannot be empty!", Toast.LENGTH_LONG).show();
            mTextReceiptNo.requestFocus();
            return false;
        } else {
            String receiptNo = mTextReceiptNo.getText().toString().trim();
            Log.d(TAG, "Checking for duplicate receipt number");
            Cursor cursor = checkForDuplicatereceiptNo(receiptNo);

            int receiptNoPos = cursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_LOCALRECEIPT);
            int payerNamePos = cursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_MEMBER_FULLNAME);
            int monthPaidPos = cursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_MONTHPAID);
            int chandaPos = cursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_MEMBER_CHANDANO);
            boolean more = cursor.moveToFirst();
            ArrayList<String[]> receiptNumbers = new ArrayList(cursor.getCount());

            while (more){
                String receiptNumber = cursor.getString(receiptNoPos);
                String payerName = cursor.getString(payerNamePos);
                String monthPaid = cursor.getString(monthPaidPos);
                int chandaNo = cursor.getInt(chandaPos);
                receiptNumbers.add(new String[]{receiptNumber,payerName,monthPaid,String.valueOf(chandaNo)});
                more = cursor.moveToNext();
            }
            cursor.close();
            for(String[] details:receiptNumbers){
                if(Integer.valueOf(details[0]) == Integer.valueOf(receiptNo) && mChandaNo != Integer.valueOf(details[3])){
                    // get the jamaatName of payer with chandaNo
                    String jamaatName = getPayerJamaat(Integer.valueOf(details[3]));
                    Snackbar.make(view, "Receipt Number: " +
                            details[0] + " already entered for " +
                            details[1].toUpperCase() + " at " +
                            jamaatName + " Jamaat for " + details[2], Snackbar.LENGTH_LONG).show();
                    mTextReceiptNo.requestFocus();
                    return false;
                }
            }

        }

        return true;
    }

    private String getPayerJamaat(int chandaNo) {
        Uri uri = Members.CONTENT_URI;
        String[] projection = {Members.COLUMN_MEMBER_CHANDANO, Members.COLUMN_MEMBER_JAMAATNAME};
        String selection = Members.COLUMN_MEMBER_CHANDANO + "=?";
        String[] selectionArgs = { Integer.toString(chandaNo) };
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs,null);

        if(cursor.moveToFirst()){
            int jamaatNamePos = cursor.getColumnIndex(Members.COLUMN_MEMBER_JAMAATNAME);
            String jamaatName = cursor.getString(jamaatNamePos);
            return jamaatName;
        }
        return null;
    }

    private Cursor checkForDuplicatereceiptNo(String receiptNo) {
        Uri uri = PaymentProviderContract.Payments.CONTENT_URI;
        String[] projection = {
                PaymentProviderContract.Payments.COLUMN_MEMBER_CHANDANO,
                PaymentProviderContract.Payments.COLUMN_MEMBER_FULLNAME,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_MONTHPAID,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_LOCALRECEIPT,

        };
        int integerReceipt = Integer.valueOf(receiptNo);
        String selection = PaymentProviderContract.Payments.COLUMN_PAYMENT_LOCALRECEIPT + "= ? OR " +
                PaymentProviderContract.Payments.COLUMN_PAYMENT_LOCALRECEIPT + " LIKE ?";
        String[] selectionArgs = { receiptNo, "%" +integerReceipt };
        Cursor cursor = getContentResolver().query(uri,projection,selection,selectionArgs,null);

        return cursor;

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "**********onPause**********");
        super.onPause();
        if(mIsCancelling){
            // check if we're cancelling on newpayment otherwise do nothing
            if(mIsNewPayment){
//                DataManager.getInstance().removePayment(mPaymentId);
                Log.d(TAG, "Cancelling newly created payment");
                deletePaymentFromDatabase();
            } else {
                Log.d(TAG, "Existing editing of payment "+ mViewModel.mPaymentUri);
//                storePreviousPaymentValues();
            }
        } else if(!mIsSaving && mIsNewPayment) {
            Log.d(TAG, "user cancelling creation of new payment clicking back button");
            deletePaymentFromDatabase(); // delete if payment is new and user is not saving
        }
    }

    private void deletePaymentFromDatabase() {
        Log.i(TAG, "Deleting payment: "+ mViewModel.mPaymentUri);
        getContentResolver().delete(mViewModel.mPaymentUri, null, null);

    }

    private void storePreviousPaymentValues() {
        ScheduleInfo schedule = DataManager.getInstance().getSchedule(mOriginalScheduleId);
        mViewModel.mPayment.setSchedule(schedule);
        mViewModel.mPayment.setChandaNo(mOriginalChandaNo);
        mViewModel.mPayment.setMonthPaid(mOriginalMonthPaid);
        mViewModel.mPayment.setReceiptNo(mOriginalPaymentReceiptNo);
        mViewModel.mPayment.setChandaAm(mOriginalPaymentChandaAm);
        mViewModel.mPayment.setWasiyyat(mOriginalPaymentWasiyyat);
        mViewModel.mPayment.setTahrikJadid(mOriginalPaymentTahrikJadid);
        mViewModel.mPayment.setWaqfJadid(mOriginalPaymentWaqfJadid);
    }

    private void savePayment() {
        int chandaNo = selectedChandaNo();
        String fullname = getNameOfPayer(chandaNo);
        String monthPaid = String.valueOf(mSpinnerMonthPaid.getSelectedItemsString());
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
        Log.d(TAG, "Preparing to save input data for " + mViewModel.mPaymentUri);

        savePaymentToDatabase(chandaNo,fullname,mViewModel.mScheduleId,monthPaid,receiptNo,chandaAm,wasiyyat,
                tahrikiJadid,waqfJadid,welfare,jalsa,tabligh,scholarship,maryam,zakat,fitrana,
                mosqueDonation,mta,centinary,wasiyyatHissan,sadakat,miscellaneous,subtotal);
    }

    private void savePaymentToDatabase(int chandaNo, String fullname, String schedule, String monthPaid,
                                       String receiptNo, double chandaAm, double wasiyyat, double tahrikiJadid,
                                       double waqfJadid, double welfare, double jalsa, double tabligh,
                                       double scholarship, double maryam, double zakat, double fitrana,
                                       double mosqueDonation, double mta, double centinary, double wasiyyatHissan,
                                       double sadakat, double miscellaneous, double subtotal){

        final ContentValues values = new ContentValues();
        values.put(PaymentProviderContract.Payments.COLUMN_MEMBER_CHANDANO, chandaNo);
        values.put(PaymentProviderContract.Payments.COLUMN_MEMBER_FULLNAME, fullname);
        values.put(PaymentProviderContract.Payments.COLUMN_SCHEDULE_ID, schedule);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_MONTHPAID, monthPaid);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_LOCALRECEIPT, receiptNo);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_CHANDAAM, chandaAm);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_CHANDAWASIYYAT, wasiyyat);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_TARIKIJADID, tahrikiJadid);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_WAQFIJADID, waqfJadid);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_WELFAREFUND, welfare);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_JALSASALANA, jalsa);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_TABLIGH, tabligh);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_SCHOLARSHIP, scholarship);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_MARYAMFUND, maryam);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_ZAKAT, zakat);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_FITRANA, fitrana);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_MOSQUEDONATION, mosqueDonation);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_MTA, mta);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_CENTINARYKHILAFAT, centinary);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_WASIYYATHISSANJAIDAD, wasiyyatHissan);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_SADAKAT, sadakat);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_MISCELLANEOUS, miscellaneous);
        values.put(PaymentProviderContract.Payments.COLUMN_PAYMENT_SUBTOTAL, subtotal);
        // get connection to the content provider
        Log.i(TAG, "Saving " + mViewModel.mPaymentUri + " to the database");
        getContentResolver().update(mViewModel.mPaymentUri, values, null, null);
        if(mViewModel.mScheduleStatus == 1){
            changeScheduleToDraft();
        }

    }

    private void changeScheduleToDraft() {
        mViewModel.mSchedule.setComplete(false);
        long idSchedule = mViewModel.mSchedule.getId();
        ContentValues values2 = new ContentValues();
        values2.put(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ISCOMPLETE, 0);
        Uri scheduleUri = ContentUris.withAppendedId(PaymentProviderContract.Schedules.CONTENT_URI,
                idSchedule);
        Log.i(TAG, "Return schedule status back to 'Draft ");
        getContentResolver().update(scheduleUri, values2, null, null);
    }

    private int selectedChandaNo() {
        int selectedPosition = mSpinnerChandaNo.getSelectedItemPosition();
        Cursor cursor = mAdapterMemberIds.getCursor();
        cursor.moveToPosition(selectedPosition);
        int chandaNoPos = cursor.getColumnIndex(PaymentProviderContract.Members.COLUMN_MEMBER_ID);
        return cursor.getInt(chandaNoPos);
    }

    private String getNameOfPayer(int chandaNo) {
        Uri uri = Members.CONTENT_URI;
        String[] projection = {
                Members.COLUMN_MEMBER_CHANDANO,
                Members.COLUMN_MEMBER_ID,
                Members.COLUMN_MEMBER_JAMAATNAME,
                Members.COLUMN_MEMBER_FULLNAME
        };
        String selection = Members.COLUMN_MEMBER_CHANDANO + "=?";
        String[] selectionArgs = { Integer.toString(chandaNo)};
        Cursor cursor = getContentResolver().query(uri,projection,selection,selectionArgs,null);
        // get the index column for Fullname
        int fullnamePos = cursor.getColumnIndex(Members.COLUMN_MEMBER_FULLNAME);
        // move to first row of table
        cursor.moveToNext();
        String fullname = cursor.getString(fullnamePos);
        return fullname;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_PAYMENTS) {
            Log.d(TAG, "Loading payments");
            loader = createLoaderPayments();
        }
        else if(id == LOADER_MEMBERS) {
            Log.d(TAG, "Loading members");
            loader = createLoaderMembers();
        }
        return loader;
    }

    private CursorLoader createLoaderMembers() {
        mMembersQueriesFinished = false;
        Uri uri = Members.CONTENT_URI;
        String[] selectionColumns = {
                Members._ID,
                Members.COLUMN_MEMBER_ID,
                Members.COLUMN_MEMBER_CHANDANO,
                Members.COLUMN_MEMBER_FULLNAME,
                Members.COLUMN_MEMBER_JAMAATNAME,
        };
        String selection = Members.COLUMN_MEMBER_JAMAATNAME + "=?";
        String[] selectionArgs = { mViewModel.mSchedule.getJamaat()};

        return new CursorLoader(this, uri, selectionColumns, selection, selectionArgs, Members.COLUMN_MEMBER_FULLNAME);
    }

    private CursorLoader createLoaderPayments() {
        mPaymentQueriesFinished = false;
        mViewModel.mPaymentUri = ContentUris.withAppendedId(PaymentProviderContract.Payments.CONTENT_URI, mViewModel.mPaymentId);
        String[] paymentColumns = {
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
                PaymentProviderContract.Payments.COLUMN_PAYMENT_MARYAMFUND,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_TABLIGH,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_ZAKAT,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_SADAKAT,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_FITRANA,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_MOSQUEDONATION,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_MTA,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_CENTINARYKHILAFAT,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_WASIYYATHISSANJAIDAD,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_MISCELLANEOUS,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_SUBTOTAL
        };
        return new CursorLoader(this, mViewModel.mPaymentUri, paymentColumns, null, null, null);

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
        mChandaNoPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_MEMBER_CHANDANO);
        mFullnamePos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_MEMBER_FULLNAME);
        mLocalReceiptPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_LOCALRECEIPT);
        mMonthPaidPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_MONTHPAID);
        mChandaPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_CHANDAAM);
        mWasiyyatPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_CHANDAWASIYYAT);
        mJalsaPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_JALSASALANA);
        mTarikiPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_TARIKIJADID);
        mWaqfPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_WAQFIJADID);
        mWelfarePos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_WELFAREFUND);
        mScholarshipPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_SCHOLARSHIP);
        mMaryamPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_MARYAMFUND);
        mTablighPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_TABLIGH);
        mZakatPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_ZAKAT);
        mSadakatPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_SADAKAT);
        mFitranaPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_FITRANA);
        mMosqueDonationPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_MOSQUEDONATION);
        mMtaPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_MTA);
        mCentinaryPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_CENTINARYKHILAFAT);
        mWasiyyatHissanPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_WASIYYATHISSANJAIDAD);
        mMiscellaneousPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_MISCELLANEOUS);
        mSubtotalPos = mPaymentCursor.getColumnIndex(PaymentProviderContract.Payments.COLUMN_PAYMENT_SUBTOTAL);

        mPaymentCursor.moveToFirst();
        mPaymentQueriesFinished = true; // payment queries finished loading
        displayPaymentWhenQueriesFinished();

    }

    private void displayPaymentWhenQueriesFinished() {
        if(mMembersQueriesFinished && mPaymentQueriesFinished) {
            Log.d(TAG, "Finished loading payments and members");
            displayPayments();
        }
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

    @Override
    public void onItemsSelected(boolean[] selected) {

    }
}
