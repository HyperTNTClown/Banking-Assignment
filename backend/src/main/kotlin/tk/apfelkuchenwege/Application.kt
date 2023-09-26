package tk.apfelkuchenwege

import com.sendgrid.SendGrid
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import tk.apfelkuchenwege.data.banking.*
import tk.apfelkuchenwege.handlers.MainAPIHandler
import tk.apfelkuchenwege.handlers.configureRouting
import java.sql.*
import java.util.*

public val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
public var db: Database? = null
public val manager = BankAccountManager()
public val accountManager = AccountManager()
public val api = MainAPIHandler(manager, accountManager)

fun main() {
	embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
		.start(wait = true)
}

fun Application.module() {
	db = connectToPostgres()
	createTables()
	configureRouting(api)
}

fun createTables() {
	transaction {
		SchemaUtils.create(Transfers)
		SchemaUtils.addMissingColumnsStatements(Transfers).forEach {
			TransactionManager.current().exec(it)
		}
		SchemaUtils.create(Accounts)
		SchemaUtils.addMissingColumnsStatements(Accounts).forEach {
			TransactionManager.current().exec(it)
		}
		SchemaUtils.create(BankAccounts)
		SchemaUtils.addMissingColumnsStatements(BankAccounts).forEach {
			TransactionManager.current().exec(it)
		}
	}
}

fun connectToPostgres(): Database? {
	val url = System.getenv("PSQL_URL")
	val user = System.getenv("PSQL_USER")
	val password = System.getenv("PSQL_PASSWORD")

	return Database.connect(url, driver = "org.postgresql.Driver", user = user, password = password)
}
