package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDB() = database.close()

    private val data = ReminderDTO(
        "Reminder Test",
        "Reminder description",
        "Test location",
        0.0,
        0.0)

    @Test
    fun insertIntoDB() = runBlocking {
        repository.saveReminder(data)
        assertThat(repository.getReminders()).isInstanceOf(Result.Success::class.java)
        assertThat((repository.getReminders() as Result.Success).data).contains(data)
    }

    @Test
    fun deleteFromDB() = runBlocking {
        repository.saveReminder(data)
        repository.deleteAllReminders()
        assertThat((repository.getReminders() as Result.Success).data).isEmpty()
    }

    @Test
    fun getFromDB() = runBlocking {
        repository.saveReminder(data)

        val reminder = repository.getReminder(data.id) as Result.Success

        assertThat(reminder?.data).isNotNull()
        assertThat(reminder?.data?.title).isEqualTo(data.title)
    }

    @Test
    fun getFromDBWithError() = runBlocking {
        repository.deleteAllReminders()
        val reminder = repository.getReminder(data.id)

        assertThat(reminder).isEqualTo(Result.Error("Reminder not found!"))
    }

}