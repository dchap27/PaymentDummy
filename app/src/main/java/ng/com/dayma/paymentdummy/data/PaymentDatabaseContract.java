package ng.com.dayma.paymentdummy.data;

import android.provider.BaseColumns;

public final class PaymentDatabaseContract {
    private PaymentDatabaseContract () {} // make non-creatable

    public static final class MemberInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "member_info";

        public static final String COLUMN_MEMBER_JAMAATNAME = "jamaat_name";
        public static final String COLUMN_MEMBER_ID = "member_id";
        public static final String COLUMN_MEMBER_CHANDANO = "ChandaNo";
        public static final String COLUMN_MEMBER_FULLNAME = "Fullname";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_MEMBER_JAMAATNAME + " TEXT NOT NULL, " +
                        COLUMN_MEMBER_ID + " TEXT, " +
                        COLUMN_MEMBER_CHANDANO + " INTEGER UNIQUE NOT NULL, " +
                        COLUMN_MEMBER_FULLNAME + " TEXT NOT NULL)";

        // CREATE INDEX member_info_index1 ON member_info (columns)
        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                        "(" + COLUMN_MEMBER_JAMAATNAME + "," +
                        COLUMN_MEMBER_ID + "," +
                        COLUMN_MEMBER_CHANDANO + ")";

        // Get table qualified name
        public static final String getQName(String columnName){
            return TABLE_NAME + "." + columnName;
        }
    }

    public static final class MonthInfoEntry implements BaseColumns {

        public static final String TABLE_NAME = "month_info";

        public static final String COLUMN_MONTH_ID = "month_id";
//        public static final String COLUMN_MONTH_TITLE = "month_title";

        // CREATE TABLE
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_MONTH_ID + " TEXT NOT NULL)";

        // CREATE INDEX member_info_index1 ON member_info (columns)
        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                        "(" + COLUMN_MONTH_ID + ")";

        public static final String getQName(String columnName){
            return TABLE_NAME + "." + columnName;
        }

    }

    public static final class ScheduleInfoEntry implements BaseColumns {

        public static final String TABLE_NAME = "schedule_info";

        public static final String COLUMN_SCHEDULE_ID = "schedule_id";
        public static final String COLUMN_SCHEDULE_TITLE = "schedule_title";
        public static final String COLUMN_MEMBER_JAMAATNAME = "jamaat_name";
        public static final String COLUMN_SCHEDULE_ISCOMPLETE = "completion_status";
        public static final String COLUMN_MONTH_ID = "month_id";
        public static final String COLUMN_SCHEDULE_TOTALAMOUNT = "total_amount";
        public static final String COLUMN_SCHEDULE_TOTALPAYERS = "total_payers";

        // CREATE TABLE
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_SCHEDULE_ID + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_SCHEDULE_TITLE + " TEXT NOT NULL, " +
                        COLUMN_MEMBER_JAMAATNAME + " TEXT NOT NULL, " +
                        COLUMN_SCHEDULE_ISCOMPLETE + " INTEGER DEFAULT 0, " +
                        COLUMN_SCHEDULE_TOTALAMOUNT + " REAL DEFAULT 0, " +
                        COLUMN_SCHEDULE_TOTALPAYERS + " INTEGER DEFAULT 0, " +
                        COLUMN_MONTH_ID + " TEXT NOT NULL)";

        // CREATE INDEX member_info_index1 ON member_info (columns)
        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                        "(" + COLUMN_SCHEDULE_ID + "," + COLUMN_SCHEDULE_TOTALAMOUNT + "," +
                        COLUMN_SCHEDULE_TOTALPAYERS + "," + COLUMN_MONTH_ID + ")";

        public static final String getQName(String columnName){
            return TABLE_NAME + "." + columnName;
        }

    }

    public static final class PaymentInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "payment_info";

        public static final String COLUMN_SCHEDULE_ID = "schedule_id";
        public static final String COLUMN_MEMBER_CHANDANO = "ChandaNo";
        public static final String COLUMN_MEMBER_FULLNAME = "Fullname";
        public static final String COLUMN_PAYMENT_LOCALRECEIPT = "LocalReceipt";
        public static final String COLUMN_PAYMENT_MONTHPAID = "MonthPaidFor";
        public static final String COLUMN_PAYMENT_CHANDAAM = "Chanda Aam";
        public static final String COLUMN_PAYMENT_CHANDAWASIYYAT = "Chanda Wasiyyat";
        public static final String COLUMN_PAYMENT_JALSASALANA = "Jalsa Salana";
        public static final String COLUMN_PAYMENT_TARIKIJADID = "Tariki Jadid";
        public static final String COLUMN_PAYMENT_WAQFIJADID = "Waqfi Jadid";
        public static final String COLUMN_PAYMENT_WELFAREFUND = "Welfare Fund";
        public static final String COLUMN_PAYMENT_SCHOLARSHIP = "Scholarship";
        public static final String COLUMN_PAYMENT_MARYAMFUND = "Maryam Fund";
        public static final String COLUMN_PAYMENT_TABLIGH = "Tabligh";
        public static final String COLUMN_PAYMENT_ZAKAT = "Zakat";
        public static final String COLUMN_PAYMENT_SADAKAT = "Sadakat";
        public static final String COLUMN_PAYMENT_FITRANA = "Fitrana";
        public static final String COLUMN_PAYMENT_MOSQUEDONATION = "Moaque Donation";
        public static final String COLUMN_PAYMENT_MTA = "MTA";
        public static final String COLUMN_PAYMENT_CENTINARYKHILAFAT = "Centinary Khilafat";
        public static final String COLUMN_PAYMENT_WASIYYATHISSANJAIDAD = "Wasiyyat_Hissan_Jaidad";
        public static final String COLUMN_PAYMENT_MISCELLANEOUS = "Miscellaneous";
        public static final String COLUMN_PAYMENT_SUBTOTAL = "SubTotal";

        // CREATE TABLE
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_SCHEDULE_ID + " TEXT NOT NULL, " +
                        COLUMN_MEMBER_CHANDANO + " INTEGER NOT NULL, " +
                        COLUMN_MEMBER_FULLNAME + " TEXT, " +
                        COLUMN_PAYMENT_LOCALRECEIPT + " TEXT NOT NULL, " +
                        COLUMN_PAYMENT_MONTHPAID + " TEXT, " +
                        COLUMN_PAYMENT_CHANDAAM + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_CHANDAWASIYYAT + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_JALSASALANA + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_TARIKIJADID + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_WAQFIJADID + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_WELFAREFUND + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_SCHOLARSHIP + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_MARYAMFUND + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_TABLIGH + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_ZAKAT + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_SADAKAT + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_FITRANA + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_MOSQUEDONATION + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_MTA + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_CENTINARYKHILAFAT + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_WASIYYATHISSANJAIDAD + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_MISCELLANEOUS + " REAL DEFAULT 0.0, " +
                        COLUMN_PAYMENT_SUBTOTAL + " REAL DEFAULT 0.0)";

        // CREATE INDEX member_info_index1 ON member_info (columns)
        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                        "(" + COLUMN_SCHEDULE_ID + "," +
                        COLUMN_MEMBER_CHANDANO + ")";

        public static final String getQName(String columnName){
            return TABLE_NAME + "." + columnName;
        }

    }


}
