package jp.ac.titech.cs.se.refactorhub.app.infrastructure.auth

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.Authentication
import io.ktor.auth.OAuthAccessTokenResponse
import io.ktor.auth.OAuthServerSettings
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.oauth
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.location
import io.ktor.locations.locations
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import io.ktor.util.KtorExperimentalAPI
import jp.ac.titech.cs.se.refactorhub.app.model.User
import jp.ac.titech.cs.se.refactorhub.app.usecase.service.UserService
import org.kohsuke.github.GitHub
import org.koin.core.component.KoinApiExtension
import org.koin.java.KoinJavaComponent.inject

private const val GITHUB = "github"

@KtorExperimentalLocationsAPI
@Location("/login")
private class Login

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun Authentication.Configuration.github() {
    oauth(GITHUB) {
        client = HttpClient(Apache)
        providerLookup = {
            OAuthServerSettings.OAuth2ServerSettings(
                name = GITHUB,
                authorizeUrl = "https://github.com/login/oauth/authorize",
                accessTokenUrl = "https://github.com/login/oauth/access_token",
                clientId = application.environment.config.property("ktor.oauth.clientId").getString(),
                clientSecret = application.environment.config.property("ktor.oauth.clientSecret").getString()
            )
        }
        urlProvider = {
            redirectUrl(Login())
        }
    }
}

@KtorExperimentalLocationsAPI
private fun <T : Any> ApplicationCall.redirectUrl(t: T): String {
    val host = request.host()
    val path = application.locations.href(t)
    return when (host) {
        "localhost" -> "http://$host${request.port().let { if (it == 80) "" else ":$it" }}$path"
        else -> "https://$host$path"
    }
}

@KoinApiExtension
@KtorExperimentalLocationsAPI
fun Route.login() {
    authenticate(GITHUB) {
        location<Login> {
            handle {
                val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
                if (principal != null) {
                    val me = getMe(principal.accessToken)
                    call.sessions.set(
                        Session(
                            me.id.toString(),
                            principal.accessToken
                        )
                    )
                    call.respondRedirect("/")
                } else {
                    // TODO
                    call.respondText("Failed")
                }
            }
        }
    }
}

@KoinApiExtension
private fun getMe(accessToken: String): User {
    val userService by inject(UserService::class.java)
    val me = GitHub.connectUsingOAuth(accessToken).myself
    return userService.createIfNotExist(me.id.toInt(), me.login)
}
