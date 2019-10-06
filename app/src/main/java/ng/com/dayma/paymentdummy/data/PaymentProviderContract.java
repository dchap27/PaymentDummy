package ng.com.dayma.paymentdummy.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class PaymentProviderContract {

    private PaymentProviderContract () {}

    public static final String AUTHORITY = "ng.com.dayma.paymentdummy.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    // create columns common among the tables
    protected interface MembersCommonColumns {
        public static final String COLUMN_MEMBER_CHANDANO = "chanda_no";
        public static final String COLUMN_MEMBER_FULLNAME = "fullname";
    }

    protected interface SchedulesIdColumns {
        public static final String COLUMN_SCHEDULE_ID = "schedule_id";
    }
    /*
    Create the column constants for the tables and using protected interfaces
     */
    protected interface MembersColumns {
        public static final String COLUMN_MEMBER_JAMAATNAME = "jamaat_name";
        public static final String COLUMN_MEMBER_ID = "member_id";
    }

    protected interface MonthsColumns {
        public static final String COLUMN_MONTH_ID = "month_id";
    }

    protected interface SchedulesColumns {

        public static final String COLUMN_SCHEDULE_TITLE = "schedule_title";
        public static final String COLUMN_SCHEDULE_JAMAAT = "jamaat_name";
        public static final String COLUMN_SCHEDULE_ISCOMPLETE = "completion_status";
        public static final String COLUMN_SCHEDULE_TOTALAMOUNT = "total_amount";
        public static final String COLUMN_SCHEDULE_TOTALPAYERS = "total_payers";
    }

    protected interface PaymentsColumns {

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
    }



    // create the Months table:  the BaseColumns contains the _ID column interface
    public static final class Months implements BaseColumns, MonthsColumns {
        // create path for the table
        public static final String PATH = "months";
        // content://ng.com.dayma.paymentdummy.provider/months
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Members implements BaseColumns, MembersColumns, MembersCommonColumns {
        // create path for the table
        public static final String PATH = "members";
        // content://ng.com.dayma.paymentdummy.provider/members
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Schedules implements BaseColumns, SchedulesColumns,
            MonthsColumns, SchedulesIdColumns {
        // create path for the table
        public static final String PATH = "schedules";
        // content://ng.com.dayma.paymentdummy.provider/schedules
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Payments implements BaseColumns, PaymentsColumns,
            MembersCommonColumns, SchedulesIdColumns {
        // create path for the table
        public static final String PATH = "payments";
        // content://ng.com.dayma.paymentdummy.provider/payments
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

}
