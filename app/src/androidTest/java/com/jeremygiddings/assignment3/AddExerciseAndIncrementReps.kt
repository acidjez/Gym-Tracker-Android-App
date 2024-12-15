package com.jeremygiddings.assignment3


import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AddExerciseAndIncrementReps {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun addExerciseAndIncrementReps() {
        onView(
            allOf(
                withId(R.id.button),
                childAtPosition(
                    allOf(
                        withId(R.id.exercise_item_layout),
                        childAtPosition(
                            withId(R.id.recycler_exercises),
                            0
                        )
                    ),
                    0))).perform(click())

        mActivityScenarioRule.scenario.onActivity { activity ->
            val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_current_workout)
        }


        onView(
            allOf(
                withId(R.id.button_reps_plus), withText("+"),
                childAtPosition(
                    allOf(
                        withId(R.id.repsConstraintLayout),
                        childAtPosition(
                            withId(R.id.current_exercise_item_layout),
                            3
                        )
                    ),
                    0))).perform(click())

        onView(
            allOf(
                withId(R.id.editTextNumber), withText("1"),
                withParent(
                    allOf(
                        withId(R.id.repsConstraintLayout),
                        withParent(withId(R.id.current_exercise_item_layout))
                    )))).check(matches(withText("1")))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
