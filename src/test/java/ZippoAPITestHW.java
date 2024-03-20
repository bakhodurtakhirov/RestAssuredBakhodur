import POJOClasses.Homework;
import POJOClasses.Location;
import POJOClasses.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ZippoAPITestHW {

/*    {
        "post code": "90210",
            "country": "United States",
            "country abbreviation": "US",
            "places": [
        {
            "place name": "Beverly Hills",
                "longitude": "-118.4065",
                "state": "California",
                "state abbreviation": "CA",
                "latitude": "34.0901"
        }
    ]
    }*/
    
    @Test
    void statusCodeTest() {
        given()
                .when().get("http://api.zippopotam.us/us/90210")
                .then().log().body()//log() has multiple submethods as body(), status() and etc.
                .log().status()
                .statusCode(200); //asserts if returned code as in ()
    }
    
    @Test
    void contentTypeTest() {
        given()
                .when().get("http://api.zippopotam.us/us/90210")
                .then().log().all()
                .contentType(ContentType.JSON); //asserts if returned type is JSON or other specified type
    }
    
    @Test
    void countryInformationTest() {
        given()
                .when().get("http://api.zippopotam.us/us/90210")
                .then().log().body()
                .body("country", equalTo("United States")); //asserts if "country" key from body is equal to "United States". "country" key is a path of parameter.
    }
    
    @Test
    void stateInformationTest() {
        given()
                .when().get("http://api.zippopotam.us/us/90210")
                .then().log().body()
                .body("places[0].state", equalTo("California")); //using index to reach element
    }
    
    @Test
    void stateAbbreviationInformationTest() {
        given()
                .when().get("http://api.zippopotam.us/us/90210")
                .then().log().body()
                .body("places[0].'state abbreviation'", equalTo("CA")); //use single quotes if there is a space in the parameter name. We can use \" too.
    }
    
    @Test
    void bodyHasItemTest() {
        given()
                .when().get("http://api.zippopotam.us/tr/01000")
                .then().log().body()
                .body("places.'place name'", hasItem("Büyükdikili Köyü")); //When we don't use [] with index then all items of the list will be returned. '' must be used if the key contains space
    }
    
    @Test
    void arrayHasSizeTest() {
        given()
                .when().get("http://api.zippopotam.us/tr/01000")
                .then().log().body()
                .body("places.'place name'", hasSize(71)); //Asserts if the size of list has 71 place names.
    }
    
    @Test
    void multipleTest() {
        given()
                .when().get("http://api.zippopotam.us/tr/01000")
                .then().log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("places", hasSize(71)) //Asserts if the size of list has 71 place names.
                .body("places.'place name'", hasItem("Büyükdikili Köyü"))
                .body("country", equalTo("Turkey")); //All test must be successful to make entire test successful.
    }
    
    //Parameters
    //There are 2 types of parameters"
    //    1. Path parameters -> http://api.zippopotam.us/tr/01000 -> They are part of the url separated by /
    //    2. Query parameters -> https://gorest.co.in/public/v2/users?page=3 -> They are separated by ? and & if multiple parameters. They are added automatically to the URL, so should not be added manually.
    
    @Test
    void pathParametersTest() {
        String countryCode = "us";
        String zipCode = "90210";
        
        given()
                .pathParams("country", countryCode)
                .pathParams("zip", zipCode)
                .log().uri() //returns url which is being sent to see its correctness
                .when().get("http://api.zippopotam.us/{country}/{zip}")
                .then()
                .log().body()
                .statusCode(200);
    }
    
    // send a get request for zipcodes between 90210 and 90213 and verify that in all responses the size
    // of the place array is 1
    @Test
    void pathParametersTest2() {
        
        for (int i = 90210; i <= 90213; i++) {
            given()
                    .pathParams("country", "us")
                    .pathParams("zip", i)
                    .log().uri() //returns url which is being sent to see its correctness
                    .when().get("http://api.zippopotam.us/{country}/{zip}")
                    .then()
                    .log().body()
                    .body("places", hasSize(1));
        }
    }
    
    @Test
    void queryParametersTest1() {
        
        given()
                .param("page", 2)
                .pathParams("apiName", "users")
                .pathParams("version", "v1")
                .log().uri() //returns url which is being sent to see its correctness
                .when().get("https://gorest.co.in/public/{version}/{apiName}")
                .then()
                .log().body()
                .statusCode(200);
    }
    
    // send the same request for the pages between 1-10 and check if
    // the page number we send from request and page number we get from response are the same
    @Test
    void queryParametersTest2() {
        
        for (int i = 1; i <= 10; i++) {
            given()
                    .param("page", i)
                    .pathParams("apiName", "users")
                    .pathParams("version", "v1")
                    .log().uri() //returns url which is being sent to see its correctness
                    .when().get("https://gorest.co.in/public/{version}/{apiName}")
                    .then()
                    .log().body()
                    .body("meta.pagination.page", equalTo(i)); //can add multiple body() lines
        }
    }
    
    // Write the same test with Data Provider
    @Test(dataProvider = "dataMethod")
    void queryParametersTest3(int page, String apiName, String version) {
        
        given()
                .param("page", page)
                .pathParam("apiName", apiName)
                .pathParam("version", version)
                .log().uri() //returns url which is being sent to see its correctness
                .when().get("https://gorest.co.in/public/{version}/{apiName}")
                .then()
                .log().body()
                .body("meta.pagination.page", equalTo(page));
    }
    
    @DataProvider
    public Object[][] dataMethod() {
        return new Object[][]{
                {1, "users", "v1"},
                {2, "users", "v1"},
                {3, "users", "v1"},
                {4, "users", "v1"},
                {5, "users", "v1"},
                {6, "users", "v1"},
                {7, "users", "v1"},
                {8, "users", "v1"},
                {9, "users", "v1"},
                {10, "users", "v1"}
        };
        
    }
    
    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
    
    @BeforeClass
    public void setUp() {
        baseURI = "https://gorest.co.in/public/";
        
        requestSpecification = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .addPathParam("apiName", "users")
                .addPathParam("version", "v1")
                .addParam("page", 3)
                .build();
        
        responseSpecification = new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectBody("meta.pagination.page", equalTo(3))
                .build();
    }
    
    @Test
    void baseURITest() {
        given()
                .param("page", 3)
                .log().uri()
                .when().get("/users") //if http is not included in get() then rest assured puts baseURI to the beginning of the url in the request method
                .then()
                .log().body();
    }
    
    @Test
    void specificationTest() {
        given()
                .spec(requestSpecification) //will get variables from requestSpecifications
                .when().get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification);
    }
    
    @Test
    void extractStringTest() {
        String placeName = given()
                .pathParam("country", "us")
                .pathParam("zip", "90210")
                .log().uri()
                .when().get("http://api.zippopotam.us/{country}/{zip}")
                .then()
                .log().body()
                .statusCode(200)
                .extract().path("places[0].'place name'");
        System.out.println("placeName = " + placeName);
        // with extract method our request returns a value(not an object).
        // extract returns only one part of the response(the part that we specified in the path method) or list of that value
        // we can assign it to a variable and use it however we want
    }
    
    @Test
    void extractIntTest() {
        int pageNumber = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("meta.pagination.page");
        
        System.out.println("pageNumber = " + pageNumber);
        Assert.assertTrue(pageNumber == 3);
        
        // We are not allowed to assign an int to a String(cannot assign a type to another type)
    }
    
    @Test
    void extractListTest1() {
        List<String> nameList = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("data.name");
        
        System.out.println("nameList.size() = " + nameList.size());
        System.out.println("nameList.get(4) = " + nameList.get(4));
        System.out.println("nameList.contains(\"Ravi Adiga\") = " + nameList.contains("Ravi Adiga"));
        
        Assert.assertTrue(nameList.contains("Ravi Adiga"));
    }
    
    // Send a request to https://gorest.co.in/public/v1/users?page=3
    // and extract email values from the response and check if they contain patel_atreyee_jr@gottlieb.test
    @Test
    void extract() {
        List<String> emails = given()
                .spec(requestSpecification) //will get variables from requestSpecifications
                .when().get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("data.email");
        
        Assert.assertTrue(emails.contains("patel_atreyee_jr@gottlieb.test"));
    }
    
    // Send a request to https://gorest.co.in/public/v1/users?page=3
    // and check if the next link value contains page=4
    @Test
    void extractListTest2() {
        String nextLink = given()
                .spec(requestSpecification) //will get variables from requestSpecifications
                .when().get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("meta.pagination.links.next");
        
        Assert.assertTrue(nextLink.contains("page=4"));
    }
    
    // extract.path           vs                 extract.response
    // extract.path() can only give us one part of the response. If you need different values from different parts of the response (names and page)
    // you need to write two different request.
    // extract.response() gives us the entire response as an object so if you need different values from different parts of the response (names and page)
    // you can get them with only one request
    @Test
    void extractResponse() {
        Response response = given()
                .spec(requestSpecification) //will get variables from requestSpecifications
                .when().get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().response(); //The entire response willl be saved into Response object which allows us to access any element of the response.
        int page = response.path("meta.pagination.page");
        System.out.println("page = " + page);
        
        String name = response.path("data[1].name");
        System.out.println("name = " + name);
        
        List<String> emails = response.path("data.email");
        
        System.out.println("emails = " + emails);
    }
    
    //POJO -> Plain Old Java Object. Variable names must match. Create only if you need it. Creating POJO classes take time if we have many objects in the body,
    @Test
    void extractJsonPOJO() {
        Location location = given() //Need to assign to the main class as in extract().as(Location.class)
                .pathParam("countryCode", "us")
                .pathParam("zipCode", "90210")
                .when()
                .get("http://api.zippopotam.us/{countryCode}/{zipCode}")
                .then()
                .log().body()
                .extract().as(Location.class);
        
        System.out.println("location.getPostCode() = " + location.getPostCode());
        System.out.println("location.getCountry() = " + location.getCountry());
        System.out.println("location.getPlaces().get(0).getPlaceName() = " + location.getPlaces().get(0).getPlaceName());
        System.out.println("location.getPlaces().get(0).getState() = " + location.getPlaces().get(0).getState());
        // This request extracts the entire response and assigns it to Location class as a Location object
        // We cannot extract the body partially (e.g. cannot extract place object separately)
        //Variable names in response body and POJO classes must match. If there is a space in response body variables, then we need to use import com.fasterxml.jackson.annotation.JsonProperty; in
        //classes and add @JsonProperty("post code") above private variable which has space in the response body, so systems will know that "post code" matches setPostCode. We need to add
        //this annotation only above setters.
        
    }
    
    /*    Send a get request to "https://jsonplaceholder.typicode.com/users".
            Extract the response to POJO classes.
            Verify that Chelsey Dietrich's
                - email is "Lucio_Hettinger@annie.ca"
                - street name is "Skiles Walks"
                - phone number is "(254)954-1289"
                - Company name is "Keebler LLC"
        Don't use static index. Keep in mind that the index of the user could change. Use a dynamic code*/
    @Test
    void hw1() {
        int id = 1;
        while (true) {
            Homework homework1 = given()
                    .pathParams("id", id)
                    .when().get("https://jsonplaceholder.typicode.com/users/{id}")
                    .then()
                    .extract().as(Homework.class);
            
            if (homework1.getName().equals("Chelsey Dietrich")) {
                Assert.assertEquals(homework1.getAddress().getStreet(), "Skiles Walks");
                Assert.assertEquals(homework1.getPhone(), "(254)954-1289");
                Assert.assertEquals(homework1.getCompany().getName(), "Keebler LLC");
                break;
            }
            id++;
//            if(homework1.getName()==null) break;
        }
    }
    
    // extract.path() => We can extract only one value (String, int...) or list of that value(List<String>, List<Integer>)
    //      String name = extract.path(data[0].name);
    //      List<String> nameList = extract.path(data.name);
    
    // extract.response => We can get the entire response as a Response object and get whatever we want from it.
    // We may need to write long path to reach necessary element and can't be used as request body in the next test.
    //      We don't need a class structure. But if you need to use an object for your next requests it is not useful
    
    // extract.as => We can extract the entire response body as POJO classes. But we cannot extract one part of the body separately.
    //      We need to create a class structure for the entire body
    //      extract.as(Location.class)
    //      extract.as(Place.class) is not allowed
    //      extract.as(User.class)
    
    // extract.jsonPath() => We can extract the entire body as POJO classes as well as only one part of the body. So if you need only one part
    //      of the body you don't need to create a class structure for the entire body. You only need class for that part of the body
    //      extract.jsonPath().getObject(Location.class)
    //      extract.jsonPath().getObject(Place.class)
    //      extract.jsonPath().getObject(User.class)
    
    @Test
    void extractWithJsonPath() {//The most efficient way compared than other methods
        User user = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().jsonPath().getObject("data[0]", User.class); //save into User class
        
        System.out.println("user.getId() = " + user.getId());
        System.out.println("user.getName() = " + user.getName());
        System.out.println("user.getEmail() = " + user.getEmail());
    }
    
    @Test
    void extractWithJsonPath2() {//The most efficient way compared than other methods
        List<User> userList = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().jsonPath().getList("data", User.class); //list of objects
        
        System.out.println("userList.size() = " + userList.size());
        System.out.println("userList.get(2) = " + userList.get(2).getName());
        System.out.println("userList.get(8).getId() = " + userList.get(8).getId());
    }
    
    @Test
    void extractWithJsonPath3() {
        String name = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().jsonPath().getString("data[1].name"); //no need indicate type
        
        System.out.println("name = " + name);
    }
    
    @Test
    void extractWithJsonPath4() {
        Response response = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().response(); //we can get Response object to get to use it with jsonPath
        
        int page = response.jsonPath().getInt("meta.pagination.page");
        System.out.println("page = " + page);
        
        String currentLink= response.jsonPath().getString("meta.pagination.links.current");
        System.out.println("currentLink = " + currentLink);
        
        User user = response.jsonPath().getObject("data[2]", User.class);
        System.out.println("user.getName() = " + user.getName());
        
        List<User> userList = response.jsonPath().getList("data", User.class);
    }
    
}