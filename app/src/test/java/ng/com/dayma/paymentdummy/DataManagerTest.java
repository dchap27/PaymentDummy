package ng.com.dayma.paymentdummy;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ahmad on 7/30/2019.
 */
public class DataManagerTest {
    static DataManager sDataManager;

    @BeforeClass
    public static void classSetUp() throws Exception {
        sDataManager = DataManager.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        sDataManager.getPayments().clear();
        sDataManager.initializeExamplePayments();
    }
    @Test
    public void createNewNote() throws Exception {
        final MonthInfo month = sDataManager.getMonth("May 2019");
        final ScheduleInfo schedule = sDataManager.getSchedule("Schedule2 for schedule_kewulere");
        final int paymentId = 44114;
        final String monthPaid = "May";
        final String receiptno = "12534";
        final float chandaam = 2002;
        final float wasiyyat = 20000;
        final float tahrikJadid = 150;
        final float waqfJadid = 200;

        int paymentIndex = sDataManager.createNewPayment();
        PaymentInfo newPayment = sDataManager.getPayments().get(paymentIndex);
        newPayment.setSchedule(schedule);
        newPayment.setChandaNo(paymentId);
        newPayment.setMonthPaid(monthPaid);
        newPayment.setReceiptNo(receiptno);
        newPayment.setChandaAm(chandaam);
        newPayment.setWasiyyat(wasiyyat);
        newPayment.setTahrikJadid(tahrikJadid);
        newPayment.setWaqfJadid(waqfJadid);

        PaymentInfo comparePayment = sDataManager.getPayments().get(paymentIndex);
        assertEquals(schedule, comparePayment.getSchedule());
        assertEquals(monthPaid, comparePayment.getMonthPaid());
        assertEquals(receiptno, comparePayment.getReceiptNo());

    }

    @Test
    public void findSimilarNotes() throws Exception {
        final ScheduleInfo schedule = sDataManager.getSchedule("Schedule1 for schedule_kewulere");
        final int paymentId = 44114;
        final String monthPaid = "June";
        final String receiptno = "12980";
        final float chandaam = 20000;

        int paymentIndex1 = sDataManager.createNewPayment();
        PaymentInfo newPayment1 = sDataManager.getPayments().get(paymentIndex1);
        newPayment1.setSchedule(schedule);
        newPayment1.setChandaNo(paymentId);
        newPayment1.setMonthPaid(monthPaid);

        int paymentIndex2 = sDataManager.createNewPayment();
        PaymentInfo newPayment2 = sDataManager.getPayments().get(paymentIndex2);
        newPayment2.setSchedule(schedule);
        newPayment2.setChandaNo(paymentId);
        newPayment2.setReceiptNo(receiptno);

        int foundIndex1 = sDataManager.findPayment(newPayment1);
        assertEquals(paymentIndex1, foundIndex1);

        int foundIndex2 = sDataManager.findPayment(newPayment2);
        assertEquals(paymentIndex2, foundIndex2);
    }

}