package ng.com.dayma.paymentdummy.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Jim.
 */

public class DatabaseDataWorker {
    private SQLiteDatabase mDb;

    public DatabaseDataWorker(SQLiteDatabase db) {
        mDb = db;
    }

    public void insertMembers(){
        insertMember("Kewulere", 930293, "Kolawola Abdul");
        insertMember("Kewulere",3450, "John Sefiu");
        insertMember("Kewulere", 83929, "Sekinat Orimipe");
        insertMember("Ashipa", 293883, "Ipadeola Rafiu");
        insertMember( "Ashipa", 3323, "Mukaila Oladele");
        insertMember("Oke-Ado", 3729, "Emmanuel Lawal");
        insertMember("Oke-Ado", 9320, "Adepoju Suraj");
        insertMember("Felele", 38299, "Haruna Mujidat");
        insertMember("Felele", 38272, "Garba Idiat");
        insertMember("Kewulere", 2345, "Mahmud kaka");
        insertMember("Kewulere", 2365, "Muhammad Tolu");
        insertMember("Oke-Ado", 2115, "Adeleke Haruna");
        insertMember("Oke-Ado", 2005, "Modupe Adeoye");
        insertMember("Oke-Ado", 29222, "Kunle Yusuf");
    }

    public void insertMonths() {
        insertMonth("May 2019");
        insertMonth("June 2019");
        insertMonth("August 2019");
        insertMonth("January 2019");
    }

    public void insertSampleSchedules() {
        insertSchedule("May 2019", "May_2019_Kewulere_1", "Kewulere", "Schedule for chanda payment May 2019");
        insertSchedule("January 2019", "January_2019_Kewulere_2", "Kewulere", "Schedule for chanda payment January 2019");
        insertSchedule("June 2019", "June_2019_Felele_3", "Felele", "Schedule for chanda payment June");
        insertSchedule("May 2019", "May_2019_Felele_4", "Felele", "Schedule for chanda payment May 2019");
        insertSchedule("August 2019", "August_2019_Oke-Ado_5", "Oke-Ado", "Schedule for chanda payment August 2019");
        insertSchedule("June 2019", "June_2019_Kewulere_6", "Kewulere", "Schedule for chanda payment 2019");
        insertSchedule("August 2019", "August_2019_Felele_7", "Felele", "Schedule for chanda payment Felele August 2019");

    }

    public void insertSamplePayments(){
        insertPayment("May_2019_Kewulere_1", 2345, "0001", "MAY2019", 200,
                70, 0, 10);
        insertPayment("May_2019_Kewulere_1", 2365, "00031", "MAY2019", 290,
                709, 700, 10);
        insertPayment("May_2019_Kewulere_1", 3450, "000231", "MAY2019", 7000,
                0, 50, 0);
        insertPayment("May_2019_Kewulere_1", 930293, "00022", "MAY2019", 4000,
                60, 200, 30);

        insertPayment("January_2019_Kewulere_2", 83929, "0067701", "JAN2019", 0,
                1000, 0, 70);
        insertPayment("January_2019_Kewulere_2", 3450, "0005", "JAN2019", 800,
                0, 560, 300);
        insertPayment("January_2019_Kewulere_2", 2345, "00087", "JAN2019", 1200,
                210, 30, 0);

        insertPayment("May_2019_Felele_4", 38299, "0009551", "MAY2019", 2500,
                700, 030, 10);
        insertPayment("May_2019_Felele_4", 38272, "00081", "MAY2019", 2900,
                7050, 20, 100);

        insertPayment("August_2019_OkeAdo_5", 2115, "032001", "AUG2019", 4500,
                0, 200, 0);
        insertPayment("August_2019_OkeAdo_5", 2005, "0001093", "AUG2018", 34200,
                700, 340, 90);
        insertPayment("August_2019_OkeAdo_5", 29222, "9004001", "JUL2019", 2900,
                0, 0230, 100);
    }

    private void insertMonth(String monthId) {
        ContentValues values = new ContentValues();
        values.put(PaymentDatabaseContract.MonthInfoEntry.COLUMN_MONTH_ID, monthId);

        long newRowId = mDb.insert(PaymentDatabaseContract.MonthInfoEntry.TABLE_NAME, null, values);
    }

     private void insertSchedule(String monthId, String scheduleId, String jamaat, String title) {
        ContentValues values = new ContentValues();
        values.put(PaymentDatabaseContract.ScheduleInfoEntry.COLUMN_MONTH_ID, monthId);
        values.put(PaymentDatabaseContract.ScheduleInfoEntry.COLUMN_SCHEDULE_ID, scheduleId);
        values.put(PaymentDatabaseContract.ScheduleInfoEntry.COLUMN_MEMBER_JAMAATNAME, jamaat);
        values.put(PaymentDatabaseContract.ScheduleInfoEntry.COLUMN_SCHEDULE_TITLE, title);

        long newRowId = mDb.insert(PaymentDatabaseContract.ScheduleInfoEntry.TABLE_NAME, null, values);
    }

    private void insertPayment(String scheduleId, int chandaNo, String localReceipt, String monthPaid,
                               float chandaAm, float jalsaSalana, float tarikiJadid, float welfareFund){
        ContentValues values = new ContentValues();
        values.put(PaymentDatabaseContract.PaymentInfoEntry.COLUMN_SCHEDULE_ID, scheduleId);
        values.put(PaymentDatabaseContract.PaymentInfoEntry.COLUMN_MEMBER_CHANDANO, chandaNo);
        values.put(PaymentDatabaseContract.PaymentInfoEntry.COLUMN_PAYMENT_LOCALRECEIPT, localReceipt);
        values.put(PaymentDatabaseContract.PaymentInfoEntry.COLUMN_PAYMENT_MONTHPAID, monthPaid);
        values.put(PaymentDatabaseContract.PaymentInfoEntry.COLUMN_PAYMENT_CHANDAAM, chandaAm);
        values.put(PaymentDatabaseContract.PaymentInfoEntry.COLUMN_PAYMENT_JALSASALANA, jalsaSalana);
        values.put(PaymentDatabaseContract.PaymentInfoEntry.COLUMN_PAYMENT_TARIKIJADID, tarikiJadid);
        values.put(PaymentDatabaseContract.PaymentInfoEntry.COLUMN_PAYMENT_WELFAREFUND, welfareFund);

        long newRowId = mDb.insert(PaymentDatabaseContract.PaymentInfoEntry.TABLE_NAME, null, values);

    }

    private void insertMember(String jamaatName, int chandaNo, String fullname){
        ContentValues values = new ContentValues();
        values.put(PaymentDatabaseContract.MemberInfoEntry.COLUMN_MEMBER_JAMAATNAME, jamaatName);
        values.put(PaymentDatabaseContract.MemberInfoEntry.COLUMN_MEMBER_ID, (chandaNo) + " - " + fullname);
        values.put(PaymentDatabaseContract.MemberInfoEntry.COLUMN_MEMBER_CHANDANO, chandaNo);
        values.put(PaymentDatabaseContract.MemberInfoEntry.COLUMN_MEMBER_FULLNAME, fullname);

        long newRowId = mDb.insert(PaymentDatabaseContract.MemberInfoEntry.TABLE_NAME, null, values);

    }

}
