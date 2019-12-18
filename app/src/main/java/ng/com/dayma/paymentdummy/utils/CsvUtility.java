package ng.com.dayma.paymentdummy.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ng.com.dayma.paymentdummy.data.CSVWriter;
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

    public boolean writeDatabaseToCSV(String fileName, String scheduleId){

        final String[] projection = {
                PaymentProviderContract.Payments.COLUMN_MEMBER_CHANDANO,
                PaymentProviderContract.Payments.COLUMN_MEMBER_FULLNAME,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_LOCALRECEIPT,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_MONTHPAID,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_CHANDAAM,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_CHANDAWASIYYAT,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_JALSASALANA,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_TARIKIJADID,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_WAQFIJADID,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_WELFAREFUND,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_SCHOLARSHIP,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_MARYAMFUND,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_TABLIGH,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_ZAKAT,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_SADAKAT,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_FITRANA,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_MOSQUEDONATION,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_MTA,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_CENTINARYKHILAFAT,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_WASIYYATHISSANJAIDAD,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_MISCELLANEOUS,
                PaymentProviderContract.Payments.COLUMN_PAYMENT_SUBTOTAL
        };
        String selection = PaymentProviderContract.Payments.COLUMN_SCHEDULE_ID + "=?";
        String[] selectionArgs = { scheduleId };
        File exportDir = new File(Environment.getExternalStorageDirectory(), "/ChandaPay");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, fileName + ".csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            Cursor curCSV = mContext.getContentResolver().query(PaymentProviderContract.Payments.CONTENT_URI,
                    projection, selection, selectionArgs, null);
            String[] columnNames = changeColumnNamesToContainSpaces(curCSV.getColumnNames());
            csvWrite.writeNext(columnNames);
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = new String[curCSV.getColumnCount()];
                for (int i = 0; i < curCSV.getColumnCount() - 1; i++)
                    arrStr[i] = curCSV.getString(i);
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            Log.d("MainActivity-CSVUtility", "Database exported successfully");
            return true;
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            return false;
        }
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

    private static String[] changeColumnNamesToContainSpaces(String[] columnNames) {
        String[] oldColumnNames = Arrays.toString(columnNames)
                .replace("["," ")
                .replace("]", " ")
                .trim()
                .split(",");
        ArrayList<String> newColumnNames = new ArrayList<>();
        for(int i=0; i< oldColumnNames.length; i++){
            if(oldColumnNames[i].contains("_") && !(oldColumnNames[i].contains("Wasiyyat_Hissan_Jaidad"))){
                newColumnNames.add(oldColumnNames[i].replace("_", " ").trim());
            }else{
                newColumnNames.add(oldColumnNames[i].trim());
            }
        }
        String[] newNames = newColumnNames.toArray(new String[0]);
        return newNames;
    }
}
