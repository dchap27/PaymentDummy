package ng.com.dayma.paymentdummy;

import android.app.LoaderManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.List;

import ng.com.dayma.paymentdummy.MyViewModels.ScheduleListViewModel;
import ng.com.dayma.paymentdummy.data.PaymentOpenHelper;
import ng.com.dayma.paymentdummy.data.PaymentProviderContract;
import ng.com.dayma.paymentdummy.touchhelpers.ScheduleClickAdapterListener;

public class ScheduleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ScheduleClickAdapterListener {

    public static final String MONTH_ID = "ng.com.dayma.paymentdummy.MONTH_ID";
    public static final int ID_NOT_SET = -1;
    public static final int LOADER_SCHEDULES = 0;
    private List<MonthInfo> mMonths;
    private List<ScheduleInfo> mSchedules;
    private ArrayAdapter<ScheduleInfo> mAdaptermonths;
    private RecyclerView mRecyclerItems;
    private ScheduleRecyclerAdapter mMonthSchedulesAdapter;
    private String mMonID;
    private PaymentOpenHelper mDbOpenHelper;
    private ActionMode mActionMode;
    private ActionModecallbacks mActionModecallbacks;
    private final String TAG = getClass().getSimpleName();
    private GridLayoutManager mSchedulesLayoutManager;
    private ScheduleListViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "*********** onCreate ************");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new PaymentOpenHelper(this);
        mViewModel = ViewModelProviders.of(this).get(ScheduleListViewModel.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleListActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });

        initializeDisplayContent();
        if(mViewModel.isNewlyCreated && savedInstanceState != null){
            mViewModel.restoreState(savedInstanceState);
        }
        mViewModel.isNewlyCreated = false;
        mViewModel.monthId = mMonID;
        displaySchedules();
        mActionModecallbacks = new ActionModecallbacks();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null){
            mViewModel.saveState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "***********OnResume************");
        getLoaderManager().restartLoader(LOADER_SCHEDULES, null, this);
        super.onResume();
    }

    private void loadScheduleData() {
        Uri uri = PaymentProviderContract.Schedules.CONTENT_URI;
        final String[] scheduleColumns = {
                PaymentProviderContract.Schedules.COLUMN_MONTH_ID,
                PaymentProviderContract.Schedules._ID,
                PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME,
                PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ISCOMPLETE,
                PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TITLE,
                PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TOTALAMOUNT,
                PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TOTALPAYERS,
                PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID,

        };
        String scheduleOrder = PaymentProviderContract.Schedules.COLUMN_MONTH_ID + "," +
                PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME;

        CursorLoader loader = new CursorLoader(this, uri, scheduleColumns, null, null, scheduleOrder);
    }

    private void initializeDisplayContent() {

        Intent intent = getIntent();
        mMonID = intent.getStringExtra(MONTH_ID);
        MonthInfo month = DataManager.getInstance().getMonth(mMonID);

        mRecyclerItems = (RecyclerView) findViewById(R.id.list_monthschedules);
        mMonthSchedulesAdapter = new ScheduleRecyclerAdapter(this, null);

        mSchedulesLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.schedule_grid_span));

    }

    private void displaySchedules(){
        getLoaderManager().initLoader(LOADER_SCHEDULES, null, this);
        mRecyclerItems.setLayoutManager(mSchedulesLayoutManager);
        mMonthSchedulesAdapter.setClickAdapter(this);

        mRecyclerItems.setAdapter(mMonthSchedulesAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_SCHEDULES){
            Uri uri = PaymentProviderContract.Schedules.CONTENT_URI;
            final String[] scheduleColumns = {
                    PaymentProviderContract.Schedules.COLUMN_MONTH_ID,
                    PaymentProviderContract.Schedules._ID,
                    PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME,
                    PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ISCOMPLETE,
                    PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TITLE,
                    PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TOTALAMOUNT,
                    PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TOTALPAYERS,
                    PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID,

            };
            String selection = PaymentProviderContract.Schedules.COLUMN_MONTH_ID + "=?";
            String[] selectionArgs = { mMonID };
            String scheduleOrder = PaymentProviderContract.Schedules.COLUMN_MONTH_ID + "," +
                    PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME;

            loader = new CursorLoader(this, uri, scheduleColumns, selection, selectionArgs, scheduleOrder);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_SCHEDULES){
            mMonthSchedulesAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_SCHEDULES)
            mMonthSchedulesAdapter.changeCursor(null);
    }

    @Override
    public void onItemClicked(int adapterPosition, long cursorDataId) {
        enableActionMode(adapterPosition, (int) cursorDataId);
    }

    @Override
    public boolean onSingleClick() {
        if (mActionMode != null){
            return true;
        }
        return false;
    }

    private void enableActionMode(int position, int cursorIdPos) {
        if(mActionMode == null ){
            mActionMode = startSupportActionMode(mActionModecallbacks);
        }
        togglePosition(position,cursorIdPos);
    }

    private void togglePosition(int position, int cursorIdPos) {
        mMonthSchedulesAdapter.toggleSelection(position, cursorIdPos);
        int count = mMonthSchedulesAdapter.getSelectedItemCount();
        if(count == 0){
            mActionMode.finish();
            mActionMode = null;
        } else {
            mActionMode.setTitle((count) + " " + getString(R.string.selected_item_count));
            mActionMode.invalidate();// redraw the CAB
        }
    }

    // code for ActionMode
    private class ActionModecallbacks implements android.support.v7.view.ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            MenuItem edit_menu = menu.findItem(R.id.edit_schedule);
            if(mMonthSchedulesAdapter.getSelectedItemCount() == 1){
                edit_menu.setEnabled(true);
            } else {
                edit_menu.setEnabled(false);
            }
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
            switch (item.getItemId()){

                case R.id.delete_schedule:
                    Log.d(TAG, "Delete schedule menu selected");
                    deleteSchedule();
                    mode.finish();
                    return true;
                case R.id.edit_schedule:
                    Log.d(TAG, "Edit schedule menu selected");
                    editSchedule();
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
            mMonthSchedulesAdapter.clearSelections();
            mActionMode = null;
        }
    }

    private void deleteSchedule() {
        List selectedItemPositions =
                mMonthSchedulesAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mMonthSchedulesAdapter.removeData((Integer) selectedItemPositions.get(i));
        }
        mMonthSchedulesAdapter.notifyDataSetChanged();
        Log.d(TAG, "done deleting");
        mActionMode = null;
    }

    private void editSchedule() {
        List selectedItemPositions = mMonthSchedulesAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mMonthSchedulesAdapter.editData((Integer) selectedItemPositions.get(i));
        }
        mMonthSchedulesAdapter.notifyDataSetChanged();
        mActionMode = null;
    }
}
