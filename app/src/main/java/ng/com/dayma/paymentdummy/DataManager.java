package ng.com.dayma.paymentdummy;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.MonthInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.PaymentInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.ScheduleInfoEntry;
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
                null, null, null, MonthInfoEntry.COLUMN_MONTH_ID + " DESC");// query returns a cursor

        // extract the months from the returned cursor
        loadMonthsFromDatabase(monthCursor);

        // get the data for schedule_info table
        final String[] scheduleColumnsProjection = {
                ScheduleInfoEntry.COLUMN_SCHEDULE_ID,
                ScheduleInfoEntry.COLUMN_SCHEDULE_TITLE,
                ScheduleInfoEntry.COLUMN_SCHEDULE_JAMAAT,
                ScheduleInfoEntry.COLUMN_MONTH_ID,
                ScheduleInfoEntry.COLUMN_SCHEDULE_IS_COMPLETE,
                ScheduleInfoEntry._ID
        };
        String scheduleOrderby = ScheduleInfoEntry.COLUMN_MONTH_ID + "," + ScheduleInfoEntry.COLUMN_SCHEDULE_JAMAAT;
        // query the schedule_info table
        Cursor scheduleCursor = db.query(ScheduleInfoEntry.TABLE_NAME, scheduleColumnsProjection, null,
                null, null, null, scheduleOrderby);
        loadSchedulesFromDatabase(scheduleCursor);

        //get columns for payment_info table
        final String[] paymentColumnsProjection = new String[]{
                PaymentInfoEntry.COLUMN_SCHEDULE_ID,
                PaymentInfoEntry._ID,
                PaymentInfoEntry.COLUMN_PAYMENT_CHANDANO,
                PaymentInfoEntry.COLUMN_PAYMENT_FULLNAME,
                PaymentInfoEntry.COLUMN_PAYMENT_LOCALRECEIPT,
                PaymentInfoEntry.COLUMN_PAYMENT_MONTHPAID,
                PaymentInfoEntry.COLUMN_PAYMENT_CHANDAAM,
                PaymentInfoEntry.COLUMN_PAYMENT_CHANDAWASIYYAT,
                PaymentInfoEntry.COLUMN_PAYMENT_JALSASALANA,
                PaymentInfoEntry.COLUMN_PAYMENT_TARIKIJADID,
                PaymentInfoEntry.COLUMN_PAYMENT_WAQFIJADID,
                PaymentInfoEntry.COLUMN_PAYMENT_WELFAREFUND,
                PaymentInfoEntry.COLUMN_PAYMENT_SCHOLARSHIP,
                PaymentInfoEntry.COLUMN_PAYMENT_MARYAMFUND,
                PaymentInfoEntry.COLUMN_PAYMENT_TABLIGH,
                PaymentInfoEntry.COLUMN_PAYMENT_ZAKAT,
                PaymentInfoEntry.COLUMN_PAYMENT_SADAKAT,
                PaymentInfoEntry.COLUMN_PAYMENT_FITRANA,
                PaymentInfoEntry.COLUMN_PAYMENT_MOSQUEDONATION,
                PaymentInfoEntry.COLUMN_PAYMENT_MTA,
                PaymentInfoEntry.COLUMN_PAYMENT_CENTINARYKHILAFAT,
                PaymentInfoEntry.COLUMN_PAYMENT_WASIYYATHISSANJAIDAD,
                PaymentInfoEntry.COLUMN_PAYMENT_MISCELLANEOUS,
                PaymentInfoEntry.COLUMN_PAYMENT_SUBTOTAL

        };

        Cursor cursor = db.query(PaymentInfoEntry.TABLE_NAME, paymentColumnsProjection, null, null,
                null, null, PaymentInfoEntry.COLUMN_PAYMENT_FULLNAME);
        loadPaymentsFromDatabase(cursor);


    }

    private static void loadPaymentsFromDatabase(Cursor cursor) {
        // get the positions of the columns
        int scheduleIdPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_SCHEDULE_ID);
        int chandaNoPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_CHANDANO);
        int fullnamePos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_FULLNAME);
        int localReceiptPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_LOCALRECEIPT);
        int monthPaidPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MONTHPAID);
        int chandaPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_CHANDAAM);
        int wasiyyatPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_CHANDAWASIYYAT);
        int jalsaPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_JALSASALANA);
        int tarikiPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_TARIKIJADID);
        int waqfPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_WAQFIJADID);
        int welfarePos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_WELFAREFUND);
        int scholarshipPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_SCHOLARSHIP);
        int maryamPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MARYAMFUND);
        int tablighPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_TABLIGH);
        int zakatPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_ZAKAT);
        int sadakatPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_SADAKAT);
        int fitranaPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_FITRANA);
        int mosqueDonationPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MOSQUEDONATION);
        int mtaPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MTA);
        int centinaryPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_CENTINARYKHILAFAT);
        int wasiyyatHissanPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_WASIYYATHISSANJAIDAD);
        int miscellaneousPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MISCELLANEOUS);
        int subtotalPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_SUBTOTAL);
        int idPos = cursor.getColumnIndex(PaymentInfoEntry._ID);

        DataManager dm = getInstance();
        dm.mPayments.clear();

        while (cursor.moveToNext()) {
            String scheduleId = cursor.getString(scheduleIdPos);
            int chandaNo = cursor.getInt(chandaNoPos);
            String fullname = cursor.getString(fullnamePos);
            String localReceipt = cursor.getString(localReceiptPos);
            String monthPaid = cursor.getString(monthPaidPos);
            float chandaAm = cursor.getFloat(chandaPos);
            float wasiyyat = cursor.getFloat(wasiyyatPos);
            float jalsaSalana = cursor.getFloat(jalsaPos);
            float tarikiJadid = cursor.getFloat(tarikiPos);
            float waqfJadid = cursor.getFloat(waqfPos);
            float welfare = cursor.getFloat(welfarePos);
            float scholarship = cursor.getFloat(scholarshipPos);
            float maryam = cursor.getFloat(maryamPos);
            float tabligh = cursor.getFloat(tablighPos);
            float zakat = cursor.getFloat(zakatPos);
            float sadakat = cursor.getFloat(sadakatPos);
            float fitrana = cursor.getFloat(fitranaPos);
            float mosqueDonation = cursor.getFloat(mosqueDonationPos);
            float mta = cursor.getFloat(mtaPos);
            float centinary = cursor.getFloat(centinaryPos);
            float wasiyyatHissan = cursor.getFloat(wasiyyatHissanPos);
            float miscellaneous = cursor.getFloat(miscellaneousPos);
            float subtotal = cursor.getFloat(subtotalPos);
            int id = cursor.getInt(idPos);

            ScheduleInfo schedule = dm.getSchedule(scheduleId);
            PaymentInfo payment = new PaymentInfo(schedule,chandaNo,fullname,localReceipt,monthPaid,chandaAm,
                    wasiyyat,jalsaSalana,tarikiJadid,waqfJadid,welfare,scholarship,maryam,
                    tabligh,zakat,sadakat,fitrana,mosqueDonation,mta,centinary,wasiyyatHissan,
                    miscellaneous,subtotal,id);

            dm.mPayments.add(payment);
        }
        // close cursor
        cursor.close();
    }

    private static void loadSchedulesFromDatabase(Cursor cursor) {
        // get the positions of the columns
        int scheduleIdPos = cursor.getColumnIndex(ScheduleInfoEntry.COLUMN_SCHEDULE_ID);
        int monthIdPos = cursor.getColumnIndex(ScheduleInfoEntry.COLUMN_MONTH_ID);
        int titlePos = cursor.getColumnIndex(ScheduleInfoEntry.COLUMN_SCHEDULE_TITLE);
        int jamaatPos = cursor.getColumnIndex(ScheduleInfoEntry.COLUMN_SCHEDULE_JAMAAT);
        int completionstatusPos = cursor.getColumnIndex(ScheduleInfoEntry.COLUMN_SCHEDULE_IS_COMPLETE);
        int idPos = cursor.getColumnIndex(ScheduleInfoEntry._ID);

        DataManager dm = getInstance();
        dm.mSchedules.clear();

        // iterate through the data
        while (cursor.moveToNext()){
            boolean status = false;
            String scheduleId = cursor.getString(scheduleIdPos);
            String monthId = cursor.getString(monthIdPos);
            String title = cursor.getString(titlePos);
            String jamaat = cursor.getString(jamaatPos);
            int completionStatus = cursor.getInt(completionstatusPos);
            int id = cursor.getInt(idPos);
            if (completionStatus == 1){
                status = true;
            }

            MonthInfo month = dm.getMonth(monthId);

            // create new schedule
            ScheduleInfo schedule = new ScheduleInfo(scheduleId, month, jamaat, title, status, id);
            dm.mSchedules.add(schedule);
        }
        // close cursor
        cursor.close();
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
        PaymentInfo payment = new PaymentInfo(null, 0, null,
                null, null);
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

    public MonthInfo getMonth(int id){
        for(MonthInfo month : mMonths){
            if(id == (month.getId()))
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

    public ScheduleInfo getSchedule(int id){
        for(ScheduleInfo schedule : mSchedules){
            if(id ==(schedule.getId()))
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

//    public void initializeExamplePayments() {
//        final DataManager dm = getInstance();
//
//        MonthInfo month = dm.getMonth("April 2019");
//        ScheduleInfo schedule1 = month.getSchedule("Schedule1 for kewulere");
//        schedule1.setComplete(true);
//        ScheduleInfo schedule2 = month.getSchedule("Schedule1 for felele");
//        schedule2.setComplete(true);
//        ScheduleInfo schedule3 = month.getSchedule("Oke-Ado schedule1");
//        schedule3.setComplete(true);
//        mSchedules.add(schedule1);
//        mSchedules.add(schedule2);
//        mSchedules.add(schedule3);
//        mPayments.add(new PaymentInfo("Adelaja", "April", 12328, 0, 10000, 200, 300, schedule3));
//        mPayments.add(new PaymentInfo("Kunle", "March", 12327, 450, 30 ,0 ,40, schedule1));
//        mPayments.add(new PaymentInfo("Ope", "April", 12330, 45000, 0 ,10 ,400, schedule2));
//
//        month = dm.getMonth("May 2019");
//        ScheduleInfo schedule4 = month.getSchedule("Schedule2 for kewulere");
//        schedule4.setComplete(true);
//        ScheduleInfo schedule5 = month.getSchedule("Oke-Ado schedule2");
//        schedule5.setComplete(true);
//        mSchedules.add(schedule4);
//        mSchedules.add(schedule5);
//
//        mPayments.add(new PaymentInfo("Modupe Ade", "May", 12534, 230, 20, 0, 0, schedule4));
//        mPayments.add(new PaymentInfo("Adeola", "April", 23452, 4000, 0,0,0, schedule5));
//
//        month = dm.getMonth("June 2019");
//        ScheduleInfo schedule6 = month.getSchedule("Schedule3 for felele");
//        schedule6.setComplete(true);
//        ScheduleInfo schedule7 = month.getSchedule("Oke-Ado schedule3");
//        schedule7.setComplete(true);
////        month.getSchedule("Schedule3 for kewulere").setComplete(true);
//        mSchedules.add(schedule6);
//        mSchedules.add(schedule7);
//
//        mPayments.add(new PaymentInfo("Faiza", "June", 12345, 0, 0, 2300, 0, schedule6));
//        mPayments.add(new PaymentInfo("Adelaja", "June", 123420, 3400, 80, 50, 30, schedule6));
//        mPayments.add(new PaymentInfo("Ope", "June", 123430, 8400, 40, 0, 0, schedule1));
//        mPayments.add(new PaymentInfo("Adeola", "May", 123410, 400, 0, 5000, 30, schedule7));
//    }
//
//    private void initializeMonths() {
//        mMonths.add(initializeMonth1());
//        mMonths.add(initializeMonth2());
//        mMonths.add(initializeMonth3());
//
//    }
//
//    private MonthInfo initializeMonth3() {
//        MonthInfo month = new MonthInfo("April 2019");
//        List<ScheduleInfo> schedules = new ArrayList<>();
//        schedules.add(new ScheduleInfo("Schedule1 for kewulere", month));
//        schedules.add(new ScheduleInfo("Schedule1 for felele", month));
//        schedules.add(new ScheduleInfo("Oke-Ado schedule1", month));
//        for(int i=0; i < schedules.size(); i++) {
//            ScheduleInfo schedule = schedules.get(i);
//            month.addSchedule(schedule);
//        }
//
//        return month;
//    }
//
//    private MonthInfo initializeMonth2() {
//        MonthInfo month = new MonthInfo("May 2019");
//        List<ScheduleInfo> schedules = new ArrayList<>();
//        schedules.add(new ScheduleInfo("Schedule2 for kewulere", month));
//        schedules.add(new ScheduleInfo("Schedule2 for felele", month));
//        schedules.add(new ScheduleInfo("Oke-Ado schedule2", month));
//        for(int i=0; i < schedules.size(); i++) {
//            ScheduleInfo schedule = schedules.get(i);
//            month.addSchedule(schedule);
//        }
//
//        return month;
//    }
//
//    private MonthInfo initializeMonth1() {
//        MonthInfo month = new MonthInfo("June 2019");
//        List<ScheduleInfo> schedules = new ArrayList<>();
//        schedules.add(new ScheduleInfo("Schedule3 for kewulere",month));
//        schedules.add(new ScheduleInfo("Schedule3 for felele", month));
//        schedules.add(new ScheduleInfo("Oke-Ado schedule3", month));
//        for(int i=0; i < schedules.size(); i++) {
//            ScheduleInfo schedule = schedules.get(i);
//            month.addSchedule(schedule);
//        }
//
//        return month;
//    }

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
        mMonthsYear.add(10, "November");
        mMonthsYear.add(11, "December");
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
