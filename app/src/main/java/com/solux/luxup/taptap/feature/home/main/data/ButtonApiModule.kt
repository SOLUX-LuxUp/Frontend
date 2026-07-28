package com.solux.luxup.taptap.feature.home.main.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ButtonApiModule {

    @Provides
    @Singleton
    fun provideButtonApi(retrofit: Retrofit): ButtonApi = retrofit.create(ButtonApi::class.java)
}