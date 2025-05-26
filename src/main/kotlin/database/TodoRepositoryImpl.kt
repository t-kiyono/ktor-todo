package database

import gateways.Todo
import gateways.TodoRepository
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Todos : IntIdTable() {
  val title = varchar("title", 255)
  val completed = bool("completed")
}

class TodoRepositoryImpl : TodoRepository {
  override fun getAll(): List<Todo> = transaction {
    Todos.selectAll().map {
      Todo(it[Todos.id].value, it[Todos.title], it[Todos.completed])
    }
  }

  override fun getById(id: Int): Todo? = transaction {
    Todos.select { Todos.id eq id }.mapNotNull {
      Todo(it[Todos.id].value, it[Todos.title], it[Todos.completed])
    }.singleOrNull()
  }

  override fun create(title: String): Todo = transaction {
    val id = Todos.insertAndGetId {
      it[Todos.title] = title
      it[Todos.completed] = false
    }.value
    Todo(id, title, false)
  }

  override fun update(id: Int, completed: Boolean): Boolean = transaction {
    Todos.update({ Todos.id eq id }) {
      it[Todos.completed] = completed
    } > 0
  }

  override fun delete(id: Int): Boolean = transaction {
    Todos.deleteWhere { Todos.id eq id } > 0
  }
}
