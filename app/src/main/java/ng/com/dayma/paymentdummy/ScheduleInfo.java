package ng.com.dayma.paymentdummy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmad on 7/12/2019.
 */

public final class ScheduleInfo implements Parcelable {
    private final String mScheduleId;
    private MonthInfo mMonth;
    private boolean mIsComplete = false;

    public ScheduleInfo(String scheduleId, MonthInfo month) {
        this(scheduleId, month, false);
    }

    public ScheduleInfo (String scheduleId, MonthInfo month, boolean isComplete) {
        mScheduleId = scheduleId;
        mMonth = month;
        mIsComplete = isComplete;
    }

    public ScheduleInfo(Parcel source) {
        mScheduleId = source.readString();
        mMonth = source.readParcelable(MonthInfo.class.getClassLoader());
        mIsComplete = source.readByte() == 1;
    }

    public String getScheduleId() {
        return mScheduleId;
    }

    public MonthInfo getMonth() {
        return mMonth;
    }

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
}
