package ng.com.dayma.paymentdummy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.InputStream;
import java.util.List;

import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract;
import ng.com.dayma.paymentdummy.data.PaymentProviderContract;
import ng.com.dayma.paymentdummy.data.ReadCSV;

public class CsvUtility {
    private Context mContext;

    public CsvUtility(Context context){
        mContext = context;

    }

    public void readCSVToDatabase(String jamaatName, InputStream inputStream){
        // InputStream inputstream = mContext.getResources().openRawResource(R.raw.felele);
        ReadCSV csvFile = new ReadCSV(inputStream);
        List<String[]> jamaatInfo = csvFile.read();
        for(int i=1; i<jamaatInfo.size(); i++){
            // start from i=1 to avoid insert of the heading
            insertMember(jamaatName, Integer.parseInt(jamaatInfo.get(i)[0]), jamaatInfo.get(i)[1]);
        }
        Log.d("CSVUtility", "Done reading file!");
    }
    private void insertMember(String jamaatName, int chandaNo, String fullname){
        ContentValues values = new ContentValues();
        values.put(PaymentDatabaseContract.MemberInfoEntry.COLUMN_MEMBER_JAMAATNAME, jamaatName);
        values.put(PaymentDatabaseContract.MemberInfoEntry.COLUMN_MEMBER_ID, (chandaNo) + " - " + fullname);
        values.put(PaymentDatabaseContract.MemberInfoEntry.COLUMN_MEMBER_CHANDANO, chandaNo);
        values.put(PaymentDatabaseContract.MemberInfoEntry.COLUMN_MEMBER_FULLNAME, fullname);

        String[] projection = {
                PaymentProviderContract.Members.COLUMN_MEMBER_JAMAATNAME,
                PaymentProviderContract.Members.COLUMN_MEMBER_CHANDANO
        };
        String selection = PaymentProviderContract.Members.COLUMN_MEMBER_CHANDANO + "=?";
        String[] selectionArgs = {String.valueOf(chandaNo)};
        Cursor cursor = mContext.getContentResolver().query(PaymentProviderContract.Members.CONTENT_URI,
                projection, selection,selectionArgs,null);
        if(cursor.moveToNext()){
            mContext.getContentResolver().update(PaymentProviderContract.Members.CONTENT_URI,
                    values, selection, selectionArgs);
        } else {
            mContext.getContentResolver().insert(PaymentProviderContract.Members.CONTENT_URI, values);
        }
        cursor.close();
    }
}
