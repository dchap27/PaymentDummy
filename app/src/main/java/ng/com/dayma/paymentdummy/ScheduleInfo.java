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
        String monthId;
        try{
            monthId = mMonth.getMonthId();
        }catch (NullPointerException nullpointerException){
            monthId = "Null-Month";
        }
        return monthId + "|" + mScheduleId;
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

    public long getId() { return mId; }

    public List<String[]> getScheduleCategories(List<PaymentInfo> payments, String invoiceNumber){

        ArrayList categories = new ArrayList();
        double chandaAm = 0;
        double wasiyyat = 0;
        double jalsaSalana = 0;
        double tahrikJadid = 0;
        double waqfJadid = 0;
        double welfare = 0;
        double scholarship = 0;
        double maryam = 0;
        double tabligh = 0;
        double zakat = 0;
        double sadakat = 0;
        double fitrana = 0;
        double mosqueDonation = 0;
        double mta = 0;
        double centinary = 0;
        double wasiyyatHissan = 0;
        double miscellaneous = 0;
        double total = 0;

        for(PaymentInfo paymentInfo : payments){
            chandaAm += paymentInfo.getChandaAm();
            wasiyyat += paymentInfo.getWasiyyat();
            jalsaSalana += paymentInfo.getJalsaSalana();
            tahrikJadid += paymentInfo.getTahrikJadid();
            waqfJadid += paymentInfo.getWaqfJadid();
            scholarship += paymentInfo.getScholarship();
            maryam += paymentInfo.getMaryam();
            tabligh += paymentInfo.getTabligh();
            zakat += paymentInfo.getZakat();
            sadakat += paymentInfo.getSadakat();
            fitrana += paymentInfo.getFitrana();
            mosqueDonation += paymentInfo.getMosqueDonation();
            mta += paymentInfo.getMta();
            centinary += paymentInfo.getCentinary();
            wasiyyatHissan += paymentInfo.getWasiyyatHissan();
            miscellaneous += paymentInfo.getMiscellaneous();
            total += paymentInfo.getSubtotal();
        }
        categories.add(0, new String[]{" ",mTitle});
        categories.add(1,new String[] {"Month Paid", mMonth.getMonthId()});
        categories.add(2,new String[] {"Invoice number", invoiceNumber});
        if(chandaAm > 0){ categories.add(new String[]{"Chanda aAm",String.valueOf(chandaAm)});}
        if(wasiyyat > 0){ categories.add(new String[]{"Wasiyyat",String.valueOf(wasiyyat)});}
        if(jalsaSalana > 0){ categories.add(new String[]{"Jalsa Salana", String.valueOf(jalsaSalana)});}
        if(tahrikJadid > 0){ categories.add(new String[]{"Tahrik Jadid", String.valueOf(tahrikJadid)});}
        if(waqfJadid > 0){ categories.add(new String[]{"Waqf Jadid", String.valueOf(waqfJadid)});}
        if(welfare > 0){ categories.add(new String[]{"Welfare", String.valueOf(welfare)});}
        if(scholarship > 0){ categories.add(new String[]{"Scholarship", String.valueOf(scholarship)});}
        if(maryam > 0){ categories.add(new String[]{"Maryam Fund", String.valueOf(maryam)});}
        if(tabligh > 0){ categories.add(new String[]{"Tabligh", String.valueOf(tabligh)});}
        if(zakat > 0){ categories.add(new String[]{"Zakat", String.valueOf(zakat)});}
        if(sadakat > 0){ categories.add(new String[]{"Sadakat", String.valueOf(sadakat)});}
        if(fitrana > 0){ categories.add(new String[]{"Fitrana", String.valueOf(fitrana)});}
        if(mosqueDonation > 0){ categories.add(new String[]{"Mosque Donation",String.valueOf(mosqueDonation)});}
        if(mta > 0){ categories.add(new String[]{"Mta", String.valueOf(mta)});}
        if(centinary > 0){ categories.add(new String[]{"Centinary Fund", String.valueOf(centinary)});}
        if(wasiyyatHissan > 0){ categories.add(new String[]{"Wasiyyat Hissan", String.valueOf(wasiyyatHissan)});}
        if(miscellaneous > 0){ categories.add(new String[]{"Miscellaneous", String.valueOf(miscellaneous)});}
        categories.add(new String[]{"Total", String.valueOf(total)});
        return categories;
    }
}
