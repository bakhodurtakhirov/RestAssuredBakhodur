import POJOClasses.ToDo;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Practice {

    /**
     * Task 1
     * write a request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * Convert Into POJO
     */

    @Test
    void task1() {

        ToDo toDo = given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .statusCode(200)
                .extract().as(ToDo.class);

        System.out.println("toDo = " + toDo);

    }

    /**
     * Task 2
     * send a get request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * expect content type JSON
     * expect title in response body to be "quis ut nam facilis et officia qui"
     */

    @Test
    void task2() {
        //Version 1
//        given()
//                .when()
//                .get("https://jsonplaceholder.typicode.com/todos/2")
//                .then()
//                .statusCode(200)
//                .contentType(ContentType.JSON)
//                .body("title",equalTo("quis ut nam facilis et officia qui"));
        
        //Version 2
//        String title = given()
//                .when()
//                .get("https://jsonplaceholder.typicode.com/todos/2")
//                .then()
//                .statusCode(200)
//                .contentType(ContentType.JSON)
//                .extract().path("title");
//
//        Assert.assertEquals(title, "quis ut nam facilis et officia qui");
        
        //Version 3
        ToDo toDo = given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(ToDo.class);
        
        Assert.assertEquals(toDo.getTitle(), "quis ut nam facilis et officia qui");
    }
    
    /**
     * Task 3
     * create a get request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * expect content type JSON
     * expect response completed status to be false
     */
    @Test
    void task3() {
        given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("completed", equalTo(false)); //v1 without POJO
        
        
        ToDo toDo = given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(ToDo.class);
        
        Assert.assertFalse(toDo.isCompleted()); //v2 with POJO
        
        boolean completed = given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().path("completed"); //v3 with extract.path
        
        Assert.assertFalse(completed);
    }
    
}
