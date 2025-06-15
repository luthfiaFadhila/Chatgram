package com.example.whatsapp;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);
    @Test
    public void testLoginAndNavigateBottomNav() throws InterruptedException {
        onView(withId(R.id.etEmail)).perform(typeText("fia@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("08122311"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.toolbarTitle)).check(matches(withText("Chatgram")));

        onView(withId(R.id.navigation_pembaruan)).perform(click());
        onView(withId(R.id.tvPembaruanTitle)).check(matches(isDisplayed()));

        onView(withId(R.id.navigation_komunitas)).perform(click());
        onView(withId(R.id.tvKomunitasTitle)).check(matches(isDisplayed()));

        onView(withId(R.id.navigation_panggilan)).perform(click());
        onView(withId(R.id.tvPanggilanTitle)).check(matches(isDisplayed()));

    }
}
