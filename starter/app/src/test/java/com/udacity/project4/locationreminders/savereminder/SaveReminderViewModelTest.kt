package com.udacity.project4.locationreminders.savereminder

import androidx.test.ext.junit.runners.AndroidJUnit4

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.stopKoin
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import kotlinx.coroutines.test.runBlockingTest
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem


@Config(sdk = [Build.VERSION_CODES.O_MR1])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel

    @Before
    fun createViewModelWithDataSource() {
        stopKoin()

        dataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            dataSource
        )
    }

    @Test
    fun saveReminder_ValidateAndSaveReturnsTrue() = mainCoroutineRule.runBlockingTest {
        val reminder = ReminderDataItem("Reminder Test",
            "Reminder description",
            "Test location",
            0.0,
            0.0)

        assertThat(viewModel.validateAndSaveReminder(reminder), `is`(true))
    }

    @Test
    fun saveReminderEmptyTitle_ValidateAndSaveReturnsFalse() = mainCoroutineRule.runBlockingTest {
        val reminder = ReminderDataItem("",
            "Reminder description",
            "Test location",
            0.0,
            0.0)

        assertThat(viewModel.validateAndSaveReminder(reminder), `is`(false))
    }

    @Test
    fun saveReminderEmptyLocation_ValidateAndSaveReturnsFalse() = mainCoroutineRule.runBlockingTest {
        val reminder = ReminderDataItem("Reminder Test",
            "Reminder description",
            "",
            0.0,
            0.0)

        assertThat(viewModel.validateAndSaveReminder(reminder), `is`(false))
    }


}