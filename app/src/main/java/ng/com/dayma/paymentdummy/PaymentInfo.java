package ng.com.dayma.paymentdummy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmad on 7/12/2019.
 */

public final class PaymentInfo {
    private String mFullname;
    private String mLocalReceipt;
    private float mJalsaSalana;
    private float mWelfare;
    private float mScholarship;
    private float mSubtotal;
    private float mMiscellaneous;
    private float mWasiyyatHissan;
    private float mCentinary;
    private float mMaryam;
    private float mTabligh;
    private float mZakat;
    private float mSadakat;
    private float mFitrana;
    private float mMosqueDonation;
    private float mMta;
    private int mChandaNo;
    private ScheduleInfo mSchedule;
    private String mMonthPaid;
    private float mChandaAm;
    private float mWasiyyat;
    private float mTahrikJadid;
    private float mWaqfJadid;
    private long mId;

    public PaymentInfo(ScheduleInfo schedule, int chandaNo, String fullname, String localReceipt, String monthPaid,
                       float chandaAm, float wasiyyat, float jalsaSalana, float tarikiJadid,
                       float waqfJadid, float welfare, float scholarship, float maryam,
                       float tabligh, float zakat, float sadakat, float fitrana,
                       float mosqueDonation, float mta, float centinary, float wasiyyatHissan,
                       float miscellaneous, float subtotal, long id) {
        mId = id;
        mChandaNo = chandaNo;
        setFullname(fullname);
        mMonthPaid = monthPaid;
        setLocalReceipt(localReceipt);
        mChandaAm = chandaAm;
        mWasiyyat = wasiyyat;
        mJalsaSalana = jalsaSalana;
        mTahrikJadid = tarikiJadid;
        mWaqfJadid = waqfJadid;
        mWelfare = welfare;
        mScholarship = scholarship;
        mMaryam = maryam;
        mTabligh = tabligh;
        mZakat = zakat;
        mSadakat = sadakat;
        mFitrana = fitrana;
        mMosqueDonation = mosqueDonation;
        mMta = mta;
        mCentinary = centinary;
        mWasiyyatHissan = wasiyyatHissan;
        mMiscellaneous = miscellaneous;
        setSubtotal(subtotal);
        mSchedule = schedule;

    }

    public PaymentInfo(ScheduleInfo schedule, int chandaNo, String fullname, String localReceipt, String monthPaid) {
        this(null,chandaNo,null,localReceipt,monthPaid, 0.0f, 0.0f, 0.0f,0.0f,
                0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,
                0.0f,0.0f,0.0f,-1);
    }

    public long getId() { return mId; }

    public int getChandaNo() {
        return mChandaNo;
    }

    public String getFullname() {
        return mFullname;
    }

    public void setChandaNo(int chandaNo) {
        mChandaNo = chandaNo;
    }

    public ScheduleInfo getSchedule() {
        return mSchedule;
    }

    public void setSchedule(ScheduleInfo schedule) {
        mSchedule = schedule;
    }

    public String getReceiptNo() {
        return getLocalReceipt();
    }

    public void setReceiptNo(String receiptNo) {
        setLocalReceipt(receiptNo);
    }

    public String getMonthPaid() {
        return mMonthPaid;
    }

    public void setMonthPaid(String monthPaid) {
        mMonthPaid = monthPaid;
    }

    public float getChandaAm() {
        return mChandaAm;
    }

    public void setChandaAm(float chandaAm) {
        mChandaAm = chandaAm;
    }

    public float getWasiyyat() {
        return mWasiyyat;
    }

    public void setWasiyyat(float wasiyyat) {
        mWasiyyat = wasiyyat;
    }

    public float getTahrikJadid() {
        return mTahrikJadid;
    }

    public void setTahrikJadid(float tahrikJadid) {
        mTahrikJadid = tahrikJadid;
    }

    public float getWaqfJadid() {
        return mWaqfJadid;
    }

    public void setWaqfJadid(float waqfJadid) {
        mWaqfJadid = waqfJadid;
    }

    public float getSubtotal(){ return mSubtotal; }

