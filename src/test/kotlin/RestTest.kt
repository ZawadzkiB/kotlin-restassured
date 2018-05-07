import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.amshove.kluent.shouldBe
import org.apache.http.HttpStatus
import org.apache.http.HttpStatus.SC_OK
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Test

class RestTest {

    private val post = "{\n" +
            "    \"userId\": 1,\n" +
            "    \"id\": 3,\n" +
            "    \"title\": \"ea molestias quasi exercitationem repellat qui ipsa sit aut\",\n" +
            "    \"body\": \"et iusto sed quo iure\\nvoluptatem occaecati omnis eligendi aut ad\\nvoluptatem doloribus vel accusantium quis pariatur\\nmolestiae porro eius odio et labore et velit aut\"\n" +
            "  }"

    @Before
    fun config() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com/"
    }

    @Test
    fun shouldReturn200AndNotEmptyBodyWhenGetPosts() {
        given().basePath("/posts")
                .`when`().get()
                .then().statusCode(HttpStatus.SC_OK).and().body(
                        "userId", notNullValue(),
                        "title", notNullValue(),
                        "id", notNullValue(),
                        "body", notNullValue()).log().all()
    }

    @Test
    fun shouldReturn200AndGzipEncodingWhenGetPosts() {
        given().basePath("/posts")
                .`when`().get()
                .then().statusCode(HttpStatus.SC_OK).and().header("Content-Encoding", equalTo("gzip"))
    }

    @Test
    fun shouldReturn200AndArrayWithSize100WhenGetPosts() {
        given().basePath("/posts")
                .`when`().get()
                .then().statusCode(HttpStatus.SC_OK).and().body("size()", equalTo(100))
    }

    @Test
    fun shouldReturn200AndCommentsWithSameIdWhenGetComments() {
        given().basePath("/posts/1/comments")
                .`when`().get()
                .then().statusCode(HttpStatus.SC_OK).and().body("postId", hasItems(1))
    }

    @Test
    fun shouldCreatePost() {
        given().basePath("/posts").body(post)
                .`when`().post()
                .then().statusCode(HttpStatus.SC_CREATED).and().body("id", notNullValue())
    }

    @Test
    fun shouldDeletePost() {
        given().basePath("/posts/100")
                .`when`().delete()
                .then().statusCode(HttpStatus.SC_OK)
    }

    @Test
    fun shouldFindUserWithZipCode() {
        given().basePath("/users")
                .`when`().get()
                .then().statusCode(SC_OK).and().body("address.zipcode", hasItem("23505-1337"))
    }

    @Test
    fun shouldFindUserWithSiteEndWithOrg() {
        given().basePath("/users")
                .`when`().get()
                .then().statusCode(SC_OK).and()
                .extract().body().jsonPath().getList<String>("website").filter { it.endsWith(".org") }.size shouldBe 2
    }
}