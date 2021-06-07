/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package kt.json.server

import io.ktor.server.testing.*
import kotlin.test.*

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*

class AppTest {
    @Test
    fun testAppHasAGreeting() {
        val classUnderTest = App()
        assertNotNull(classUnderTest.greeting, "app should have a greeting")
    }

    @Test
    fun testRequests() =
        withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Get, "/posts")) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
            with(handleRequest(HttpMethod.Get, "/comments")) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
            with(handleRequest(HttpMethod.Get, "/profiles")) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
            with(handleRequest(HttpMethod.Get, "/health")) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
}
