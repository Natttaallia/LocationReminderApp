package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import org.hamcrest.CoreMatchers.not
import kotlinx.coroutines.test.runBlockingTest

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: ReminderDataSource
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun registerResources(): Unit = IdlingRegistry.getInstance().run {
        register(dataBindingIdlingResource)
    }

    @After
    fun unregisterResources(): Unit = IdlingRegistry.getInstance().run {
        unregister(dataBindingIdlingResource)
    }

    @Before
    fun setupAppModule() {
        stopKoin()

        val appModule = module {
            viewModel {
                RemindersListViewModel(
                    ApplicationProvider.getApplicationContext(),
                    get() as ReminderDataSource
                )
            }

            single<ReminderDataSource> { RemindersLocalRepository(get()) }
            single { LocalDB.createRemindersDao(ApplicationProvider.getApplicationContext()) }
        }

        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(listOf(appModule))
        }

        repository = GlobalContext.get().koin.get()
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @Test
    fun show_reminder_data() = runBlockingTest {
        val reminder = ReminderDTO(
            "Reminder Test",
            "Reminder description",
            "Test location",
            0.0,
            0.0)
        runBlocking {
            repository.saveReminder(reminder)
        }
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)

        onView(withText(reminder.title)).check(matches(isDisplayed()))
        onView(withText(reminder.description)).check(matches(isDisplayed()))
        onView(withText(reminder.location)).check(matches(isDisplayed()))
        onView(withId(R.id.noDataTextView)).check(matches(not(isDisplayed())))
    }

    @Test
    fun show_no_data() = runBlockingTest {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnAddReminder_navigatesToSaveReminderFragment() {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        dataBindingIdlingResource.monitorFragment(scenario)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }
}