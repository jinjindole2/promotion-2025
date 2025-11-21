    package com.jinjindole2.dailypromotion.promotion.adapter.`in`.web.dto

    data class PaginatedRes<T> (
        val totalPages : Long,
        val totalCount : Long,
        val currentPage : Long,
        val pageSize : Long,
        val list : List<T>?
    )