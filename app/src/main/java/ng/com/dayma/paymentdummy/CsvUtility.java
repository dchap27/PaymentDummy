package ng.com.dayma.paymentdummy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.InputStream;
import java.util.List;

import ng.com.dayma.paymentdummy.data.DatabaseDataWorker;
import ng.com.dayma.paymentdummy.data.PaymentOpenHelper;
import ng.com.dayma.paymentdummy.data.ReadCSV;

public class CsvUtility {
    private Context mContext;

    public CsvUtility(Context context){
        mContext = context;

    }

    public void readCSVToDatabase(String jamaatName, InputStream inputStream){
        PaymentOpenHelper mDbOpenHelper = new PaymentOpenHelper(mContext);
        SQLiteDatabase mDb = mDbOpenHelper.getWritableDatabase();
        DatabaseDataWorker databaseDataWorker = new DatabaseDataWorker(mDb);
//        InputStream inputstream = mContext.getResources().openRawResource(R.raw.felele);
        ReadCSV csvFile = new ReadCSV(inputStream);
        List<String[]> jamaatInfo = csvFile.read();
        for(int i=1; i<jamaatInfo.size(); i++){
            // start from i=1 to avoid insert of the heading
            databaseDataWorker.insertMember(jamaatName, Integer.parseInt(jamaatInfo.get(i)[0]), jamaatInfo.get(i)[1]);
        }
        Log.d("CSVUtility", "Done reading file!");
    }
}
