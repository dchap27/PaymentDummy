package ng.com.dayma.paymentdummy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.MemberInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.MonthInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.PaymentInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.ScheduleInfoEntry;

public class PaymentOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ChandaPayment.db";
    public static final int DATABASE_VERSION = 1;

    public PaymentOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the database table if it doesn't exist
        db.execSQL(MemberInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(MonthInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(ScheduleInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(PaymentInfoEntry.SQL_CREATE_TABLE);

        // create a dummy data
        DatabaseDataWorker worker = new DatabaseDataWorker(db);
        worker.insertMembers();
        worker.insertMonths();
        worker.insertSampleSchedules();
        worker.insertSamplePayments();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
