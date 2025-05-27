package controllers

import database.Todos
import database.TodoRepositoryImpl
import gateways.Todo
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import usecases.TodoService

@Serializable
data class TodoResponse(val id: Int, val title: String, val completed: Boolean)

fun Todo.toResponse() = TodoResponse(id = this.id, title = this.title, completed = this.completed)

fun Application.module() {
  install(ContentNegotiation) {
    json()
  }
  install(StatusPages) {
    exception<Throwable> { call, cause ->
      call.respond(HttpStatusCode.InternalServerError, cause.localizedMessage)
    }
  }

  Database.connect("jdbc:sqlite:todos.db", driver = "org.sqlite.JDBC")
  transaction {
    SchemaUtils.create(Todos)
  }

  val repo = TodoRepositoryImpl()
  val service = TodoService(repo)

  routing {
    route("/todos") {
      get {
        call.respond(service.listTodos())
      }
      post {
        val request = call.receive<Map<String, String>>()
        val todo = service.addTodo(request["title"] ?: "")
        call.respond(HttpStatusCode.Created, todo.toResponse())
      }
      get("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        id?.let {
          service.getTodo(it)?.let { todo ->
            call.respond(todo.toResponse())
          } ?: call.respond(HttpStatusCode.NotFound)
        } ?: call.respond(HttpStatusCode.BadRequest)
      }
      put("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        val request = call.receive<Map<String, Boolean>>()
        val completed = request["completed"] ?: false
        id?.let {
          if (service.completeTodo(it, completed))
            call.respond(HttpStatusCode.OK)
          else
            call.respond(HttpStatusCode.NotFound)
        } ?: call.respond(HttpStatusCode.BadRequest)
      }
      delete("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        id?.let {
          if (service.removeTodo(it))
            call.respond(HttpStatusCode.OK)
          else
            call.respond(HttpStatusCode.NotFound)
        } ?: call.respond(HttpStatusCode.BadRequest)
      }
    }
  }
}

fun main() {
  embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}
