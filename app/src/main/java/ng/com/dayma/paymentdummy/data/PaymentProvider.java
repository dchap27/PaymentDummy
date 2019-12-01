package ng.com.dayma.paymentdummy.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.MemberInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.MonthInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.PaymentInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.ScheduleInfoEntry;
import ng.com.dayma.paymentdummy.data.PaymentProviderContract.Payments;

public class PaymentProvider extends ContentProvider {

    private PaymentOpenHelper mDbOpenHelper;

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);// any attempt to pass a URI
    // with no AUTHORITY OR a PATH return value no_match

    public static final int MEMBERS = 100;

    public static final int MEMBERS_ROW = 101;

    public static final int MONTHS_ROW = 201;

    public static final int MONTHS = 200;

    public static final int SCHEDULES = 300;

    public static final int SCHEDULES_ROW = 301;

    public static final int PAYMENTS = 400;

    public static final int PAYMENTS_ROW = 401;

    // Add static initializer to initialise the URIs
    static {
        sUriMatcher.addURI(PaymentProviderContract.AUTHORITY, PaymentProviderContract.Members.PATH, MEMBERS);
        sUriMatcher.addURI(PaymentProviderContract.AUTHORITY, PaymentProviderContract.Members.PATH + "/#", MEMBERS_ROW);

        sUriMatcher.addURI(PaymentProviderContract.AUTHORITY, PaymentProviderContract.Months.PATH, MONTHS);
        sUriMatcher.addURI(PaymentProviderContract.AUTHORITY, PaymentProviderContract.Months.PATH + "/#", MONTHS_ROW);

        sUriMatcher.addURI(PaymentProviderContract.AUTHORITY, PaymentProviderContract.Schedules.PATH, SCHEDULES);
        sUriMatcher.addURI(PaymentProviderContract.AUTHORITY, PaymentProviderContract.Schedules.PATH + "/#", SCHEDULES_ROW);

        sUriMatcher.addURI(PaymentProviderContract.AUTHORITY, Payments.PATH, PAYMENTS);
        sUriMatcher.addURI(PaymentProviderContract.AUTHORITY, Payments.PATH + "/#", PAYMENTS_ROW);

    }

    public PaymentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        long rowId = -1;
        String rowSelection = null;
        String[] rowSelectionArgs = null;
        int nRows = -1;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        int uriMatch = sUriMatcher.match(uri);

        switch (uriMatch) {
            case PAYMENTS_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = PaymentInfoEntry._ID + "=?";
                rowSelectionArgs = new String[] {Long.toString(rowId)};
                // returns the no of rows deleted
                nRows = db.delete(PaymentDatabaseContract.PaymentInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;

            case MONTHS_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = MonthInfoEntry._ID + "=?";
                rowSelectionArgs = new String[] {Long.toString(rowId)};
                nRows = db.delete(MonthInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;

            case SCHEDULES_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = ScheduleInfoEntry._ID + "=?";
                rowSelectionArgs = new String[] {Long.toString(rowId)};
                nRows = db.delete(ScheduleInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;

            case MEMBERS_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = MemberInfoEntry._ID + "=?";
                rowSelectionArgs = new String[] {Long.toString(rowId)};
                nRows = db.delete(MemberInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;

        }

        return nRows;

    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        long rowId = -1; // initialize the rowId
        Uri rowUri = null; // initialize the uri of the new row;
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case MEMBERS:
                // db.insert returns the rowid of the new inserted row
                rowId = db.insert(MemberInfoEntry.TABLE_NAME, null, values);
                // content://ng.com.dayma.notekeeper.provider/notes/1
                rowUri = ContentUris.withAppendedId(PaymentProviderContract.Members.CONTENT_URI, rowId);
                break;

            case PAYMENTS:
                // db.insert returns the rowid of the new inserted row
                rowId = db.insert(PaymentInfoEntry.TABLE_NAME, null, values);
                // content://ng.com.dayma.notekeeper.provider/notes/1
                rowUri = ContentUris.withAppendedId(Payments.CONTENT_URI, rowId);
                break;

            case MONTHS:
                rowId = db.insert(MonthInfoEntry.TABLE_NAME, null, values);
                rowUri = ContentUris.withAppendedId(PaymentProviderContract.Months.CONTENT_URI, rowId);
                break;

            case SCHEDULES:
                rowId = db.insert(ScheduleInfoEntry.TABLE_NAME, null, values);
                rowUri = ContentUris.withAppendedId(PaymentProviderContract.Schedules.CONTENT_URI, rowId);
                break;
        }
        return rowUri;
    }

    @Override
    public boolean onCreate() {
        mDbOpenHelper = new PaymentOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch){
            case MEMBERS:
                cursor = db.query(MemberInfoEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                // set a Notification for a change in data
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case MONTHS:
                cursor = db.query(MonthInfoEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case SCHEDULES:
                cursor = db.query(ScheduleInfoEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case PAYMENTS:
                cursor = db.query(PaymentInfoEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case MEMBERS_ROW:
                long rowId = ContentUris.parseId(uri);
                String rowSelection = MemberInfoEntry._ID + "=?";
                String[] rowSelectionArgs = new String[]{Long.toString(rowId)};
                cursor = db.query(MemberInfoEntry.TABLE_NAME, projection,rowSelection,rowSelectionArgs,
                        null, null, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case MONTHS_ROW:
                long row_Id = ContentUris.parseId(uri);
                String row_Selection = MonthInfoEntry._ID + "=?";
                String[] row_SelectionArgs = new String[]{Long.toString(row_Id)};
                cursor = db.query(MonthInfoEntry.TABLE_NAME, projection,row_Selection,row_SelectionArgs,
                        null, null, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case SCHEDULES_ROW:
                long row_id = ContentUris.parseId(uri);
                String row_selection = ScheduleInfoEntry._ID + "=?";
                String[] row_selectionArgs = new String[] {Long.toString(row_id)};
                cursor = db.query(ScheduleInfoEntry.TABLE_NAME, projection, row_selection, row_selectionArgs,
                        null, null, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case PAYMENTS_ROW:
                long paymentId = ContentUris.parseId(uri);
                String paymentSelection = PaymentInfoEntry._ID + "=?";
                String[] paymentSelectionArgs = new String[] {Long.toString(paymentId)};
                cursor = db.query(PaymentInfoEntry.TABLE_NAME, projection, paymentSelection, paymentSelectionArgs,
                        null, null, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        long rowId = -1;
        String rowSelection = null;
        String[] rowSelectionArgs = null;
        int nRows = -1;
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch){
            case MEMBERS:
                nRows = db.update(MemberInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case MONTHS:
                nRows = db.update(MonthInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case SCHEDULES:
                nRows = db.update(ScheduleInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case MEMBERS_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = MemberInfoEntry._ID + "=?";
                rowSelectionArgs = new String[] {Long.toString(rowId)};
                nRows = db.update(MemberInfoEntry.TABLE_NAME, values, rowSelection, rowSelectionArgs);
                break;

            case MONTHS_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = MonthInfoEntry._ID + "=?";
                rowSelectionArgs = new String[] {Long.toString(rowId)};
                nRows = db.update(MonthInfoEntry.TABLE_NAME, values, rowSelection, rowSelectionArgs);
                break;

            case SCHEDULES_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = ScheduleInfoEntry._ID + "=?";
                rowSelectionArgs = new String[] {Long.toString(rowId)};
                nRows = db.update(ScheduleInfoEntry.TABLE_NAME, values, rowSelection, rowSelectionArgs);
                break;

            case PAYMENTS_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = PaymentInfoEntry._ID + "=?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.update(PaymentInfoEntry.TABLE_NAME, values, rowSelection, rowSelectionArgs);
                break;

        }
        return nRows;
    }
}
