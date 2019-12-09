package ng.com.dayma.paymentdummy.MyViewModels;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;

import ng.com.dayma.paymentdummy.DataManager;
import ng.com.dayma.paymentdummy.PaymentInfo;
import ng.com.dayma.paymentdummy.ScheduleInfo;

public class PaymentActivityViewModel extends ViewModel {

    public boolean isNewlyCreated = true;

    public static final String ORIGINAL_PAYMENT_WELFARE = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_WELFARE";
    public static final String ORIGINAL_PAYMENT_SCHOLARSHIP = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_SCHOLARSHIP";
    public static final String ORIGINAL_PAYMENT_TABLIGH = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_TABLIGH";
    public static final String ORIGINAL_PAYMENT_MTA = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_MTA";
    public static final String ORIGINAL_PAYMENT_MARYAM = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_MARYAM";
    public static final String ORIGINAL_PAYMENT_MOSQUEDONATION = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_MOSQUEDONATION";
    public static final String ORIGINAL_PAYMENT_SADAQA = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_SADAQA";
    public static final String ORIGINAL_PAYMENT_JALSASALANA = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_JALSASALANA";
    public static final String ORIGINAL_PAYMENT_ZAKAT = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_ZAKAT";
    public static final String ORIGINAL_PAYMENT_FITRANA = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_FITRANA";
    public static final String ORIGINAL_PAYMENT_FULLNAME = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_FULLNAME";
    public static final String ORIGINAL_PAYMENT_MISCELLANEOUS = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_MISCELLANEOUS";
    public static final String ORIGINAL_PAYMENT_CENTINARY = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_CENTINARY";
    public static final String ORIGINAL_PAYMENT_WASIYYATHISSAN = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_WASIIYYATHISSAN";
    public static final String ORIGINAL_PAYMENT_ID = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_ID";
    public static final String ORIGINAL_MONTH_PAID = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_MONTH_PAID";
    public static final String ORIGINAL_PAYMENT_RECEIPT_NO = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_RECEIPT_NO";
    public static final String ORIGINAL_PAYMENT_CHANDAAM = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_CHANDAAM";
    public static final String ORIGINAL_PAYMENT_WASIYYAT = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_WASIYYAT";
    public static final String ORIGINAL_PAYMENT_TAHRIKJADID = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_TAHRIKJADID";
    public static final String ORIGINAL_PAYMENT_WAQFJADID = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_PAYMENT_WAQFJADID";
    public static final String ORIGINAL_SCHEDULE_ID = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.ORIGINAL_SCHEDULE_ID";
    public static final String PAYMENT_URI = "ng.com.dayma.paymentdummy.PaymentActivityViewModel.PAYMENT_URI";

    public ArrayList<String> monthsYear = DataManager.getInstance().getMonthsWithYear();
    public String mScheduleId;
    public Uri mPaymentUri;
    public int mPaymentId;
    public ScheduleInfo mSchedule;
    public PaymentInfo mPayment;
    private int mOriginalChandaNo;
    private String mOriginalMonthPaid;
    private String mOriginalPaymentReceiptNo;
    private float mOriginalPaymentChandaAm;
    private float mOriginalPaymentWasiyyat;
    private float mOriginalPaymentTahrikJadid;
    private float mOriginalPaymentWaqfJadid;
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
    private String mOriginalScheduleId;

    public void saveOriginalPaymentValues() {
        if(isNewlyCreated){
            mPayment = new PaymentInfo(DataManager.getInstance().getSchedule(mScheduleId),-1,null,null,null);
        }
        mOriginalChandaNo = mPayment.getChandaNo();
        mOriginalMonthPaid = mPayment.getMonthPaid();
        mOriginalPaymentReceiptNo = mPayment.getReceiptNo();
        mOriginalPaymentChandaAm = mPayment.getChandaAm();
        mOriginalPaymentWasiyyat = mPayment.getWasiyyat();
        mOriginalPaymentTahrikJadid = mPayment.getTahrikJadid();
        mOriginalPaymentWaqfJadid = mPayment.getWaqfJadid();
        mOriginalPaymentWelfare = mPayment.getWelfare();
        mOriginalPaymentScholarship = mPayment.getScholarship();
        mOriginalPaymentTabligh = mPayment.getTabligh();
        mOriginalPaymentMta = mPayment.getMta();
        mOriginalPaymentMaryam = mPayment.getMaryam();
        mOriginalPaymentMosqueDonation = mPayment.getMosqueDonation();
        mOriginalPaymentSadaqa = mPayment.getSadakat();
        mOriginalPaymentZakat = mPayment.getZakat();
        mOriginalPaymentFitrana = mPayment.getFitrana();
        mOriginalPaymentFullname = mPayment.getFullname();
        mOriginalPaymentMiscellaneous = mPayment.getMiscellaneous();
        mOriginalPaymentCentinary = mPayment.getCentinary();
        mOriginalPaymentWasiyyatHissan = mPayment.getWasiyyatHissan();
        mOriginalPaymentJalsaSalana = mPayment.getJalsaSalana();
//        mPayment.setSchedule(mSchedule);
        mOriginalScheduleId = mScheduleId;
    }

