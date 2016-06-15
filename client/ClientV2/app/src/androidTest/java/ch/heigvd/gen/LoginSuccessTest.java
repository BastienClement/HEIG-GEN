package ch.heigvd.gen;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.heigvd.gen.activities.LoginActivity;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>

public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
} */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginSuccessTest {

    private String mStringLoginToBetyped;
    private String mStringPasswordToBetyped;



    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);
/*
    @Rule
    public IntentsTestRule<LoginActivity> mActivityRule = new IntentsTestRule(LoginActivity.class);
*/
    @Before
    public void initValidString() {
        // Specify valid strings for login.
        mStringLoginToBetyped = "holla";
        mStringPasswordToBetyped = "password";
    }

    @Test
    public void TrivialTest() {
        // Type login text and then press the button.
        onView(withId(R.id.login))
                .perform(typeText(mStringLoginToBetyped), closeSoftKeyboard());

        onView(withId(R.id.password))
                .perform(typeText(mStringPasswordToBetyped), closeSoftKeyboard());

        //onView(withId(R.id.login_button)).perform(click());

        onView(withId(R.id.login_button)).check(matches(withText("Login")));




        //Click the login button
        //onView(withId(R.id.login_button)).perform(click());

        //Verify that the MainActivity is launched
        //To achieve that, we verify that the pager view, showing the fragments tabs, is displayed
        //onView(withId(R.id.result_textview)).check(matches(withText("LoggedIn")));

        //intended(toPackage("ch.heigvd.gen.activities.MainActivity"));
    }

}
