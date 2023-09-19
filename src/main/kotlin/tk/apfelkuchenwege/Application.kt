package tk.apfelkuchenwege

import com.sendgrid.SendGrid
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import tk.apfelkuchenwege.data.banking.Account
import tk.apfelkuchenwege.data.banking.AccountManager
import tk.apfelkuchenwege.handlers.MainAPIHandler
import tk.apfelkuchenwege.data.banking.BankAccountManager
import tk.apfelkuchenwege.handlers.configureRouting
import java.sql.*

private val manager = BankAccountManager()
private val accountManager = AccountManager()
public val sg = SendGrid(System.getenv("SENDGRID_API_KEY"))
val api = MainAPIHandler(manager, accountManager)
fun main() {
	embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
		.start(wait = true)
}

fun Application.module() {
	configureRouting()
	val dbConnection: Connection = connectToPostgres(embedded = true)
}


/**
 * Makes a connection to a Postgres database.
 *
 * In order to connect to your running Postgres process,
 * please specify the following parameters in your configuration file:
 * - postgres.url -- Url of your running database process.
 * - postgres.user -- Username for database connection
 * - postgres.password -- Password for database connection
 *
 * If you don't have a database process running yet, you may need to [download]((https://www.postgresql.org/download/))
 * and install Postgres and follow the instructions [here](https://postgresapp.com/).
 * Then, you would be able to edit your url,  which is usually "jdbc:postgresql://host:port/database", as well as
 * user and password values.
 *
 *
 * @param embedded -- if [true] defaults to an embedded database for tests that runs locally in the same process.
 * In this case you don't have to provide any parameters in configuration file, and you don't have to run a process.
 *
 * @return [Connection] that represent connection to the database. Please, don't forget to close this connection when
 * your application shuts down by calling [Connection.close]
 * */
fun Application.connectToPostgres(embedded: Boolean): Connection {
	Class.forName("org.postgresql.Driver")
	if (embedded) {
		return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "root", "")
	} else {
		val url = environment.config.property(System.getenv("postgres.url")).getString()
		val user = environment.config.property(System.getenv("postgres.user")).getString()
		val password = environment.config.property(System.getenv("postgres.password")).getString()

		return DriverManager.getConnection(url, user, password)
	}
}
