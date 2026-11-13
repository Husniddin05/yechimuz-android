package uz.yechim.app.core.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import uz.yechim.app.core.security.KeystoreManager

class SessionCookieJar(private val keystoreManager: KeystoreManager) : CookieJar {

    private val cookieStore = mutableMapOf<String, List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host] = cookies
        for (cookie in cookies) {
            if (cookie.name == "yechimuz_token") {
                keystoreManager.saveToken(cookie.value)
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = cookieStore[url.host]?.toMutableList() ?: mutableListOf()
        val token = keystoreManager.getToken()
        if (token != null && cookies.none { it.name == "yechimuz_token" }) {
            cookies.add(
                Cookie.Builder()
                    .name("yechimuz_token")
                    .value(token)
                    .domain(url.host)
                    .path("/")
                    .secure()
                    .httpOnly()
                    .build()
            )
        }
        return cookies
    }
}
