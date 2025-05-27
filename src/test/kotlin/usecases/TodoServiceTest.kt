import database.TodoRepositoryImpl
import database.Todos
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import usecases.TodoService

class TodoServiceTest : StringSpec({

  val repository = TodoRepositoryImpl()
  val service = TodoService(repository)

  beforeSpec {
    Database.connect("jdbc:sqlite:test.db", driver = "org.sqlite.JDBC")
    transaction {
      SchemaUtils.create(Todos)
    }
  }

  beforeTest {
    transaction {
      Todos.deleteAll()
    }
  }

  "新しいTODOを作成し取得できること" {
    val todo = service.addTodo("テストTODO")
    todo.title shouldBe "テストTODO"
    todo.completed shouldBe false

    val retrieved = service.getTodo(todo.id)
    retrieved shouldNotBe null
    retrieved!!.title shouldBe "テストTODO"
  }

  "TODOの完了状態を更新できること" {
    val todo = service.addTodo("完了テスト")
    service.completeTodo(todo.id, true) shouldBe true

    val updated = service.getTodo(todo.id)
    updated shouldNotBe null
    updated!!.completed shouldBe true
  }

  "TODOを削除できること" {
    val todo = service.addTodo("削除テスト")
    service.removeTodo(todo.id) shouldBe true

    val deleted = service.getTodo(todo.id)
    deleted shouldBe null
  }

  "全TODOを取得できること" {
    service.addTodo("タスク1")
    service.addTodo("タスク2")

    val all = service.listTodos()
    all.map { it.title } shouldContainExactlyInAnyOrder listOf("タスク1", "タスク2")
  }
})
