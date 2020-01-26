package top.easelink.lcg.network

import org.jsoup.nodes.Document

interface ApiRequest {

    fun sendGetRequestWithQuery(query: String): Document?

    fun sendGetRequestWithUrl(url: String): Document?
}