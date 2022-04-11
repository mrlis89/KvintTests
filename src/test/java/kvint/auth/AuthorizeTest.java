package kvint.auth;

import dto.authenticate.AuthRequest;
import dto.authenticate.AuthResponse;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static model.User.LOGIN;
import static model.User.PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static requests.Requests.userAuthURI;

@Story("API тесты")
@Feature("Authorization")
public class AuthorizeTest {

    @BeforeAll
    static void setupBaseURI() {
        RestAssured.baseURI = "https://api.go.staging.kvint.io/";
    }

//Позитивный тест
    @Test
    void successAuth() {
        var resp = step("Отправить POST запрос на авторизацию c корректным логином и паролем",  () -> {
            AuthRequest userAuthRequestDTO = new AuthRequest(LOGIN, PASSWORD);

            return given(userAuthURI)
                    .body(userAuthRequestDTO)
                    .filter(new AllureRestAssured())
                    .when()
                    .post();
        });

        step("Код ответа должен быть 200", () -> {
            assertThat(resp.statusCode())
                    .isEqualTo(200);
        });

        step("Тело ответа должно содержать токен авторизации", () -> {
            var actual = resp.then().extract().as(AuthResponse.class);

            assertThat(actual.getAccess_token())
                    .isNotBlank();
        });
    }

//Негативные тесты
    @Test
    void emptyLogin() {
        var resp = step("Отправить POST запрос на авторизацию без логина и пароля",  () -> {
            AuthRequest userAuthRequestDTO = new AuthRequest("", "");

            return given(userAuthURI)
                    .body(userAuthRequestDTO)
                    .filter(new AllureRestAssured())
                    .when()
                    .post();
        });

        step("Код ответа должен быть 422", () -> {
            assertThat(resp.statusCode())
                    .isEqualTo(422);
        });

        step("Тело ответа должно содержать ошибку о пустом токене", () -> {
            var actual = resp.body().asString();

            assertThat(actual)
                    .isEqualTo("{\"errors\":[{\"keyword\":\"minLength\",\"dataPath\":\".login\",\"schemaPath\":\"#/properties/login/minLength\",\"params\":{\"limit\":1},\"message\":\"should NOT be shorter than 1 characters\"}]}");
        });
    }

    @Test
    void emptyPassword() {
        var resp = step("Отправить POST запрос на авторизацию без пароля",  () -> {
            AuthRequest userAuthRequestDTO = new AuthRequest("test", "");

            return given(userAuthURI)
                    .body(userAuthRequestDTO)
                    .filter(new AllureRestAssured())
                    .when()
                    .post();
        });

        step("Код ответа должен быть 422", () -> {
            assertThat(resp.statusCode())
                    .isEqualTo(422);
        });

        step("Тело ответа должно ошибку о пустом пароле", () -> {
            var actual = resp.body().asString();

            assertThat(actual)
                    .isEqualTo("{\"errors\":[{\"keyword\":\"minLength\",\"dataPath\":\".password\",\"schemaPath\":\"#/properties/password/minLength\",\"params\":{\"limit\":1},\"message\":\"should NOT be shorter than 1 characters\"}]}");
        });
    }

    //В случае когда указан не существующий логин, ответ от сервера должен однозначно сигнализировать об этом
    //фактически сервер возвращает Application not found, это не корректный ответ
    @Test
    void incorrectLogin() {
        var resp = step("Отправить POST запрос на авторизацию с несущетсвующим пользователем",  () -> {
            AuthRequest userAuthRequestDTO = new AuthRequest("test", "1");

            return given(userAuthURI)
                    .body(userAuthRequestDTO)
                    .filter(new AllureRestAssured())
                    .when()
                    .post();
        });

        step("Код ответа должен быть 400", () -> {
            assertThat(resp.statusCode())
                    .isEqualTo(400);
        });

        step("Тело ответа должно содержать ошибку о некорректном пароле", () -> {
            var actual = resp.body().asString();

            assertThat(actual)
                    .isEqualTo("{\"errors\":[{\"keyword\":\"validPassword\",\"dataPath\":\".password\",\"schemaPath\":\"#/properties/password/validPassword\",\"params\":{},\"message\":\"Application not found\"}]}");
        });
    }

    @Test
    void incorrectPassword() {
        var resp = step("Отправить POST запрос на авторизацию с корректным пользователем и некорректным паролем",  () -> {
            AuthRequest userAuthRequestDTO = new AuthRequest(LOGIN, "1");

            return given(userAuthURI)
                    .body(userAuthRequestDTO)
                    .filter(new AllureRestAssured())
                    .when()
                    .post();
        });

        step("Код ответа должен быть 400", () -> {
            assertThat(resp.statusCode())
                    .isEqualTo(400);
        });

        step("Тело ответа должно содержать ошибку о некорректном пароле", () -> {
            var actual = resp.body().asString();

            assertThat(actual)
                    .isEqualTo("{\"errors\":[{\"keyword\":\"validPassword\",\"dataPath\":\".password\",\"schemaPath\":\"#/properties/password/validPassword\",\"params\":{},\"message\":\"Invalid password\"}]}");
        });
    }
}
