package com.example.data

import kotlinx.coroutines.flow.Flow

class EntryRepository(private val entryDao: EntryDao) {
    val allEntries: Flow<List<Entry>> = entryDao.getAllEntries()

    suspend fun insert(entry: Entry) {
        entryDao.insertEntry(entry)
    }

    suspend fun delete(entry: Entry) {
        entryDao.deleteEntry(entry)
    }

    suspend fun deleteById(id: Long) {
        entryDao.deleteEntryById(id)
    }
}
