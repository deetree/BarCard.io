package com.pl.cards.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pl.cards.R
import com.pl.cards.dao.CardDao
import com.pl.cards.dao.StoreDao
import com.pl.cards.helper.StoresTemplate
import com.pl.cards.model.Card
import com.pl.cards.model.Store
import java.util.concurrent.Executors

@Database(entities = [Card::class, Store::class], version = 1, exportSchema = false)
abstract class CardsDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    abstract fun storeDao(): StoreDao

    companion object {
        @Volatile
        private var INSTANCE: CardsDatabase? = null

        fun getInstance(context: Context): CardsDatabase {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = createInstance(context)
                }
                return INSTANCE!!
            }
        }

        private fun createInstance(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CardsDatabase::class.java,
                "cards_database.db"
            )
                .build()
    }
}
