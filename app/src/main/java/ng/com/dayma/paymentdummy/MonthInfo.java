package ng.com.dayma.paymentdummy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmad on 7/12/2019.
 */

public final class MonthInfo implements Parcelable {
    private final String mMonthId;
    private final String mTitle;
    private List<ScheduleInfo> mSchedules;

    public MonthInfo(String monthId, String title) {
        this(monthId,title, null);
    }

    public MonthInfo(String monthId, String title, List<ScheduleInfo> schedules) {
        mMonthId = monthId;
        mTitle = title;
        mSchedules = schedules;
    }

    public MonthInfo (Parcel source) {
        mMonthId = source.readString();
        mTitle = source.readString();
        mSchedules = new ArrayList<>();
        source.readTypedList(mSchedules, ScheduleInfo.CREATOR);
    }

    public String getMonthId() {
        return mMonthId;
    }

    String getTitle() {
        return mTitle;
    }

    public void addSchedule(ScheduleInfo schedule) {
        if(mSchedules == null){
            mSchedules = new ArrayList<>();
        }
        mSchedules.add(schedule);
    }

    public List<ScheduleInfo> getSchedules() {
        return mSchedules;
    }

    public boolean[] getScheduleCompletionStatus() {
        boolean[] status = new boolean[mSchedules.size()];

        for(int i=0; i < mSchedules.size(); i++)
            status[i] = mSchedules.get(i).isComplete();

        return status;
    }

    public void setSchedulesCompletionStatus(boolean[] status) {
        for(int i=0; i < mSchedules.size(); i++)
            mSchedules.get(i).setComplete(status[i]);
    }

    public ScheduleInfo getSchedule(String scheduleId) {
        for(ScheduleInfo scheduleInfo: mSchedules) {
            if(scheduleId.equals(scheduleInfo.getScheduleId()))
                return scheduleInfo;
        }
        return null;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MonthInfo that = (MonthInfo) o;

        return mMonthId.equals(that.mMonthId);

    }

    @Override
    public int hashCode() {
        return mMonthId.hashCode();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMonthId);
        dest.writeString(mTitle);
        dest.writeTypedList(mSchedules);

    }

    public final static Parcelable.Creator<MonthInfo> CREATOR =
            new Creator<MonthInfo>() {
                @Override
                public MonthInfo createFromParcel(Parcel source) {
                    return new MonthInfo(source);
                }

                @Override
                public MonthInfo[] newArray(int size) {
                    return new MonthInfo[size];
                }
            };


}
