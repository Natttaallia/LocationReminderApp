package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.robolectric.annotation.Config
import android.os.Build


@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel

    @Before
    fun createViewModelWithDataSource() {
        stopKoin()

        dataSource = FakeDataSource()
        viewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            dataSource
        )
    }

    @Test
    fun loadData_showRemindersList() = runBlockingTest {
        dataSource.deleteAllReminders()
        val reminder = ReminderDTO(
            "Reminder Test",
            "Reminder description",
            "Test location",
            0.0,
            0.0)
        dataSource.saveReminder(reminder)

        viewModel.loadReminders()

        assertThat(viewModel.showLoading.value, `is`(false))
        assertThat(viewModel.showNoData.value, `is`(false))
        assertThat(viewModel.remindersList?.value?.isNotEmpty(), `is`(true))
    }

    @Test
    fun loadUnavailableData_showErrorMsg() = runBlockingTest {
        dataSource.setReturnError(true)
        viewModel.loadReminders()
        assertThat(viewModel.showSnackBar.value, `is`("Data not found"))
    }

    @Test
    fun dataLoading_showLoadingIsTrue() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()
        assertThat(viewModel.showLoading.value, `is`(true))
    }
}