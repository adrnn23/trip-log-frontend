package com.example.triplog.main.data

data class SearchFilters(
    val searchType: String = "Users",
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val sortingDirection: String? = "Ascending"
)
