import controllers.TodoResponse
import controllers.module
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication

class ApplicationTest : StringSpec({

  "POST /todos で TODO を追加し GET /todos で取得できる" {
    testApplication {
      application {
        module()
      }

      val client = createClient {
        install(ContentNegotiation) {
          json()
        }
      }

      val postResponse = client.post("/todos") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToString(mapOf("title" to "テストAPI")))
      }
      postResponse.status shouldBe HttpStatusCode.Created

      val createdTodo = Json.decodeFromString<TodoResponse>(postResponse.bodyAsText())
      createdTodo.title shouldBe "テストAPI"
      createdTodo.completed shouldBe false

      val getResponse = client.get("/todos")
      getResponse.status shouldBe HttpStatusCode.OK

      val todos = Json.decodeFromString<List<TodoResponse>>(getResponse.bodyAsText())
      todos.any { it.title == "テストAPI" } shouldBe true
    }
  }

  "GET /todos/{id} で個別に取得できる" {
    testApplication {
      application {
        module()
      }

      val client = createClient {
        install(ContentNegotiation) {
          json()
        }
      }

      val postResponse = client.post("/todos") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToString(mapOf("title" to "詳細確認")))
      }
      val created = Json.decodeFromString<TodoResponse>(postResponse.bodyAsText())
      val id = created.id

      val getResponse = client.get("/todos/$id")
      getResponse.status shouldBe HttpStatusCode.OK

      val todo = Json.decodeFromString<TodoResponse>(getResponse.bodyAsText())
      todo.title shouldBe "詳細確認"
    }
  }

  "PUT /todos/{id} で完了状態を更新できる" {
    testApplication {
      application {
        module()
      }

      val client = createClient {
        install(ContentNegotiation) {
          json()
        }
      }

      val postResponse = client.post("/todos") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToString(mapOf("title" to "更新対象")))
      }
      val created = Json.decodeFromString<TodoResponse>(postResponse.bodyAsText())

      val putResponse = client.put("/todos/${created.id}") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToString(mapOf("completed" to true)))
      }
      putResponse.status shouldBe HttpStatusCode.OK

      val getResponse = client.get("/todos/${created.id}")
      val updated = Json.decodeFromString<TodoResponse>(getResponse.bodyAsText())
      updated.completed shouldBe true
    }
  }

  "DELETE /todos/{id} で TODO を削除できる" {
    testApplication {
      application {
        module()
      }

      val client = createClient {
        install(ContentNegotiation) {
          json()
        }
      }

      val postResponse = client.post("/todos") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToString(mapOf("title" to "削除対象")))
      }
      val created = Json.decodeFromString<TodoResponse>(postResponse.bodyAsText())

      val deleteResponse = client.delete("/todos/${created.id}")
      deleteResponse.status shouldBe HttpStatusCode.OK

      val getResponse = client.get("/todos/${created.id}")
      getResponse.status shouldBe HttpStatusCode.NotFound
    }
  }
})
