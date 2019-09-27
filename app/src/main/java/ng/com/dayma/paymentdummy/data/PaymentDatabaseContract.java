package ng.com.dayma.paymentdummy.data;

import android.provider.BaseColumns;

public final class PaymentDatabaseContract {
    private PaymentDatabaseContract () {} // make non-creatable

    public static final class MemberInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "jamaat_info";

        public static final String COLUMN_MEMBER_JAMAATNAME = "jamaat_name";
        public static final String COLUMN_MEMBER_ID = "member_id";
        public static final String COLUMN_MEMBER_CHANDANO = "chanda_no";
        public static final String COLUMN_MEMBER_FULLNAME = "fullname";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_MEMBER_JAMAATNAME + " TEXT NOT NULL, " +
                        COLUMN_MEMBER_ID + " TEXT, " +
                        COLUMN_MEMBER_CHANDANO + " INTEGER UNIQUE NOT NULL, " +
                        COLUMN_MEMBER_FULLNAME + " TEXT NOT NULL)";
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

    }

    public static final class ScheduleInfoEntry implements BaseColumns {

        public static final String TABLE_NAME = "schedule_info";

        public static final String COLUMN_SCHEDULE_ID = "schedule_id";
        public static final String COLUMN_SCHEDULE_TITLE = "schedule_title";
        public static final String COLUMN_SCHEDULE_JAMAAT = "jamaat_name";
        public static final String COLUMN_SCHEDULE_IS_COMPLETE = "completion_status";
        public static final String COLUMN_MONTH_ID = "month_id";

        // CREATE TABLE
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_SCHEDULE_ID + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_SCHEDULE_TITLE + " TEXT NOT NULL, " +
                        COLUMN_SCHEDULE_JAMAAT + " TEXT NOT NULL, " +
                        COLUMN_SCHEDULE_IS_COMPLETE + " INTEGER DEFAULT 0, " +
                        COLUMN_MONTH_ID + " TEXT NOT NULL)";

    }

    public static final class PaymentInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "payment_info";

        public static final String COLUMN_SCHEDULE_ID = "schedule_id";
        public static final String COLUMN_MEMBER_CHANDANO = "ChandaNo";
        public static final String COLUMN_MEMBER_FULLNAME = "Fullname";
        public static final String COLUMN_PAYMENT_LOCALRECEIPT = "LocalReceipt";
        public static final String COLUMN_PAYMENT_MONTHPAID = "MonthPaidFor";
        public static final String COLUMN_PAYMENT_CHANDAAM = "ChandaAam";
        public static final String COLUMN_PAYMENT_CHANDAWASIYYAT = "Chanda_Wasiyyat";
        public static final String COLUMN_PAYMENT_JALSASALANA = "Jalsa_Salana";
        public static final String COLUMN_PAYMENT_TARIKIJADID = "Tariki_Jadid";
        public static final String COLUMN_PAYMENT_WAQFIJADID = "Waqfi_Jadid";
        public static final String COLUMN_PAYMENT_WELFAREFUND = "Welfare_fund";
        public static final String COLUMN_PAYMENT_SCHOLARSHIP = "Scholarhip";
        public static final String COLUMN_PAYMENT_MARYAMFUND = "Maryam_fund";
        public static final String COLUMN_PAYMENT_TABLIGH = "Tabligh";
        public static final String COLUMN_PAYMENT_ZAKAT = "Zakat";
        public static final String COLUMN_PAYMENT_SADAKAT = "Sadakat";
        public static final String COLUMN_PAYMENT_FITRANA = "Fitrana";
        public static final String COLUMN_PAYMENT_MOSQUEDONATION = "Mosque_donation";
        public static final String COLUMN_PAYMENT_MTA = "Mta";
        public static final String COLUMN_PAYMENT_CENTINARYKHILAFAT = "Centinary_khilafat";
        public static final String COLUMN_PAYMENT_WASIYYATHISSANJAIDAD = "Wasiyyat_hissan_jaidad";
        public static final String COLUMN_PAYMENT_MISCELLANEOUS = "Miscellaneous";
        public static final String COLUMN_PAYMENT_SUBTOTAL = "Subtotal";

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

    }

}
