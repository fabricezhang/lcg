package top.easelink.lcg.network

import org.jsoup.Connection
import org.jsoup.nodes.Document

interface ApiRequest {

    fun sendGetRequestWithQuery(query: String): Document?

    fun sendGetRequestWithUrl(url: String): Document?

    fun sendPostRequestWithUrl(url: String, form: MutableMap<String, String>?): Connection.Response
}