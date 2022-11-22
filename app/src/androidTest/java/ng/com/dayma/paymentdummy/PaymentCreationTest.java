package ng.com.dayma.paymentdummy;

import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Ahmad on 7/30/2019.
 */

@RunWith(AndroidJUnit4.class)
public class PaymentCreationTest {

    static DataManager sDataManager;

    @BeforeClass
    public static void classSetup() {
        sDataManager = DataManager.getInstance();
    }
    @Rule
    public ActivityTestRule<PaymentListActivity> mPaymentListActivityRule =
            new ActivityTestRule<>(PaymentListActivity.class);

    @Test
    public void createNewNote() {

    }

}