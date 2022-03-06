package com.udacity.project4.locationreminders.savereminder

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import androidx.test.espresso.assertion.ViewAssertions.matches


/**
 * @author Kulbaka Nataly
 * @date 06.03.2022
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class SaveReminderFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SaveReminderViewModel
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
            single {
                SaveReminderViewModel(
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

        viewModel = GlobalContext.get().koin.get()
    }

    @Test
    fun clickSaveWithEmptyLocation_showErrorMsg() {
        val navController = mock(NavController::class.java)
        val scenario =
            launchFragmentInContainer<SaveReminderFragment>(Bundle.EMPTY, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.reminderTitle)).perform(typeText("Test Title"))
        onView(withId(R.id.reminderDescription)).perform(typeText("Test Description"))
        closeSoftKeyboard()

        onView(withId(R.id.saveReminder)).perform(click())

        onView(withId(R.id.snackbar_text))
            .check(matches(withText(R.string.err_select_location)))
    }

    @Test
    fun clickSaveWithValidData_showSuccessMsg() {
        val navController = mock(NavController::class.java)
        val scenario =
            launchFragmentInContainer<SaveReminderFragment>(Bundle.EMPTY, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.reminderTitle)).perform(typeText("Test Title"))
        onView(withId(R.id.reminderDescription)).perform(typeText("Test Description"))
        closeSoftKeyboard()
        viewModel.selectedPOI.postValue(PointOfInterest(LatLng(0.0, 0.0), "Test Location", null))
        viewModel.reminderSelectedLocationStr.postValue("Test Location")

        onView(withId(R.id.saveReminder)).perform(click())

        assertThat(viewModel.showToast.value).isEqualTo("Reminder Saved !")
    }

}