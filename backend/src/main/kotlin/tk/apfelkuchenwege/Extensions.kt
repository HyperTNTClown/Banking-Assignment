package tk.apfelkuchenwege

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.security.MessageDigest
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun String.sha256(): ByteArray {
	return MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
}

@OptIn(ExperimentalEncodingApi::class)
fun ByteArray.base64(): String {
	return Base64.encode(this)
}

suspend fun ApplicationCall.respondJson(response: JsonObject, statusCode: HttpStatusCode) {
	this.respondText(response.toString(), status = statusCode, contentType = ContentType.Application.Json)
}

suspend fun ApplicationCall.respondJson(response: JsonObject) {
	this.respondJson(response, HttpStatusCode.OK)
}

suspend fun ApplicationCall.receiveJson(): JsonObject? {
	val text = this.receive<String>()
	return JsonParser.parseString(text).asJsonObject
}
