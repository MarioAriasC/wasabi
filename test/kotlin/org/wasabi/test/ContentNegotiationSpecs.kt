package org.wasabi.test



import org.junit.Test as spec
import org.wasabi.test.get
import kotlin.test.assertEquals
import org.wasabi.http.ContentType
import org.wasabi.routing.InterceptOn
import org.wasabi.test.TestServer
import org.wasabi.interceptors.negotiateContent
import org.wasabi.http.with


public class ContentNegotiationSpecs : TestServerContext() {

    spec fun sending_an_object_should_encode_and_send_based_on_contentType() {

        TestServer.reset()
        TestServer.appServer.negotiateContent()

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Connection" to "keep-alive",
                "Cache-Control" to "max-age=0",
                "Accept" to "application/json",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.3"

        )

        TestServer.appServer.get("/customer/10", {

            val obj = object {
                val name = "Joe"
                val email = "Joe@smith.com"

            }

            response.send(obj)

        })

        val response = get("http://localhost:3000/customer/10", headers)

        assertEquals("{\"name\":\"Joe\",\"email\":\"Joe@smith.com\"}",response.body)





    }

    spec fun manual_negotiation_should_execute_correct_body_structure_and_serialize_if_necessary() {

        TestServer.reset()
        TestServer.appServer.negotiateContent()

        val headers = hashMapOf(
                "User-Agent" to "test-client",
                "Connection" to "keep-alive",
                "Cache-Control" to "max-age=0",
                "Accept" to "application/json",
                "Accept-Encoding" to "gzip,deflate,sdch",
                "Accept-Language" to "en-US,en;q=0.8",
                "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.3"

        )

        TestServer.appServer.get("/customer/10", {


            val obj = object {
                val name = "Joe"
                val email = "Joe@smith.com"

            }

            response.negotiate(
                    "text/html" with { send ("this is not the response you're looking for")},
                    "application/json" with { send(obj) }
            )

        })

        val response = get("http://localhost:3000/customer/10", headers)

        assertEquals("{\"name\":\"Joe\",\"email\":\"Joe@smith.com\"}",response.body)





    }

}