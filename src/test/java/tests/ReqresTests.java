package tests;

import io.restassured.RestAssured;
import model.UserModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
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
                .then().log().all()
                .statusCode(200)
                .body("support.text", equalTo("To keep ReqRes free, contributions towards server costs are appreciated!"));
    }

    @Test
    @DisplayName("Create valid user")
    void createValidUser() {
        UserModel userModel = new UserModel();
        userModel.setName(generateString(10));
        userModel.setJob(generateString(5));

        given()
                .body(userModel)
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", not(emptyString()));
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
        HashMap<String, String> data = new HashMap<>() {{
            put("name", generateString(10));
            put("job", generateString(5));
        }};

        UserModel userModel = given()
                .body(data)
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", not(emptyString()))
                .extract().as(UserModel.class);

        delete("/api/users" + userModel.getId())
                .then()
                .statusCode(204);

    }

    //Fields name and job are missing in response from request post and put
    @Test
    @DisplayName("Update user")
    void updateUser() {
        HashMap<String, String> data = new HashMap<>() {{
            put("name", generateString(10));
            put("job", generateString(5));
        }};

        UserModel userModel1 = new UserModel();
        userModel1.setName(generateString(10));
        userModel1.setJob(generateString(6));

        UserModel userModel = given()
                .body(data)
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", not(emptyString()))
                .extract().body().as(UserModel.class);

        given()
                .body(userModel1)
                .put("/api/users" + userModel.getId())
                .then()
                .statusCode(200)
                .body("updatedAt", not(emptyString()));
    }
}
