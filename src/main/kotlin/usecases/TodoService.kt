package usecases

import gateways.TodoRepository

class TodoService(private val repo: TodoRepository) {
  fun listTodos() = repo.getAll()
  fun getTodo(id: Int) = repo.getById(id)
  fun addTodo(title: String) = repo.create(title)
  fun completeTodo(id: Int, completed: Boolean) = repo.update(id, completed)
  fun removeTodo(id: Int) = repo.delete(id)
}
