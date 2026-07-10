package com.solux.luxup.taptap.feature.home.template.model

data class ChecklistCategory(
    val title: String,
    val items: List<String>
)

data class ChecklistTemplate(
    val id: String,
    val optionTitle: String,
    val description: String,
    val categories: List<ChecklistCategory>
)
