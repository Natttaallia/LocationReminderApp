package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var data: MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource {

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Data not found", 404)
        } else {
            return Result.Success(ArrayList(data))
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        data.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error("Data not found", 404)
        } else {
            val reminder = data.find { it.id == id }
            if (reminder != null) {
                return Result.Success(reminder)
            } else {
                return Result.Error("Data not found", 404)
            }
        }
    }

    override suspend fun deleteAllReminders() {
        data.clear()
    }


}