package tk.apfelkuchenwege

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
