package com.hsdroid.openinappassignment.data.models

data class Data(
    val favourite_links: List<Any>,
    val overall_url_chart: Map<String, Int>,
    val recent_links: List<RecentLink>,
    val top_links: List<TopLink>
)