package dev.castive.jmp.util

import dev.castive.javalin_auth.auth.Roles
import dev.castive.jmp.api.Socket
import dev.castive.jmp.auth.AccessManager
import dev.castive.jmp.db.dao.Role
import dev.castive.jmp.db.dao.User
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SizedIterable
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList

fun SizedIterable<User>.add(user: User): SizedIterable<User> {
	val newUsers = ArrayList<User>()
	newUsers.addAll(this)
	newUsers.add(user)
	return SizedCollection(newUsers)
}
fun SizedIterable<User>.remove(user: User): SizedIterable<User> {
	val newUsers = ArrayList<User>()
	newUsers.addAll(this)
	newUsers.remove(user)
	return SizedCollection(newUsers)
}

/**
 * Convert a pair into a FSA-compliant payload for sending over a WebSocket
 */
fun Pair<String, Any?>.forSocket(error: Boolean = false, meta: Any? = null) = Socket.Payload(first, second, error, meta)

/**
 * Get the user which has been stored in the context
 * May be null
 */
fun Context.user(): User? = this.attribute(AccessManager.attributeUser)
/**
 * Converts a String to being URL-safe
 */
fun String.safe(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)

/**
 * Converts a String to being a UUID
 * @return UUID or null if the String cannot be parsed into a valid UUID
 */
fun String.toUUID(): UUID? = runCatching { UUID.fromString(this) }.getOrNull()

/**
 * Checks whether a String is null, blank or only includes ECMAScript (JS) nullable types
 */
fun String?.isESNullOrBlank(): Boolean = isNullOrBlank() || this == "null" || this == "undefined"

fun Context.ok(): Context = this.status(HttpStatus.OK_200)

/**
 * Checks whether a DaoRole name is equal to a BasicRole name
 */
fun Role.isEqual(role: Roles.BasicRoles): Boolean = name.equals(role.name, ignoreCase = true)

/**
 * Convert a List to kotlin.collections.ArrayList
 */
fun <T> List<T>.asArrayList(): ArrayList<T> = ArrayList(this)

fun String.asEnv(default: String = "") = EnvUtil.getEnv(this, default)