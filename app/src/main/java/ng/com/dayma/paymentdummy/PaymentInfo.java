package ng.com.dayma.paymentdummy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ahmad on 7/12/2019.
 */

public final class PaymentInfo implements Parcelable {
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
    private int mId;

    public PaymentInfo(ScheduleInfo schedule, int chandaNo, String fullname, String localReceipt, String monthPaid,
                       float chandaAm, float wasiyyat, float jalsaSalana, float tarikiJadid,
                       float waqfJadid, float welfare, float scholarship, float maryam,
                       float tabligh, float zakat, float sadakat, float fitrana,
                       float mosqueDonation, float mta, float centinary, float wasiyyatHissan,
                       float miscellaneous, float subtotal, int id) {
        mId = id;
        mChandaNo = chandaNo;
        mFullname = fullname;
        mMonthPaid = monthPaid;
        mLocalReceipt = localReceipt;
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
        mSubtotal = subtotal;
        mSchedule = schedule;

    }

    public PaymentInfo(ScheduleInfo schedule, int chandaNo, String fullname, String localReceipt, String monthPaid) {
        this(null,chandaNo,null,localReceipt,monthPaid, 0.0f, 0.0f, 0.0f,0.0f,
                0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,
                0.0f,0.0f,0.0f,-1);
    }

    private PaymentInfo(Parcel source) {
        mChandaNo = source.readInt();
        mMonthPaid = source.readString();
        mLocalReceipt = source.readString();
        mChandaAm = source.readFloat();
        mWasiyyat = source.readFloat();
        mJalsaSalana = source.readFloat();
        mTahrikJadid = source.readFloat();
        mWaqfJadid = source.readFloat();
        mWelfare = source.readFloat();
        mScholarship = source.readFloat();
        mMaryam = source.readFloat();
        mTabligh = source.readFloat();
        mZakat = source.readFloat();
        mSadakat = source.readFloat();
        mFitrana = source.readFloat();
        mMosqueDonation = source.readFloat();
        mMta = source.readFloat();
        mCentinary = source.readFloat();
        mWasiyyatHissan = source.readFloat();
        mMiscellaneous = source.readFloat();
        mSubtotal = source.readFloat();
        mSchedule = source.readParcelable(ScheduleInfo.class.getClassLoader());
    }

    public int getId() { return mId; }

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
        return mLocalReceipt;
    }

    public void setReceiptNo(String receiptNo) {
        mLocalReceipt = receiptNo;
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
        mSubtotal = mChandaAm + mWasiyyat + mJalsaSalana + mTahrikJadid + mWaqfJadid + mWelfare +
                mScholarship + mTabligh + mMaryam + mCentinary + mMta + mWasiyyatHissan +
                mMosqueDonation + mFitrana + mSadakat + mZakat + mMiscellaneous;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mChandaNo);
        dest.writeParcelable(mSchedule, 0);
        dest.writeString(mMonthPaid);
        dest.writeString(mLocalReceipt);
        dest.writeFloat(mChandaAm);
        dest.writeFloat(mWasiyyat);
        dest.writeFloat(mJalsaSalana);
        dest.writeFloat(mTahrikJadid);
        dest.writeFloat(mWaqfJadid);
        dest.writeFloat(mWelfare);
        dest.writeFloat(mScholarship);
        dest.writeFloat(mMaryam);
        dest.writeFloat(mTabligh);
        dest.writeFloat(mZakat);
        dest.writeFloat(mSadakat);
        dest.writeFloat(mFitrana);
        dest.writeFloat(mMosqueDonation);
        dest.writeFloat(mMta);
        dest.writeFloat(mCentinary);
        dest.writeFloat(mWasiyyatHissan);
        dest.writeFloat(mMiscellaneous);
        dest.writeFloat(mSubtotal);

    }

    public final static Parcelable.Creator<PaymentInfo> CREATOR =
            new Creator<PaymentInfo>() {
                @Override
                public PaymentInfo createFromParcel(Parcel source) {
                    return new PaymentInfo(source);
                }

                @Override
                public PaymentInfo[] newArray(int size) {
                    return new PaymentInfo[size];
                }
            };

    }
