package ng.com.dayma.paymentdummy;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.MonthInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentOpenHelper;

/**
 * Created by Ahmad on 7/12/2019.
 */

public class DataManager {
    private static DataManager ourInstance = null;
    private List<MonthInfo> mMonths = new ArrayList<>();
    private List<PaymentInfo> mPayments = new ArrayList<>();
    private List<ScheduleInfo> mSchedules = new ArrayList<>();

    public static DataManager getInstance() {
        if(ourInstance == null){
            ourInstance = new DataManager();
//            ourInstance.initializeMonths();
//            ourInstance.initializeExamplePayments();
        }
        return ourInstance;
    }

    public static void loadFromDatabase(PaymentOpenHelper dbHelper){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // get the columns for the month_info table
        final String[] monthColumnsProjection = {
                MonthInfoEntry.COLUMN_MONTH_ID
        };
        // query the month_info table with the specified columns
        Cursor monthCursor = db.query(MonthInfoEntry.TABLE_NAME, monthColumnsProjection, null,
                null, null, null, null);// query returns a cursor

        // extract the months from the returned cursor
        loadMonthsFromDatabase(monthCursor);

    }

    private static void loadMonthsFromDatabase(Cursor cursor) {
        // get the columns position
        int monthIdPos = cursor.getColumnIndex(MonthInfoEntry.COLUMN_MONTH_ID);

        // get reference to datamanager singleton and clear any previous month
        DataManager dm = getInstance();
        dm.mMonths.clear();

        // extract data from each row
        while (cursor.moveToNext()) {
            String monthId = cursor.getString(monthIdPos);
            // create new Month and add to mMonths field
            MonthInfo month = new MonthInfo(monthId);
            dm.mMonths.add(month);
        }
        // close cursor after retreiving the rows
        cursor.close();

    }

    public String getCurrentUserName() {
        return "Ahmad Adelaja";
    }

    public String getCurrentUserEmail(){
        return "ahmad@example.com";
    }

    public List<PaymentInfo> getPayments() {
        return mPayments;
    }
    public List<ScheduleInfo> getSchedules(){
        return mSchedules;
    }

    public int createNewPayment(){
        PaymentInfo payment = new PaymentInfo(null, null, 0, 0, 0, 0, 0, null);
        mPayments.add(payment);
        return mPayments.size() - 1;
    }

    public int findPayment(PaymentInfo payment) {
        for(int index=0; index < mPayments.size(); index++){
            if(payment.equals(mPayments.get(index))){
                return index;
            }
        }
        return -1;
    }

    public void removePayment(int index){
        mPayments.remove(index);
    }

    public List<MonthInfo> getMonths(){
        return mMonths;
    }

    public MonthInfo getMonth(String id){
        for(MonthInfo month : mMonths){
            if(id.equals(month.getMonthId()))
                return month;
        }
        return null;
    }

    public ScheduleInfo getSchedule(String id){
        for(ScheduleInfo schedule : mSchedules){
            if(id.equals(schedule.getScheduleId()))
                return schedule;
        }
        return null;
    }

    public List<PaymentInfo> getPayments(ScheduleInfo schedule) {
        ArrayList<PaymentInfo> payments = new ArrayList<>();
        for(PaymentInfo payment:mPayments) {
            if(schedule.equals(payment.getSchedule()))
                payments.add(payment);
        }
        return payments;
    }

    public List<ScheduleInfo> getSchedules(MonthInfo month){
        ArrayList<ScheduleInfo> schedules = new ArrayList<>();
        for(ScheduleInfo schedule:mSchedules) {
            if(month.equals(schedule.getMonth()))
                schedules.add(schedule);
        }
        return schedules;
    }

    public int getPaymentCount(ScheduleInfo schedule) {
        int count = 0;
        for(PaymentInfo payment:mPayments) {
            if(schedule.equals(payment.getSchedule()))
                count++;
        }
        return count;
    }

    public int getScheduleCount(String monthId) {
        int count = 0;
        for(ScheduleInfo schedule:mSchedules) {
            if(monthId.equals(schedule.getMonth().getMonthId()))
                count ++;
        }
        return count;
    }

    public float getScheduleAmount(List<PaymentInfo> payments) {
        float amount = 0;
        PaymentInfo payment;
        for(int i=0; i<payments.size(); i++){
            payment = payments.get(i);
            amount = amount + payment.getTotalAmountPaid();
        }
        return amount;
    }

    private DataManager() {

    }

