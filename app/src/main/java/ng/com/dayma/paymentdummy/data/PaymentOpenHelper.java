package ng.com.dayma.paymentdummy.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ng.com.dayma.paymentdummy.MainActivity;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.MemberInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.MonthInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.PaymentInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.ScheduleInfoEntry;

public class PaymentOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ChandaPayment.db";
    public static final int DATABASE_VERSION = 1;
    private final String TAG = getClass().getSimpleName();
    private static PaymentOpenHelper mInstance = null;
    private final Context mContext;

    public PaymentOpenHelper(@Nullable MainActivity context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    public static synchronized PaymentOpenHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new PaymentOpenHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @VisibleForTesting
    public static void clearInstance() {
        mInstance = null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the database table if it doesn't exist
        db.execSQL(MemberInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(MonthInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(ScheduleInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(PaymentInfoEntry.SQL_CREATE_TABLE);

        db.execSQL(MemberInfoEntry.SQL_CREATE_INDEX1);
        db.execSQL(MonthInfoEntry.SQL_CREATE_INDEX1);
        db.execSQL(ScheduleInfoEntry.SQL_CREATE_INDEX1);
        db.execSQL(PaymentInfoEntry.SQL_CREATE_INDEX1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "Updating table from " + oldVersion + " to " + newVersion);
        // You will not need to modify this unless you need to do some android specific things.
        // When upgrading the database, all you need to do is add a file to the assets folder and name it:
        // from_1_to_2.sql with the version that you are upgrading to as the last version.
        for (int i = oldVersion; i < newVersion; ++i) {
            String migrationName = String.format("from_%d_to_%d.sql", i, (i + 1));
            Log.d(TAG, "Looking for migration file: " + migrationName);
            readAndExecuteSQLScript(db, mContext, migrationName);
        }

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    private void readAndExecuteSQLScript(SQLiteDatabase db, Context ctx, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "SQL script file name is empty");
            return;
        }

        Log.d(TAG, "Script found. Executing...");
        AssetManager assetManager = ctx.getAssets();
        BufferedReader reader = null;

        try {
            InputStream is = assetManager.open(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            executeSQLScript(db, reader);
        } catch (IOException e) {
            Log.e(TAG, "IOException:", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException:", e);
                }
            }
        }

    }

    private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) {
        String line;
        StringBuilder statement = new StringBuilder();
        db.beginTransaction();
        try {
            while ((line = reader.readLine()) != null) {
                statement.append(line);
                statement.append("\n");
                if (line.endsWith(";")) {
                    db.execSQL(statement.toString());
                    statement = new StringBuilder();
                }
            }
            db.setTransactionSuccessful();
        } catch (IOException e){
            Log.d(TAG, "Error in database transaction");
        } finally {
            db.endTransaction();
        }
    }
}
