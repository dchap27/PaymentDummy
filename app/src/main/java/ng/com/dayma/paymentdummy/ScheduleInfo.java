package ng.com.dayma.paymentdummy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ahmad on 7/12/2019.
 */

public final class ScheduleInfo implements Parcelable {
    private final String mScheduleId;
    private final String mJamaat;
    private final String mTitle;
    private MonthInfo mMonth;
    private double mTotalAmount;
    private int mTotalPayers;
    private int mId;
    private boolean mIsComplete = false;

    public ScheduleInfo(String scheduleId, MonthInfo month, String jamaat, String title) {
        this(scheduleId, month, jamaat, title, false, -1);
    }

    public ScheduleInfo (String scheduleId, MonthInfo month, String jamaat, String title, boolean isComplete,int id) {
        mScheduleId = scheduleId;
        mMonth = month;
        mJamaat = jamaat;
        mTitle = title;
        mIsComplete = isComplete;
        mId = id;
    }

    public ScheduleInfo(Parcel source) {
        mScheduleId = source.readString();
        mMonth = source.readParcelable(MonthInfo.class.getClassLoader());
        mJamaat = source.readString();
        mTitle = source.readString();
        mIsComplete = source.readByte() == 1;
    }

    public String getScheduleId() {
        return mScheduleId;
    }

    public MonthInfo getMonth() {
        return mMonth;
    }

    public String getTitle() { return mTitle; }

    public String getJamaat() { return mJamaat; }

    public double getTotalAmount(){
        return mTotalAmount;
    }

    public int getTotalPayers() { return mTotalPayers; }

    public boolean isComplete() {
        return mIsComplete;
    }

    public void setComplete(boolean complete) {
        mIsComplete = complete;
    }

    private String getCompareKey() {
        return mMonth.getMonthId() + "|" + mScheduleId;
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleInfo that = (ScheduleInfo) o;

        return mScheduleId.equals(that.mScheduleId);
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mScheduleId);
        dest.writeParcelable(mMonth, 0);
        dest.writeString(mJamaat);
        dest.writeString(mTitle);
        dest.writeByte((byte) (mIsComplete ? 1 : 0));

    }

    public final static Parcelable.Creator<ScheduleInfo> CREATOR =
            new Creator<ScheduleInfo>() {
                @Override
                public ScheduleInfo createFromParcel(Parcel source) {
                    return new ScheduleInfo(source);
                }

                @Override
                public ScheduleInfo[] newArray(int size) {
                    return new ScheduleInfo[0];
                }
            };

    public int getId() { return mId; }
}
