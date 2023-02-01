package com.femco.oxxo.reciboentiendaproveedores.di

import android.content.Context
import androidx.room.Room
import com.femco.oxxo.reciboentiendaproveedores.data.database.ReciboEnTiendaDataBase
import com.femco.oxxo.reciboentiendaproveedores.data.repository.SKURepositoryImpl
import com.femco.oxxo.reciboentiendaproveedores.domain.repository.SKURepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            ReciboEnTiendaDataBase::class.java,
            ReciboEnTiendaDataBase.RECIBO_EN_TIENDA_DB_NAME
        ).build()

    @Provides
    @Singleton
    fun provideSKURepository(db: ReciboEnTiendaDataBase): SKURepository {
        return SKURepositoryImpl(db.getReciboEnTiendaDao)
    }

}