    public void setSubTotal() {
        setSubtotal(mChandaAm + mWasiyyat + mJalsaSalana + mTahrikJadid + mWaqfJadid + mWelfare +
                mScholarship + mTabligh + mMaryam + mCentinary + mMta + mWasiyyatHissan +
                mMosqueDonation + mFitrana + mSadakat + mZakat + mMiscellaneous);
    }
    private String getCompareKey() {
        return mChandaNo + "|" + mMonthPaid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentInfo that = (PaymentInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    public void setFullname(String fullname) {
        mFullname = fullname;
    }

    public String getLocalReceipt() {
        return mLocalReceipt;
    }

    public void setLocalReceipt(String localReceipt) {
        mLocalReceipt = localReceipt;
    }

    public void setSubtotal(float subtotal) {
        mSubtotal = subtotal;
    }

    public float getJalsaSalana() {
        return mJalsaSalana;
    }

    public void setJalsaSalana(float jalsaSalana) {
        mJalsaSalana = jalsaSalana;
    }

    public float getWelfare() {
        return mWelfare;
    }

    public void setWelfare(float welfare) {
        mWelfare = welfare;
    }

    public float getScholarship() {
        return mScholarship;
    }

    public void setScholarship(float scholarship) {
        mScholarship = scholarship;
    }

    public float getMiscellaneous() {
        return mMiscellaneous;
    }

    public void setMiscellaneous(float miscellaneous) {
        mMiscellaneous = miscellaneous;
    }

    public float getWasiyyatHissan() {
        return mWasiyyatHissan;
    }

    public void setWasiyyatHissan(float wasiyyatHissan) {
        mWasiyyatHissan = wasiyyatHissan;
    }

    public float getCentinary() {
        return mCentinary;
    }

    public void setCentinary(float centinary) {
        mCentinary = centinary;
    }

    public float getMaryam() {
        return mMaryam;
    }

    public void setMaryam(float maryam) {
        mMaryam = maryam;
    }

    public float getTabligh() {
        return mTabligh;
    }

    public void setTabligh(float tabligh) {
        mTabligh = tabligh;
    }

    public float getZakat() {
        return mZakat;
    }

    public void setZakat(float zakat) {
        mZakat = zakat;
    }

    public float getSadakat() {
        return mSadakat;
    }

    public void setSadakat(float sadakat) {
        mSadakat = sadakat;
    }

    public float getFitrana() {
        return mFitrana;
    }

    public void setFitrana(float fitrana) {
        mFitrana = fitrana;
    }

    public float getMosqueDonation() {
        return mMosqueDonation;
    }

    public void setMosqueDonation(float mosqueDonation) {
        mMosqueDonation = mosqueDonation;
    }

    public float getMta() {
        return mMta;
    }

    public void setMta(float mta) {
        mMta = mta;
    }

    public List<String[]> getPaymentCategories(){

        ArrayList categories = new ArrayList();

        categories.add(0, new String[]{"Fullname",mFullname});
        categories.add(1,new String[]{"Receipt Number",mLocalReceipt});
        categories.add(2,new String[] {"Month Paid", mMonthPaid});
        if(mChandaAm > 0){ categories.add(new String[]{"Chanda No",String.valueOf(mChandaAm)});}
        if(mWasiyyat > 0){ categories.add(new String[]{"Wasiyyat",String.valueOf(mWasiyyat)});}
        if(mJalsaSalana > 0){ categories.add(new String[]{"Jalsa Salana", String.valueOf(mJalsaSalana)});}
        if(mTahrikJadid > 0){ categories.add(new String[]{"Tahrik Jadid", String.valueOf(mTahrikJadid)});}
        if(mWaqfJadid > 0){ categories.add(new String[]{"Waqf Jadid", String.valueOf(mWaqfJadid)});}
        if(mWelfare > 0){ categories.add(new String[]{"Welfare", String.valueOf(mWelfare)});}
        if(mScholarship > 0){ categories.add(new String[]{"Scholarship", String.valueOf(mScholarship)});}
        if(mMaryam > 0){ categories.add(new String[]{"Maryam Fund", String.valueOf(mMaryam)});}
        if(mTabligh > 0){ categories.add(new String[]{"Tabligh", String.valueOf(mTabligh)});}
        if(mZakat > 0){ categories.add(new String[]{"Zakat", String.valueOf(mZakat)});}
        if(mSadakat > 0){ categories.add(new String[]{"Sadakat", String.valueOf(mSadakat)});}
        if(mFitrana > 0){ categories.add(new String[]{"Fitrana", String.valueOf(mFitrana)});}
        if(mMosqueDonation > 0){ categories.add(new String[]{"Mosque Donation",String.valueOf(mMosqueDonation)});}
        if(mMta > 0){ categories.add(new String[]{"Mta", String.valueOf(mMta)});}
        if(mCentinary > 0){ categories.add(new String[]{"Centinary Fund", String.valueOf(mCentinary)});}
        if(mWasiyyatHissan > 0){ categories.add(new String[]{"Wasiyyat Hissan", String.valueOf(mWasiyyatHissan)});}
        if(mMiscellaneous > 0){ categories.add(new String[]{"Miscellaneous", String.valueOf(mMiscellaneous)});}
        categories.add(new String[]{"Total", String.valueOf(mSubtotal)});
        return categories;
    }
}
