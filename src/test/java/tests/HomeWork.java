package tests;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utilities.ConfigurationReader;


import java.util.*;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
public class HomeWork {

    @BeforeAll
    public static void setup(){
        baseURI = ConfigurationReader.getProperty("uinames.uri");
    }

    @Test
    @DisplayName("Verify that name, surname, gender, region fields have value")
    public void NoParamsTest () {
         given().
                accept(ContentType.JSON).
         when().
                 get().
         then().
                 assertThat().statusCode(200).
                 contentType("application/json; charset=utf-8").
                 body(                "name", is(notNullValue()),
                 "surname", is(notNullValue()),
                                          "gender", is(notNullValue()),
                                          "region", is(notNullValue())).
                log().ifError();

       // solution#2

//        Map<String, String> person =
//        given().
//                accept(ContentType.JSON).
//        when().
//                get().
//        thenReturn().
//                jsonPath().get();
//        for(Map.Entry entry:person.entrySet()){
//            System.out.println(entry.getValue());
//           assertTrue(!entry.getValue().equals(null));
//        }

        //solution #3
        // JsonPath json = get().thenReturn().jsonPath();
//        Map<String, String> person = json.get();
//        for(Map.Entry entry:person.entrySet()){
//            System.out.println(entry.getValue());
//           assertTrue(!entry.getValue().equals(null));


    }

    @Test
    @DisplayName("Verify that value of gender field is same given in queryParam")
    public void GenderTest () {

        given().
                accept(ContentType.JSON).queryParam("gender", "female").
        when().
                get().
        then().
                assertThat().
                    statusCode(200).contentType("application/json; charset=utf-8").
                    body("gender", is("female")).
                    log().ifError();
    }

    @Test
    @DisplayName("Verify that value of gender and region given in queryParam")
    public void TwoParamsTest(){
        given().
                accept(ContentType.JSON).queryParam("region", "Russia").queryParam("gender", "female").
        when().
                get().
        then().
                assertThat().
                    statusCode(200).
                    contentType("application/json; charset=utf-8").
                    body("region", is("Russia"), "gender", is("female")).
                    log().ifError();
    }

    @Test
    @DisplayName("Verify that value of error field is Invalid gender")
    public void InvalidGenderTest (){
        given().
                accept(ContentType.JSON).
        when().
                queryParam("gender", "???").get().
        then().
                assertThat().
                    statusCode(400).
                    contentType(ContentType.JSON).
                    statusLine(containsString("Bad Request")).
                    body("error", is("Invalid gender")).
                    log().body();
    }


    @Test
    @DisplayName("Verify that value of error field is Region or language not found")
    public void InvalidRegionTest() {
        given().
                accept(ContentType.JSON).
        when().
                queryParam("region", "???").get().
        then().
                assertThat().
                    statusCode(400).
                    contentType(ContentType.JSON).
                    statusLine(containsString("Bad Request")).
                    body("error", is("Region or language not found")).
                    log().body();
        }

    @Test
    @DisplayName("Verify that all objects have different name+surname combination")
    public void AmountAndRegionsTest(){
        Response response = given().
                accept(ContentType.JSON).
                queryParam("region", "Russia").
        when().
                get("?amount=25");
        assertEquals(200, response.getStatusCode(), "Wrong status code");
        assertEquals("application/json; charset=utf-8", response.getContentType(), "Wrong content type");
        JsonPath jsonPath = response.jsonPath();
        List<String> names = jsonPath.getList("name");
        List<String> surnames = jsonPath.getList("surname");
        Set<String> fullNames = new HashSet<>();
        for(int i =0; i<names.size(); i++){
            fullNames.add(names.get(i)+" "+surnames.get(i));
        }
        assertTrue(25==fullNames.size(), "Names is not unique");
    }

    @Test
    @DisplayName("Verify that all objects the response have the same region and gender passed in queryParams")
    public void ThreeParamsTest(){
        Response response =
        given().
                accept(ContentType.JSON).
                queryParam("region", "Russia").
                queryParam("gender", "female").
        when().
                get("?amount=25");
        assertEquals(200, response.getStatusCode(), "Wrong status code");
        assertEquals("application/json; charset=utf-8", response.getContentType(), "Wrong content type");
        JsonPath jsonPath = response.jsonPath();
        Set<String> result = new HashSet<>();
        result.addAll(jsonPath.getList("region"));
        result.addAll(jsonPath.getList("gender"));
        assertEquals("[female, Russia]", result.toString(), "Wrong gender or region");
    }

    @Test
    @DisplayName("Verify that number of objects returned in the response is same as the amount passed queryParams")
    public void AmountCountTest(){
               Response response=
                given().
                        accept(ContentType.JSON).
                when().
                        get("?amount=25");
                assertEquals(200, response.getStatusCode(), "Wrong status code");
                assertEquals("application/json; charset=utf-8", response.getContentType(), "Wrong content type");
                List<Object> objects = response.jsonPath().getList("");
                assertTrue(25==objects.size(), "Wrong amount objects");



    }

}