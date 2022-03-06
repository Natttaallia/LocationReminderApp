package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
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
    fun insertIntoDB() = runBlockingTest {
        database.reminderDao().saveReminder(data)
        assertThat(database.reminderDao().getReminders().contains(data)).isTrue()
    }

    @Test
    fun deleteFromDB() = runBlockingTest {
        database.reminderDao().saveReminder(data)
        database.reminderDao().deleteAllReminders()
        assertThat(database.reminderDao().getReminders()).isEmpty()
    }

    @Test
    fun getFromDB() = runBlockingTest {
        database.reminderDao().saveReminder(data)

        val reminder = database.reminderDao().getReminderById(data.id)

        assertThat(reminder).isNotNull()
        assertThat(reminder?.title).isEqualTo(data.title)
    }

}