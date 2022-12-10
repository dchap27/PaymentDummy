package ng.com.dayma.paymentdummy.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ng.com.dayma.paymentdummy.data.CSVWriter;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract;
import ng.com.dayma.paymentdummy.data.PaymentProviderContract;
import ng.com.dayma.paymentdummy.data.ReadCSV;

public class CsvUtility {
    private final String chandaPay = "ChandaPay";
    private Context mContext;

    public CsvUtility(Context context){
        mContext = context;

    }

    public void readCSVToDatabase(String jamaatName, InputStream inputStream){
        // InputStream inputstream = mContext.getResources().openRawResource(R.raw.schedule_felele);
        ReadCSV csvFile = new ReadCSV(inputStream);
        List<String[]> jamaatInfo = csvFile.read();
        for(int i=1; i<jamaatInfo.size(); i++){
            // start from i=1 to avoid insert of the heading
            insertMember(jamaatName, Integer.parseInt(jamaatInfo.get(i)[0]), jamaatInfo.get(i)[1]);
        }
        Log.d("CSVUtility", "Done reading file!");
    }

    public boolean writeDatabaseToCSV(String fileName, String scheduleId, String monthId, Uri filedirUri){

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DocumentFile dfile = DocumentFile.fromTreeUri(mContext, filedirUri);
            Uri newFileUri = null;
            String mimeType = "text/csv";
            String dirname = dfile.getName();
            // Check if directory is "ChandaPay" and confirm the hierarchy "ChandaPay/Month/Newfile
            if( dirname.equals(chandaPay)){
                DocumentFile monthdir = dfile.findFile(monthId);
                if(monthdir != null){
                    // Create the newfile
                    newFileUri = createANewCsvFile(fileName,mimeType,monthdir);
                } else {
                    newFileUri = createMonthFolderWithNewFile(fileName,monthId,mimeType,dfile);
                }
                return writeCsv(newFileUri, projection, selection, selectionArgs);
            }
            DocumentFile chandaPayDir = dfile.findFile(chandaPay);
            // Check if the directory does not contain "ChandaPay"
            // nor has a direct parent of "ChandaPay", nor is its name is "ChandaPay"
            // Then create the hierachy of ChandaPay/Month/Filename
            if(chandaPayDir == null && dfile.getName() != chandaPay){
                newFileUri = createMonthFolderWithNewFile(fileName, monthId, mimeType, dfile.createDirectory(chandaPay));
            } else if (chandaPayDir.exists()){
                // if "ChandaPay" exist, check for the month otherwise create Month/newFile
                DocumentFile monthDir = chandaPayDir.findFile(monthId);
                if(monthDir == null){
                    newFileUri = createMonthFolderWithNewFile(fileName, monthId, mimeType, chandaPayDir);
                } else {
                    // if Month Directory already exist, check for the File and delete old if found
                    newFileUri = createANewCsvFile(fileName, mimeType, monthDir);
                }
            }
            if(newFileUri != null)
                return writeCsv(newFileUri, projection, selection, selectionArgs);
            else return false;
        }
        else {

            File exportDir = new File(Environment.getExternalStorageDirectory(), "/ChandaPay/" + monthId);
            if (!exportDir.exists()) {
                Log.d("MainActivity-CSVUtility", "creating folder....");
                exportDir.mkdirs();
            }
            File file = new File(exportDir, fileName + ".csv");
            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                extractDatatoWrite(csvWrite, projection, selection, selectionArgs);
                csvWrite.close();
                Log.d("MainActivity-CSVUtility", "Database exported successfully");
                return true;
            } catch (Exception sqlEx) {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
                return false;
            }
        }
    }

    private Uri createANewCsvFile(String fileName, String mimeType, DocumentFile monthDir) {
        Uri newFileUri;
        DocumentFile file = monthDir.findFile(fileName + ".csv"); // Add .csv extension to match exact file
        if(file != null && file.exists()){
            file.delete();
        }
        DocumentFile newFile = monthDir.createFile(mimeType, fileName);
        newFileUri = newFile.getUri();
        return newFileUri;
    }

    private Uri createMonthFolderWithNewFile(String fileName, String monthId, String mimeType, DocumentFile result) {
        Uri newFileUri;
        DocumentFile dir = result.createDirectory(monthId);
        DocumentFile newFile = dir.createFile(mimeType, fileName);
        newFileUri = newFile.getUri();
        return newFileUri;
    }

    private void extractDatatoWrite(CSVWriter csvWrite, String[] projection,
                                    String selection, String[] selectionArgs) {
        Cursor curCSV = mContext.getContentResolver().query(PaymentProviderContract.Payments.CONTENT_URI,
                projection, selection, selectionArgs, null);
        String[] columnNames = changeColumnNamesToContainSpaces(curCSV.getColumnNames());
        csvWrite.writeNext(columnNames);
        while (curCSV.moveToNext()) {
            //Which column you want to export
            String arrStr[] = new String[curCSV.getColumnCount()];
            for (int i = 0; i < curCSV.getColumnCount() - 1; i++)
                arrStr[i] = curCSV.getString(i);
            csvWrite.writeNext(arrStr);
            Log.d("MainActivity-CSVUtility", "write new row successfully");
        }
        curCSV.close();
    }

    private boolean writeCsv(Uri uri, String[] projection,
                          String selection, String[] selectionArgs) {
        try {
            ParcelFileDescriptor pfd = mContext.getContentResolver().
                    openFileDescriptor(uri,"w");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            CSVWriter csvWriter = new CSVWriter(fileOutputStream);
            extractDatatoWrite(csvWriter, projection, selection, selectionArgs);
            // Let the document provider know you're done by closing the stream.
            csvWriter.close();
            fileOutputStream.close();
            pfd.close();
            Log.d("MainActivity-CSVUtility", "Database exported successfully");
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
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
