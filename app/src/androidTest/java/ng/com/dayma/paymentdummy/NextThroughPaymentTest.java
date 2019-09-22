package ng.com.dayma.paymentdummy;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

/**
 * Created by Ahmad on 8/4/2019.
 */
public class NextThroughPaymentTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule(MainActivity.class);

    @Test
    public void NextThroughPaymentTest() {

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        // select the schedule from the nav_view
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_schedules));

        // select the first item from the RecycleView
        onView(withId(R.id.list_schedules)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // get the first schedule
        List<ScheduleInfo> schedules = DataManager.getInstance().getSchedules();
        int index = 0;
        ScheduleInfo schedule = schedules.get(index);

        // select the first item from the payment recyclerview
        onView(withId(R.id.list_payments)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // test for the payment if it has the expected values
        /*
        the loop was introduce after checking that the test passed the index  = 0 parameter
        Now check through all note using menu item 'next'
         */
        List<PaymentInfo> payments = DataManager.getInstance().getPayments(schedule);
        for(int i = 0; i < payments.size(); i++) {
            PaymentInfo payment = payments.get(i);

            onView(withId(R.id.text_payer_title)).check(
                    matches(withText(payment.getChandaNo()))
            );
            onView(withId(R.id.spinner_monthpaid)).check(matches(withSpinnerText(payment.getMonthPaid())));
            onView(withId(R.id.text_receiptno)).check(matches(withText(containsString(String.valueOf(payment.getReceiptNo())))));
            onView(withId(R.id.text_chandaam)).check(matches(withText(containsString(String.valueOf(payment.getChandaAm())))));
            onView(withId(R.id.text_wasiyyat)).check(matches(withText(containsString(String.valueOf(payment.getWasiyyat())))));
            onView(withId(R.id.text_tahrik)).check(matches(withText(containsString(String.valueOf(payment.getTahrikJadid())))));
            onView(withId(R.id.text_waqf)).check(matches(withText(containsString(String.valueOf(payment.getWaqfJadid())))));

            // next get the view for the 'next' menu item
            // ensure the item remain enabled until the very last item
            if(i < payments.size() - 1)
                onView(allOf(withId(R.id.action_next), isEnabled())).perform(click());
        }
        // check if the last 'next' item is disabled
        onView(withId(R.id.action_next)).check(matches(not(isEnabled())));
        pressBack();

    }

}