package com.solux.luxup.taptap.feature.team.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TeamApiModule {

    @Provides
    @Singleton
    fun provideTeamApi(retrofit: Retrofit): TeamApi = retrofit.create(TeamApi::class.java)
}
