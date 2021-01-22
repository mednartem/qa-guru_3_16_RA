package tests;

import model.UserModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static utils.RandomGeneration.generateString;

public class ReqresTests {

    @Test
    @DisplayName("Check list users isn't empty")
    void getListUsers() {
        given()
                .spec(Specification.spec())
                .get("/api/users?page=2")
                .then()
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
                .spec(Specification.spec())
                .body(userModel)
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", not(emptyString()));
    }


    @Test
    @DisplayName("Create valid user without body")
    void createValidUserWithoutBody() {
        given()
                .spec(Specification.spec())
                .post("/api/user")
                .then()
                .statusCode(201)
                .body("id", not(emptyString()));
    }

    @Test
    @DisplayName("Delete created user")
    void deleteCreatedUser() {
        HashMap<String, String> data = new HashMap<>() {{
            put("name", generateString(10));
            put("job", generateString(5));
        }};

        UserModel userModel = given()
                .spec(Specification.spec())
                .body(data)
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", not(emptyString()))
                .extract().as(UserModel.class);

        given()
                .spec(Specification.spec())
                .delete("/api/users" + userModel.getId())
                .then()
                .statusCode(204);

    }

    //Fields name and job are missing in response from request post and put
    @Test
    @DisplayName("Update user")
    void updateUser() {
        HashMap<String, String> data = new HashMap<>();
        data.put("name", generateString(10));
        data.put("job", generateString(5));

        UserModel userModel1 = new UserModel();
        userModel1.setName(generateString(10));
        userModel1.setJob(generateString(6));

        UserModel userModel = given()
                .spec(Specification.spec())
                .body(data)
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", not(emptyString()))
                .extract().body().as(UserModel.class);

        given()
                .spec(Specification.spec())
                .body(userModel1)
                .put("/api/users" + userModel.getId())
                .then()
                .statusCode(200)
                .body("updatedAt", not(emptyString()));
    }
}
