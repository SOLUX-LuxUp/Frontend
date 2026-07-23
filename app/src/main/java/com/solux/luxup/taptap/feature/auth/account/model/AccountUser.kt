package com.solux.luxup.taptap.feature.auth.account.model

data class AccountUser(
    val nickname: String,
    val email: String,
    val profileImageUrl: String? = null,
)