package com.duke.orca.android.kotlin.simpledonelist.di.modules

import android.content.Context
import com.duke.orca.android.kotlin.simpledonelist.donelist.datasource.local.DoneListDatasource
import com.duke.orca.android.kotlin.simpledonelist.donelist.datasource.local.DoneListDatasourceImpl
import com.duke.orca.android.kotlin.simpledonelist.donelist.repository.DoneListRepository
import com.duke.orca.android.kotlin.simpledonelist.donelist.repository.DoneListRepositoryImpl
import com.duke.orca.android.kotlin.simpledonelist.history.datasource.local.HistoryDatasource
import com.duke.orca.android.kotlin.simpledonelist.history.datasource.local.HistoryDatasourceImpl
import com.duke.orca.android.kotlin.simpledonelist.history.repository.HistoryRepository
import com.duke.orca.android.kotlin.simpledonelist.history.repository.HistoryRepositoryImpl
import com.duke.orca.android.kotlin.simpledonelist.persistence.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DoneListModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return AppDatabase.getInstance(applicationContext)
    }

    @Singleton
    @Provides
    fun provideDoneListDatasource(database: AppDatabase): DoneListDatasource {
        return DoneListDatasourceImpl(database)
    }

    @Singleton
    @Provides
    fun provideDoneListRepository(datasource: DoneListDatasource): DoneListRepository {
        return DoneListRepositoryImpl(datasource)
    }

    @Singleton
    @Provides
    fun provideHistoryDatasource(database: AppDatabase): HistoryDatasource {
        return HistoryDatasourceImpl(database)
    }

    @Singleton
    @Provides
    fun provideHistoryRepository(datasource: HistoryDatasource): HistoryRepository {
        return HistoryRepositoryImpl(datasource)
    }
}