    public void initializeExamplePayments() {
        final DataManager dm = getInstance();

        MonthInfo month = dm.getMonth("April 2019");
        ScheduleInfo schedule1 = month.getSchedule("Schedule1 for kewulere");
        schedule1.setComplete(true);
        ScheduleInfo schedule2 = month.getSchedule("Schedule1 for felele");
        schedule2.setComplete(true);
        ScheduleInfo schedule3 = month.getSchedule("Oke-Ado schedule1");
        schedule3.setComplete(true);
        mSchedules.add(schedule1);
        mSchedules.add(schedule2);
        mSchedules.add(schedule3);
        mPayments.add(new PaymentInfo("Adelaja", "April", 12328, 0, 10000, 200, 300, schedule3));
        mPayments.add(new PaymentInfo("Kunle", "March", 12327, 450, 30 ,0 ,40, schedule1));
        mPayments.add(new PaymentInfo("Ope", "April", 12330, 45000, 0 ,10 ,400, schedule2));

        month = dm.getMonth("May 2019");
        ScheduleInfo schedule4 = month.getSchedule("Schedule2 for kewulere");
        schedule4.setComplete(true);
        ScheduleInfo schedule5 = month.getSchedule("Oke-Ado schedule2");
        schedule5.setComplete(true);
        mSchedules.add(schedule4);
        mSchedules.add(schedule5);

        mPayments.add(new PaymentInfo("Modupe Ade", "May", 12534, 230, 20, 0, 0, schedule4));
        mPayments.add(new PaymentInfo("Adeola", "April", 23452, 4000, 0,0,0, schedule5));

        month = dm.getMonth("June 2019");
        ScheduleInfo schedule6 = month.getSchedule("Schedule3 for felele");
        schedule6.setComplete(true);
        ScheduleInfo schedule7 = month.getSchedule("Oke-Ado schedule3");
        schedule7.setComplete(true);
//        month.getSchedule("Schedule3 for kewulere").setComplete(true);
        mSchedules.add(schedule6);
        mSchedules.add(schedule7);

        mPayments.add(new PaymentInfo("Faiza", "June", 12345, 0, 0, 2300, 0, schedule6));
        mPayments.add(new PaymentInfo("Adelaja", "June", 123420, 3400, 80, 50, 30, schedule6));
        mPayments.add(new PaymentInfo("Ope", "June", 123430, 8400, 40, 0, 0, schedule1));
        mPayments.add(new PaymentInfo("Adeola", "May", 123410, 400, 0, 5000, 30, schedule7));
    }

    private void initializeMonths() {
        mMonths.add(initializeMonth1());
        mMonths.add(initializeMonth2());
        mMonths.add(initializeMonth3());

    }

    private MonthInfo initializeMonth3() {
        MonthInfo month = new MonthInfo("April 2019");
        List<ScheduleInfo> schedules = new ArrayList<>();
        schedules.add(new ScheduleInfo("Schedule1 for kewulere", month));
        schedules.add(new ScheduleInfo("Schedule1 for felele", month));
        schedules.add(new ScheduleInfo("Oke-Ado schedule1", month));
        for(int i=0; i < schedules.size(); i++) {
            ScheduleInfo schedule = schedules.get(i);
            month.addSchedule(schedule);
        }

        return month;
    }

    private MonthInfo initializeMonth2() {
        MonthInfo month = new MonthInfo("May 2019");
        List<ScheduleInfo> schedules = new ArrayList<>();
        schedules.add(new ScheduleInfo("Schedule2 for kewulere", month));
        schedules.add(new ScheduleInfo("Schedule2 for felele", month));
        schedules.add(new ScheduleInfo("Oke-Ado schedule2", month));
        for(int i=0; i < schedules.size(); i++) {
            ScheduleInfo schedule = schedules.get(i);
            month.addSchedule(schedule);
        }

        return month;
    }

    private MonthInfo initializeMonth1() {
        MonthInfo month = new MonthInfo("June 2019");
        List<ScheduleInfo> schedules = new ArrayList<>();
        schedules.add(new ScheduleInfo("Schedule3 for kewulere",month));
        schedules.add(new ScheduleInfo("Schedule3 for felele", month));
        schedules.add(new ScheduleInfo("Oke-Ado schedule3", month));
        for(int i=0; i < schedules.size(); i++) {
            ScheduleInfo schedule = schedules.get(i);
            month.addSchedule(schedule);
        }

        return month;
    }

    public List<String> getMonthsOfTheYear() {
        List<String> mMonthsYear = new ArrayList<>();
        mMonthsYear.add(0,"January");
        mMonthsYear.add(1, "February");
        mMonthsYear.add(2, "March");
        mMonthsYear.add(3, "April");
        mMonthsYear.add(4, "May");
        mMonthsYear.add(5, "June");
        mMonthsYear.add(6, "July");
        mMonthsYear.add(7, "August");
        mMonthsYear.add(8, "September");
        mMonthsYear.add(9, "October");
        return mMonthsYear;
    }

    public List<String> getPaymentIds() {
        List<String> paymentIds = new ArrayList<>();
        paymentIds.add(0, "Modupe Ade");
        paymentIds.add(1, "Adeola");
        paymentIds.add(2, "Ope");
        paymentIds.add(3, "Kunle");
        paymentIds.add(4, "Adelaja");
        paymentIds.add(5, "Faiza");
        return paymentIds;
    }
}
