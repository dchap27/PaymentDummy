package ng.com.dayma.paymentdummy;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
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
    private List<MemberInfo> mMembers = new ArrayList<>();
    public static final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);

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
                ScheduleInfoEntry.COLUMN_MEMBER_JAMAATNAME,
                ScheduleInfoEntry.COLUMN_MONTH_ID,
                ScheduleInfoEntry.COLUMN_SCHEDULE_ISCOMPLETE,
                ScheduleInfoEntry._ID
        };
        String scheduleOrderby = ScheduleInfoEntry.COLUMN_MONTH_ID + "," + ScheduleInfoEntry.COLUMN_MEMBER_JAMAATNAME;
        // query the schedule_info table
        Cursor scheduleCursor = db.query(ScheduleInfoEntry.TABLE_NAME, scheduleColumnsProjection, null,
                null, null, null, scheduleOrderby);
        loadSchedulesFromDatabase(scheduleCursor);

        //get columns for payment_info table
        final String[] paymentColumnsProjection = new String[]{
                PaymentInfoEntry.COLUMN_SCHEDULE_ID,
                PaymentInfoEntry._ID,
                PaymentInfoEntry.COLUMN_MEMBER_CHANDANO,
                PaymentInfoEntry.COLUMN_MEMBER_FULLNAME,
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
                null, null, PaymentInfoEntry.COLUMN_MEMBER_FULLNAME);
        loadPaymentsFromDatabase(cursor);


    }

    private static void loadPaymentsFromDatabase(Cursor cursor) {
        // get the positions of the columns
        int scheduleIdPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_SCHEDULE_ID);
        int chandaNoPos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_MEMBER_CHANDANO);
        int fullnamePos = cursor.getColumnIndex(PaymentInfoEntry.COLUMN_MEMBER_FULLNAME);
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
        int jamaatPos = cursor.getColumnIndex(ScheduleInfoEntry.COLUMN_MEMBER_JAMAATNAME);
        int completionstatusPos = cursor.getColumnIndex(ScheduleInfoEntry.COLUMN_SCHEDULE_ISCOMPLETE);
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

    public int addNewMember(){
        MemberInfo member = new MemberInfo(null, -1, null);
        mMembers.add(member);
        return mMembers.size() - 1;
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

    public PaymentInfo getPayment(int id){
        for(PaymentInfo payment : mPayments){
            if(id ==(payment.getId()))
                return payment;
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

    public float getScheduleAmount(ScheduleInfo schedule) {
        float amount = 0;
        List<PaymentInfo> payments = getPayments(schedule);
        PaymentInfo payment;
        for(int i=0; i<payments.size(); i++){
            payment = payments.get(i);
            amount = amount + payment.getSubtotal();
        }
        return amount;
    }

    private DataManager() {

    }


    public ArrayList<String> getMonthsWithYear() {
        ArrayList monthsList = new ArrayList();

        String[] months = new DateFormatSymbols().getShortMonths();
        int year = CURRENT_YEAR - 1;
        while(year < CURRENT_YEAR + 2){
            for(int i = 0; i < months.length; i++){
                String mon = months[i].toUpperCase() + year;
                monthsList.add(mon);
            }
            year++;
        }

        return monthsList;
    }

    public ArrayList<String> getMonthsOnly(){
        ArrayList monthsList = new ArrayList();
        String[] months = new DateFormatSymbols().getMonths();
        for (int i = 0; i < months.length; i++) {
            String month = months[i];
            monthsList.add(months[i]);
        }
        return monthsList;
    }

    public ArrayList<String> getYearsOnly(){
        ArrayList years = new ArrayList();
        int yearlimit = CURRENT_YEAR - 1;
        while (yearlimit < CURRENT_YEAR + 2){
            years.add(yearlimit);
            ++yearlimit;
        }
        return years;
    }
}