    public void saveState(Bundle outState) {
        outState.putString(ORIGINAL_PAYMENT_ID, String.valueOf(mOriginalChandaNo));
        outState.putString(ORIGINAL_SCHEDULE_ID, mOriginalScheduleId);
        outState.putString(ORIGINAL_MONTH_PAID, mOriginalMonthPaid);
        outState.putString(ORIGINAL_PAYMENT_RECEIPT_NO, String.valueOf(mOriginalPaymentReceiptNo));
        outState.putString(ORIGINAL_PAYMENT_CHANDAAM, String.valueOf(mOriginalPaymentChandaAm));
        outState.putString(ORIGINAL_PAYMENT_WASIYYAT, String.valueOf(mOriginalPaymentWasiyyat));
        outState.putString(ORIGINAL_PAYMENT_TAHRIKJADID, String.valueOf(mOriginalPaymentTahrikJadid));
        outState.putString(ORIGINAL_PAYMENT_WAQFJADID, String.valueOf(mOriginalPaymentWaqfJadid));
        outState.putString(ORIGINAL_PAYMENT_WELFARE, String.valueOf(mOriginalPaymentWelfare));
        outState.putString(ORIGINAL_PAYMENT_SCHOLARSHIP, String.valueOf(mOriginalPaymentScholarship));
        outState.putString(ORIGINAL_PAYMENT_TABLIGH, String.valueOf(mOriginalPaymentTabligh));
        outState.putString(ORIGINAL_PAYMENT_MTA, String.valueOf(mOriginalPaymentMta));
        outState.putString(ORIGINAL_PAYMENT_MARYAM, String.valueOf(mOriginalPaymentMaryam));
        outState.putString(ORIGINAL_PAYMENT_MOSQUEDONATION, String.valueOf(mOriginalPaymentMosqueDonation));
        outState.putString(ORIGINAL_PAYMENT_SADAQA, String.valueOf(mOriginalPaymentSadaqa));
        outState.putString(ORIGINAL_PAYMENT_JALSASALANA, String.valueOf(mOriginalPaymentJalsaSalana));
        outState.putString(ORIGINAL_PAYMENT_ZAKAT, String.valueOf(mOriginalPaymentZakat));
        outState.putString(ORIGINAL_PAYMENT_FITRANA, String.valueOf(mOriginalPaymentFitrana));
        outState.putString(ORIGINAL_PAYMENT_FULLNAME, String.valueOf(mOriginalPaymentFullname));
        outState.putString(ORIGINAL_PAYMENT_MISCELLANEOUS, String.valueOf(mOriginalPaymentMiscellaneous));
        outState.putString(ORIGINAL_PAYMENT_CENTINARY, String.valueOf(mOriginalPaymentCentinary));
        outState.putString(ORIGINAL_PAYMENT_WASIYYATHISSAN, String.valueOf(mOriginalPaymentWasiyyatHissan));

        outState.putString(PAYMENT_URI, mPaymentUri.toString());
    }

    public void restoreOriginalPaymentValues(Bundle savedInstanceState) {

        mOriginalChandaNo = Integer.parseInt(savedInstanceState.getString(ORIGINAL_PAYMENT_ID));
        mOriginalScheduleId = savedInstanceState.getString(ORIGINAL_SCHEDULE_ID);
        mOriginalMonthPaid = savedInstanceState.getString(ORIGINAL_MONTH_PAID);
        mOriginalPaymentReceiptNo = savedInstanceState.getString(ORIGINAL_PAYMENT_RECEIPT_NO);
        mOriginalPaymentChandaAm = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_CHANDAAM));
        mOriginalPaymentWasiyyat = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_WASIYYAT));
        mOriginalPaymentTahrikJadid = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_TAHRIKJADID));
        mOriginalPaymentWaqfJadid = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_WAQFJADID));
        mOriginalPaymentWelfare = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_WELFARE));
        mOriginalPaymentScholarship = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_SCHOLARSHIP));
        mOriginalPaymentTabligh = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_TABLIGH));
        mOriginalPaymentFitrana = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_FITRANA));
        mOriginalPaymentFullname = (savedInstanceState.getString(ORIGINAL_PAYMENT_FULLNAME));
        mOriginalPaymentJalsaSalana = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_JALSASALANA));
        mOriginalPaymentCentinary = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_CENTINARY));
        mOriginalPaymentMaryam = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_MARYAM));
        mOriginalPaymentMiscellaneous = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_MISCELLANEOUS));
        mOriginalPaymentMosqueDonation = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_MOSQUEDONATION));
        mOriginalPaymentMta = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_MTA));
        mOriginalPaymentSadaqa = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_SADAQA));
        mOriginalPaymentZakat = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_ZAKAT));
        mOriginalPaymentWasiyyatHissan = Float.parseFloat(savedInstanceState.getString(ORIGINAL_PAYMENT_WASIYYATHISSAN));
        String StringPaymentUri = savedInstanceState.getString(PAYMENT_URI);
        mPaymentUri = Uri.parse(StringPaymentUri);
    }
}
