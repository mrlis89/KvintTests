package kvint.auth;

import dto.authenticate.AuthResponse;
import dto.authenticate.AuthRequest;
import dto.refresh.RefreshRequest;
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
import static requests.Requests.userRefreshURI;

@Story("API тесты")
@Feature("Authorization/refreshToken")
public class RefreshTest {

    @BeforeAll
    static void setupBaseURI() {
        RestAssured.baseURI = "https://api.go.staging.kvint.io/";
    }

    @Test
    void successRefresh() {
        var authResponse = step("Отправить POST запрос на авторизацию c корректным логином и паролем",  () -> {
            AuthRequest userAuthRequestDTO = new AuthRequest(LOGIN, PASSWORD);

            return given(userAuthURI)
                    .body(userAuthRequestDTO)
                    .filter(new AllureRestAssured())
                    .when()
                    .post()
                    .then().extract().as(AuthResponse.class);
        });

        var refreshResponse = step("Отправить POST запрос на обновление токена",  () -> {
            RefreshRequest request = new RefreshRequest(authResponse.getRefresh_token());

            return given(userRefreshURI)
                    .body(request)
                    .filter(new AllureRestAssured())
                    .when()
                    .post();
        });

        step("Код ответа должен быть 200", () -> {
            assertThat(refreshResponse.statusCode())
                    .isEqualTo(200);
        });

        step("Тело ответа должно содержать обновленный токен авторизации", () -> {
            var actual = refreshResponse.then().extract().as(AuthResponse.class);

            assertThat(actual.getAccess_token())
                    .isNotBlank();
        });
    }

    @Test
    void incorrectRefresh() {
        var refreshResponse = step("Отправить POST запрос на обновление токена с некорректным токеном",  () -> {
            RefreshRequest request = new RefreshRequest("1");

            return given(userRefreshURI)
                    .body(request)
                    .filter(new AllureRestAssured())
                    .when()
                    .post();
        });

        step("Код ответа должен быть 400", () -> {
            assertThat(refreshResponse.statusCode())
                    .isEqualTo(400);
        });

        step("Тело ответа должно содержать обновленный токен авторизации", () -> {
            var actual = refreshResponse.body().asString();

            assertThat(actual)
                    .isEqualTo("{\"errors\":[{\"keyword\":\"validRefreshToken\",\"dataPath\":\".refresh_token\",\"schemaPath\":\"#/properties/refresh_token/validRefreshToken\",\"params\":{},\"message\":\"jwt malformed\"}]}");
        });
    }
}
