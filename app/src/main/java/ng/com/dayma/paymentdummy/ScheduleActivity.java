package ng.com.dayma.paymentdummy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract;

public class ScheduleActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private Spinner mSpinnerMonth;
    private Spinner mSpinnerYear;
    private SimpleCursorAdapter mAdapterJamaatList;
    private Spinner mSpinnerJamaat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mSpinnerMonth = (Spinner) findViewById(R.id.spinner_schedule_month);
        mSpinnerYear = (Spinner) findViewById(R.id.spinner_schedule_year);
        mSpinnerJamaat = (Spinner) findViewById(R.id.spinner_jamaat_name);

        Log.d(TAG,"Populate the Months into spinner");
        ArrayList<String> months = DataManager.getInstance().getMonthsOnly();
        ArrayAdapter<String> adapterMonths =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        adapterMonths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMonth.setAdapter(adapterMonths);

        Log.d(TAG,"Populate the years into spinner");
        ArrayList<String> years = DataManager.getInstance().getYearsOnly();
        ArrayAdapter<String> adapterYears =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        adapterYears.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerYear.setAdapter(adapterYears);

        Log.d(TAG,"Populate the jamaats into spinner");
        mAdapterJamaatList = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[] {PaymentDatabaseContract.MemberInfoEntry.COLUMN_MEMBER_JAMAATNAME},
                new int[] {android.R.id.text1}, 0);

        mAdapterJamaatList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerJamaat.setAdapter(mAdapterJamaatList);


    }

}
