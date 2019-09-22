package ng.com.dayma.paymentdummy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ahmad on 7/12/2019.
 */

public final class PaymentInfo implements Parcelable {
    private String mPaymentId;
    private ScheduleInfo mSchedule;
    private String mMonthPaid;
    private int mReceiptNo;
    private float mChandaAm;
    private float mWasiyyat;
    private float mTahrikJadid;
    private float mWaqfJadid;

    public PaymentInfo(String paymentId, String monthPaid, int receiptNo, float chandaAm, float wasiyyat,
                       float tahrikJadid, float waqfJadid) {
        this(paymentId,monthPaid,receiptNo,chandaAm,wasiyyat,tahrikJadid,waqfJadid,null);
    }

    public PaymentInfo(String paymentId, String monthPaid, int receiptNo, float chandaAm, float wasiyyat,
                       float tahrikJadid, float waqfJadid, ScheduleInfo schedule) {
        mPaymentId = paymentId;
        mMonthPaid = monthPaid;
        mReceiptNo = receiptNo;
        mChandaAm = chandaAm;
        mWasiyyat = wasiyyat;
        mTahrikJadid = tahrikJadid;
        mWaqfJadid = waqfJadid;
        mSchedule = schedule;

    }

    private PaymentInfo(Parcel source) {
        mPaymentId = source.readString();
        mMonthPaid = source.readString();
        mReceiptNo = source.readInt();
        mChandaAm = source.readFloat();
        mWasiyyat = source.readFloat();
        mTahrikJadid = source.readFloat();
        mWaqfJadid = source.readFloat();
        mSchedule = source.readParcelable(ScheduleInfo.class.getClassLoader());
    }

    public String getPaymentId() {
        return mPaymentId;
    }

    public void setPaymentId(String paymentId) {
        mPaymentId = paymentId;
    }

    public ScheduleInfo getSchedule() {
        return mSchedule;
    }

    public void setSchedule(ScheduleInfo schedule) {
        mSchedule = schedule;
    }

    public int getReceiptNo() {
        return mReceiptNo;
    }

    public void setReceiptNo(int receiptNo) {
        mReceiptNo = receiptNo;
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

    public float getTotalAmountPaid() {
        return getChandaAm() + getWasiyyat() + getTahrikJadid() + getWaqfJadid();
    }
    private String getCompareKey() {
        return mPaymentId + "|" + mMonthPaid;
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
        dest.writeString(mPaymentId);
        dest.writeParcelable(mSchedule, 0);
        dest.writeString(mMonthPaid);
        dest.writeInt(mReceiptNo);
        dest.writeFloat(mChandaAm);
        dest.writeFloat(mWasiyyat);
        dest.writeFloat(mTahrikJadid);
        dest.writeFloat(mWaqfJadid);

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
