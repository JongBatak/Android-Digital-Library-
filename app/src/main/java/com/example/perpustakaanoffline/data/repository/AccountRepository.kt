package com.example.perpustakaanoffline.data.repository

import com.example.perpustakaanoffline.data.db.AccountDao
import com.example.perpustakaanoffline.data.db.AccountEntity
import kotlinx.coroutines.flow.Flow

class AccountRepository(private val dao: AccountDao) {
    fun observeAccounts(): Flow<List<AccountEntity>> = dao.observeAccounts()

    suspend fun upsert(account: AccountEntity): Long = dao.upsert(account)

    suspend fun remove(id: Long) = dao.deleteById(id)

    suspend fun findByUsername(username: String): AccountEntity? = dao.findByUsername(username)
}

