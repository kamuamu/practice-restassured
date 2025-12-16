package com.api.test.stepdefs;

import com.api.test.modals.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import io.restassured.response.Response;


import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class UserManagementTest {
    private Response response;
    public static String id;
    private final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1ydm5ka3hqaG5kcGxoaGpta2JmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkxNDMxMDcsImV4cCI6MjA2NDcxOTEwN30.1g03KgbnmXOwjPdeT72QRlUBQWwnald5aD4lSqkKAw0";

    @Given("I have the base API URL")
    public void setUp() {
        RestAssured.baseURI = "https://mrvndkxjhndplhhjmkbf.supabase.co/rest/v1";
    }

    @When("I send a GET request with token")
    public void testGetUsers() {
        response = given()
                    .header("apiKey", API_KEY)
                    .contentType("application/json")
                    .when()
                    .get("/users");
    }

    @Then("the response status code should be 200")
    public void verifyResponse() {
        response.then().statusCode(200);

    }

    @When("I have create a new user with following details")
    public void createUser(DataTable dataTable) throws JsonProcessingException {
        Map<String,String> map = dataTable.asMap(String.class, String.class);
        User user = new User();
        user.setFirstname(map.get("first_name"));
        user.setLastname(map.get("last_name"));
        user.setEmail(map.get("email"));
        user.setAge(Integer.parseInt(map.get("age")));
        response = given()
                .header("apikey", API_KEY)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Prefer", "return=representation")
                .contentType("application/json")
                .body(user)
                .when()
                .post("/users");
    }

    @Then("the response status code should be 201")
    public void verifyResponseCreated() {
        response.then().statusCode(201);
        id = response.body().jsonPath().get("[0].id");
        System.out.println("ID: " + id);
    }

    @Then("the response status code should be 204")
    public void verifyResponseNoContent() {
        response.then().statusCode(204);
    }

    @When("I delete the user with id.")
    public void iDeleteTheUserWithId() {
        System.out.println(id);
        if(id == null) {
            System.out.println("id is null");
        }
        else{
            response = given()
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .contentType("application/json")
                    .queryParam("id", "eq." + id)
                    .when()
                    .delete("/users");
            response.then().log().all();
        }

    }

    @When("I have update the user with {string}")
    public void iHaveUpdateTheUserWith(String email){
        Map<String,String> map = new HashMap<>();
        map.put("email", email);
        response = given()
                .header("apikey", API_KEY)
                .header("Authorization", "Bearer "+ API_KEY)
                .header("Prefer", "return=representation")
                .contentType("application/json")
                .queryParam("id", "eq." +id)
                .body(map)
                .when()
                .patch("/users");
        response.then().log().all();
    }

    @And("the response should be {string}")
    public void theResponseShouldBe(String schemafile) {
        response.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/"+ schemafile));
        System.out.println("Json schema successfull..");
    }
}