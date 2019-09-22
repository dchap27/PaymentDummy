package ng.com.dayma.paymentdummy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    public static final String PAYMENT_POSITION = "ng.com.dayma.paymentdummy.PAYMENT_POSITION";
    public static final String MONTH_POSITION = "ng.com.dayma.paymentdummy.MONTH_POSITION";
    public static final int POSITION_NOT_SET = -1;
    public static final String SCHEDULE_INFO = "ng.com.dayma.paymentdummy.SCHEDULE_INFO";

    public static final String ORIGINAL_PAYMENT_ID = "ng.com.dayma.paymentdummy.ORIGINAL_PAYMENT_ID";
    public static final String ORIGINAL_MONTH_PAID = "ng.com.dayma.paymentdummy.ORIGINAL_MONTH_PAID";
    public static final String ORIGINAL_PAYMENT_RECEIPT_NO = "ng.com.dayma.paymentdummy.ORIGINAL_PAYMENT_RECEIPT_NO";
    public static final String ORIGINAL_PAYMENT_CHANDAAM = "ng.com.dayma.paymentdummy.ORIGINAL_PAYMENT_CHANDAAM";
    public static final String ORIGINAL_PAYMENT_WASIYYAT = "ng.com.dayma.paymentdummy.ORIGINAL_PAYMENT_WASIYYAT";
    public static final String ORIGINAL_PAYMENT_TAHRIKJADID = "ng.com.dayma.paymentdummy.ORIGINAL_PAYMENT_TAHRIKJADID";
    public static final String ORIGINAL_PAYMENT_WAQFJADID = "ng.com.dayma.paymentdummy.ORIGINAL_PAYMENT_WAQFJADID";
    public static final String ORIGINAL_SCHEDULE_ID = "ng.com.dayma.paymentdummy.ORIGINAL_SCHEDULE_ID";

    private Spinner mSpinnerMonthPaid;
    private PaymentInfo mPayment;
    private boolean mIsNewPayment;
    private EditText mTextReceiptNo;
    private EditText mTextChandaAm;
    private EditText mTextWasiyyat;
    private EditText mTextTahrikJadid;
    private EditText mTextWaqfJadid;
    private int mPaymentPosition;
    private boolean mIsCancelling;
    private MonthInfo mMonth;
    private ScheduleInfo mSchedule;
    private Spinner mSpinnerPaymentId;
    private int mOriginalChandaNo;
    private String mOriginalMonthPaid;
    private String mOriginalPaymentReceiptNo;
    private float mOriginalPaymentChandaAm;
    private float mOriginalPaymentWasiyyat;
    private float mOriginalPaymentTahrikJadid;
    private float mOriginalPaymentWaqfJadid;
    private String mOriginalScheduleId;
    private TextView mTextPaymentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpinnerMonthPaid = (Spinner) findViewById(R.id.spinner_monthpaid);
        mSpinnerPaymentId = (Spinner) findViewById(R.id.spinner_payment_text);

        List<String> monthsYear = DataManager.getInstance().getMonthsOfTheYear();
        List<String> paymentIds = DataManager.getInstance().getPaymentIds();
        ArrayAdapter<String> adapterMonths =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthsYear);
        adapterMonths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapterPaymentIds =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentIds);
        adapterPaymentIds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerMonthPaid.setAdapter(adapterMonths);
        mSpinnerPaymentId.setAdapter(adapterPaymentIds);


        readDisplayStateValue();
        if(savedInstanceState == null) {
            saveOriginalPaymentValues(); // when first created
        } else {
            restoreOriginalPaymentValues(savedInstanceState); // restore the values when activity is recreated
        }

        // get references to the views
        mTextPaymentId = (TextView) findViewById(R.id.text_payer_title);
        mTextReceiptNo = (EditText) findViewById(R.id.text_receiptno);
        mTextChandaAm = (EditText) findViewById(R.id.text_chandaam);
        mTextWasiyyat = (EditText) findViewById(R.id.text_wasiyyat);
        mTextTahrikJadid = (EditText) findViewById(R.id.text_tahrik);
        mTextWaqfJadid = (EditText) findViewById(R.id.text_waqf);

        if(!mIsNewPayment)
            // populate the views with values if not a new payment
            displayPayments(mSpinnerMonthPaid, mSpinnerPaymentId,
                    mTextReceiptNo, mTextChandaAm, mTextWasiyyat, mTextTahrikJadid, mTextWaqfJadid);
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

    private void displayPayments(Spinner spinnerMonthPaid, Spinner spinnerPaymentId,
                                 EditText textReceipt, EditText textChanda, EditText textWasiyyatPaid,
                                 EditText textTahrik, EditText textWaqf) {
        List<String> monthsYear = DataManager.getInstance().getMonthsOfTheYear();
        List<String> paymentIds = DataManager.getInstance().getPaymentIds();
        int paymentIdIndex = paymentIds.indexOf(mPayment.getChandaNo());
        spinnerPaymentId.setSelection(paymentIdIndex);
        if(!mIsNewPayment){
            mTextPaymentId.setVisibility(View.VISIBLE); // show textview if not a new payment
            spinnerPaymentId.setVisibility(View.INVISIBLE); // hide spinner
        } else {
            mTextPaymentId.setVisibility(View.INVISIBLE); // show textview if not a new payment
            spinnerPaymentId.setVisibility(View.VISIBLE); // hide spinner
        }
        int monthIndex = monthsYear.indexOf(mPayment.getMonthPaid());
        spinnerMonthPaid.setSelection(monthIndex);

        mTextPaymentId.setText(mPayment.getChandaNo());

        textReceipt.setText(String.valueOf(mPayment.getReceiptNo()));
        textChanda.setText(String.valueOf(mPayment.getChandaAm()));
        textWasiyyatPaid.setText(String.valueOf(mPayment.getWasiyyat()));
        textTahrik.setText(String.valueOf(mPayment.getTahrikJadid()));
        textWaqf.setText(String.valueOf(mPayment.getWaqfJadid()));
    }

    private void readDisplayStateValue() {
        Intent intent = getIntent();
        //get value that was put into the intent
        String scheduleId = intent.getStringExtra(SCHEDULE_INFO);
        mSchedule = DataManager.getInstance().getSchedule(scheduleId);
        mPaymentPosition = intent.getIntExtra(PAYMENT_POSITION, POSITION_NOT_SET);
        mIsNewPayment = mPaymentPosition == POSITION_NOT_SET;
        if(mIsNewPayment){
            createNewPayment();
        }else {
            mPayment = DataManager.getInstance().getPayments(mSchedule).get(mPaymentPosition);
        }

    }

    private void createNewPayment() {
        DataManager dm = DataManager.getInstance();
        mPaymentPosition = dm.createNewPayment(); // create a new payment and return its position
        mPayment = DataManager.getInstance().getPayments().get(mPaymentPosition);
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
        int lastPaymentIndex = DataManager.getInstance().getPayments(mSchedule).size() - 1;
        item1.setEnabled(mPaymentPosition < lastPaymentIndex);
        item2.setEnabled(mPaymentPosition > 0); // if position is on index greater than startindex 0
        if(mIsNewPayment){
            item1.setEnabled(false); // disable next menu
        }
        item3.setEnabled(mIsNewPayment); // enable the button if NewPayment
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_email) {
            sendEmail();
            return true;
        } else if(id == R.id.action_cancel){
            mIsCancelling = true;
            finish();
        } else if(id == R.id.action_next){
            moveNext();
        } else if(id == R.id.action_previous){
            movePrevious();
        } else if(id == R.id.action_save) {
            boolean isSaving = true;
            onSavingPayment();
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSavingPayment() {
        savePayment();
        createNewPayment();
        mIsNewPayment = true;

        // display new payments options
        displayPayments(mSpinnerMonthPaid, mSpinnerPaymentId,
                mTextReceiptNo, mTextChandaAm, mTextWasiyyat, mTextTahrikJadid, mTextWaqfJadid);
        invalidateOptionsMenu(); // to enable calling of onPrepareOptionsMenu again and check for
        // possible conditions to re-enable the menu item
    }

    private void movePrevious() {
        savePayment();
        --mPaymentPosition;
        mPayment = DataManager.getInstance().getPayments(mSchedule).get(mPaymentPosition);

        saveOriginalPaymentValues();
        displayPayments(mSpinnerMonthPaid, mSpinnerPaymentId,
                mTextReceiptNo, mTextChandaAm, mTextWasiyyat, mTextTahrikJadid, mTextWaqfJadid);
        invalidateOptionsMenu(); // to enable calling of onPrepareOptionsMenu again and check for
        // possible conditions to re-enable the menu item
    }

    private void moveNext() {
        savePayment(); // save the present Note we're looking at
        ++mPaymentPosition;
        mPayment = DataManager.getInstance().getPayments(mSchedule).get(mPaymentPosition);

        saveOriginalPaymentValues(); // save the next note displayed before any editing
        displayPayments(mSpinnerMonthPaid, mSpinnerPaymentId,
                mTextReceiptNo, mTextChandaAm, mTextWasiyyat, mTextTahrikJadid, mTextWaqfJadid);
        invalidateOptionsMenu(); // to enable calling of onPrepareOptionsMenu again and check for
        // possible conditions to re-enable the menu item
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling){
            // check if we're cancelling on newpayment otherwise do nothing
            if(mIsNewPayment){
                DataManager.getInstance().removePayment(mPaymentPosition);
            } else {
                storePreviousPaymentValues();
            }
        } else {
            savePayment();
        }
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

        mTextReceiptNo.getText();
        mTextReceiptNo.getText().toString();
        Integer.parseInt(mTextReceiptNo.getText().toString());
        mPayment.setSchedule(mSchedule);
        mPayment.setChandaNo(Integer.parseInt((String) mSpinnerPaymentId.getSelectedItem()));
        mPayment.setMonthPaid((String) mSpinnerMonthPaid.getSelectedItem());
        // parse the editText to String
        mPayment.setReceiptNo(mTextReceiptNo.getText().toString());
        // convert the String to float
        mPayment.setChandaAm(Float.valueOf(mTextChandaAm.getText().toString()));
        mPayment.setWasiyyat(Float.valueOf(mTextWasiyyat.getText().toString()));
        mPayment.setTahrikJadid(Float.valueOf(mTextTahrikJadid.getText().toString()));
        mPayment.setWaqfJadid(Float.valueOf(mTextWaqfJadid.getText().toString()));
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
}
