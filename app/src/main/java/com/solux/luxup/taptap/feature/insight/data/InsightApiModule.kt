package com.solux.luxup.taptap.feature.insight.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InsightApiModule {

    @Provides
    @Singleton
    fun provideInsightApi(retrofit: Retrofit): InsightApi = retrofit.create(InsightApi::class.java)
}