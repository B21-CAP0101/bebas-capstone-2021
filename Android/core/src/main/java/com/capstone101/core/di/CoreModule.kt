package com.capstone101.core.di

import androidx.room.Room
import com.capstone101.core.data.Repositories
import com.capstone101.core.data.db.DBGetData
import com.capstone101.core.data.db.room.UserDB
import com.capstone101.core.data.network.NetworkGetData
import com.capstone101.core.domain.repositories.IRepositories
import com.capstone101.core.utils.SessionManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        val passphrase: ByteArray = SQLiteDatabase.getBytes("Bebas B21-0101".toCharArray())
        val factory = SupportFactory(passphrase)
        Room.databaseBuilder(
            androidContext(), UserDB::class.java, "User DB"
        ).fallbackToDestructiveMigration().openHelperFactory(factory).build()
    }
    single { get<UserDB>().getDao() }
}

val firebaseModule = module {
    single { Firebase.firestore }
    single { Firebase.storage }
}

val repositoriesModule = module {
    single { DBGetData(get()) }
    single { NetworkGetData(get(), get()) }
    single<IRepositories> { Repositories(get(), get()) }
}

val sessionModule = module {
    factory { SessionManager(androidContext()) }
}
