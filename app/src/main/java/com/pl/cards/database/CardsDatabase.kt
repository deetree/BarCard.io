package com.pl.cards.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pl.cards.dao.CardDao
import com.pl.cards.dao.StoreDao
import com.pl.cards.helper.EncryptionHelper
import com.pl.cards.model.Card
import com.pl.cards.model.Store
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

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

        private fun getSupportFactory(ctx: Context): SupportFactory =
            SupportFactory(
                SQLiteDatabase.getBytes(
                    EncryptionHelper().getDbEncryptionKey(
                        "application_alias"
                    ).toCharArray()
                )
            )

        private fun createInstance(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CardsDatabase::class.java,
                "cards_database.db"
            )
                .openHelperFactory(getSupportFactory(context))
                .build()
    }


}
