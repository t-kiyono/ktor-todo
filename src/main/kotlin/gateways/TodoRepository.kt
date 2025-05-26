package gateways

import kotlinx.serialization.Serializable

@Serializable
data class Todo(val id: Int, val title: String, val completed: Boolean)

interface TodoRepository {
  fun getAll(): List<Todo>
  fun getById(id: Int): Todo?
  fun create(title: String): Todo
  fun update(id: Int, completed: Boolean): Boolean
  fun delete(id: Int): Boolean
}
