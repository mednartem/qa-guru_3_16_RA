package tests;

import io.restassured.RestAssured;
import model.UserModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static com.fasterxml.jackson.databind.ObjectWriter.Prefetch.empty;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.not;
import static utils.RandomGeneration.generateString;

public class ReqresTests {

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "https://reqres.in";
    }

    @Test
    @DisplayName("Check list users isn't empty")
    void getListUsers() {
        get("/api/users?page=2")
                .then()
                .statusCode(200)
                .body("data", not(empty));
    }

    @Test
    @DisplayName("Create valid user")
    void createValidUser() {
        given()
                .body(new HashMap<String, String>() {{
                    put("name", generateString(10));
                    put("job", generateString(5));
                }})
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", not(empty));
    }


    @Test
    @DisplayName("Create invalid user")
    void createInvalidUser() {
        post("/api/users")
                .then()
                .statusCode(415);

    }

    @Test
    @DisplayName("Delete created user")
    void deleteCreatedUser() {
        UserModel userModel = given()
                .body(new HashMap<String, String>() {{
                    put("name", generateString(10));
                    put("job", generateString(5));
                }})
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", not(empty))
                .extract().as(UserModel.class);

        delete("/api/users" + userModel.getId())
                .then()
                .statusCode(204);

    }

    //Fields name and job are missing in response from request post and put
    @Test
    @DisplayName("Update user")
    void updateUser() {
        UserModel userModel = new UserModel();
        userModel.setName(generateString(10));
        userModel.setJob(generateString(6));

        given()
                .body(new HashMap<String, String>() {{
                    put("name", generateString(10));
                    put("job", generateString(5));
                }})
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", not(empty));

        given()
                .body(userModel)
                .put("/api/users" + userModel.getId())
                .then().log().all()
                .statusCode(200)
                .body("updatedAt", not(empty));
    }
}
