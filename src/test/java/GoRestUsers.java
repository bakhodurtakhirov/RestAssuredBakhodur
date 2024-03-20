import POJOClasses.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class GoRestUsers {
    
    public String randomName() {
        return RandomStringUtils.randomAlphabetic(10);
    }
    
    public String randomEmail() {
        return RandomStringUtils.randomAlphanumeric(7) + "@techno.com";
    }
    
    
    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
    
    @BeforeClass
    public void setU() {
        baseURI = "https://gorest.co.in/public/v2/users";
        requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer 1b75a03b6459ee4bda00bd05aeae5a75bbb1c59bf272598e048784ed1a4695f0")
                .setContentType(ContentType.JSON)
                .build();
        
        responseSpecification = new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .expectContentType(ContentType.JSON)
                .build();
    }
    
    @Test(priority = 1)
    void getUsersList() {
        given()
                .when().get()//Since the entire url is baseURI we dont' need to use anything in the get()
                .then()
                .statusCode(200)
                .spec(responseSpecification)
                .body("", hasSize(10)); //if entire body is an array then just "" is enough
    }
    
    //    @Test(priority = 1)
//    void createUsersList() {
//        given().header("Authorization", "Bearer 1b75a03b6459ee4bda00bd05aeae5a75bbb1c59bf272598e048784ed1a4695f0")
//                .body("{\"name\":\"" + randomName() + "\",\"gender\":\"male\",\"email\":\"" + randomEmail() + "\",\"status\":\"active\"}")
//                .contentType(ContentType.JSON) //Content type is must
//                .when().post()//Since the entire url is baseURI we dont' need to use anything in the get()
//                .then()
//                .log().body()
//                .statusCode(201)
//                .contentType(ContentType.JSON);
///*        {
//            "name": "{{$randomFullName}}",
//                "gender": "male",
//                "email": "{{$randomEmail}}",
//                "status": "active"
//        }*/
//    }
//
    @Test(priority = 2)
    void createNewUserWithMaps() {
        Map<String, String> user = new HashMap<>(); //The key are always string. Object super class is necessary if values are of different type
        user.put("name", randomName());
        user.put("email", randomEmail());
        user.put("status", "active");
        user.put("gender", "male");
        
        given()
                .spec(requestSpecification)
                .body(user)
                .when().post()
                .then()
                .statusCode(201)
                .spec(responseSpecification)
                .body("email", equalTo(user.get("email")))
                .body("name", equalTo(user.get("name")));
    }
    
    User user;
    User userFromResponse;
    
    @Test(priority = 3)
    void createNewUserWithObject() {
//        User user = new User(); //with default constructor
//        user.setName(randomName());
//        user.setEmail(randomEmail());
//        user.setGender("male");
//        user.setStatus("active");
        
        //user has no ID because it's created only after posting request. We need to add it to our POJO class.
        user = new User(randomName(), randomEmail(), "male", "active"); //with parametrized constructor
        
        userFromResponse = given()
                .spec(requestSpecification)
                .body(user) //requestSpecification tells that user format is JSON
                .when().post()
                .then()
                .spec(responseSpecification)
                .statusCode(201)
                .body("email", equalTo(user.getEmail()))
                .body("name", equalTo(user.getName()))
                .extract().as(User.class);
    }
    
    
    @Test(dependsOnMethods = "createNewUserWithObject", priority = 4)
    void createNewUserNegativeTest() {
        User userNegative = new User(randomName(), user.getEmail(), "female", "active");
        
        given()
                .spec(requestSpecification)
                .body(userNegative)
                .when()
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(422)
                .body("[0].message", equalTo("has already been taken"));//the first element of array
        
    }
    
    /**
     * get the user you created in createNewUserWithObject test
     **/
    @Test(dependsOnMethods = "createNewUserWithObject", priority = 5)
    void getUserByID() {
        given()
                .pathParams("userID", userFromResponse.getId())
                .spec(requestSpecification)
                .when().get("{userID}")//adding path parameter to baseURI
                .then()
                .statusCode(200)
                .spec(responseSpecification)
                .body("id", equalTo(userFromResponse.getId()))
                .body("name", equalTo(userFromResponse.getName()))
                .body("email", equalTo(userFromResponse.getEmail()));
    }
    
    /**
     * Update the user you created in createNewUserWithObject
     **/
    @Test(dependsOnMethods = "createNewUserWithObject", priority = 6)
    void UpdateUserByID() {
        User updateUser = new User(randomName(), randomEmail(), "female", "active");
//        userFromResponse.setName(randomName());
//        userFromResponse.setName(randomEmail());
        given()
                .spec(requestSpecification)
                .pathParams("userID", userFromResponse.getId())
//                .body(userFromResponse)
                .body(updateUser)
                .when().put("{userID}")//adding path parameter to baseURI
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .body("id", equalTo(userFromResponse.getId()))
//                .body("name", equalTo(userFromResponse.getName()))
//                .body("email", equalTo(userFromResponse.getEmail()))
                .body("name", equalTo(updateUser.getName()))
                .body("email", equalTo(updateUser.getEmail()))
        ;
    }
    
    /**
     * Delete the user you created in createNewUserWithObject
     **/
    @Test(dependsOnMethods = "createNewUserWithObject", priority = 7)
    void deleteUser() {
        given()
                .spec(requestSpecification)
                .pathParams("userId", userFromResponse.getId())
                .when()
                .delete("{userId}")
                .then()
                .statusCode(204);//no body is returned from this request so no need to add responseSpecification
    }
    
    /**
     * create delete user negative test
     **/
    @Test(dependsOnMethods = {"createNewUserWithObject", "deleteUser"}, priority = 8)
    void deleteUserNegative() {
        given()
                .spec(requestSpecification)
                .pathParams("userId", userFromResponse.getId())
                .when()
                .delete("{userId}")
                .then()
                .statusCode(404); //no body is returned from this request so no need to add responseSpecification
    }
    
    @Test(dependsOnMethods = {"createNewUserWithObject", "deleteUser"}, priority = 9)
    void getUserByIdNegative() {
        given()
                .spec(requestSpecification)
                .pathParams("userId", userFromResponse.getId())
                .when()
                .get("{userId}")
                .then()
                .statusCode(404);
    }
    
}