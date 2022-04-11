package kvint.campaigns;

import dto.authenticate.AuthRequest;
import dto.authenticate.AuthResponse;
import dto.campaign.CampaignInfoResponse;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import model.Campaign;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static model.User.LOGIN;
import static model.User.PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static requests.Requests.campaignURI;
import static requests.Requests.userAuthURI;

@Story("API тесты")
@Feature("CampaignInfo")
public class GetCampaignInfoTest {
    private static String token;

    @BeforeAll
    static void setupBaseURI() {
        RestAssured.baseURI = "https://api.go.staging.kvint.io/";
        AuthRequest userAuthRequestDTO = new AuthRequest(LOGIN, PASSWORD);
        step("Отправить POST запрос на авторизацию c корректным логином и паролем", () -> {
            token = given(userAuthURI)
                    .body(userAuthRequestDTO)
                    .filter(new AllureRestAssured())
                    .when()
                    .post()
                    .then()
                    .extract()
                    .as(AuthResponse.class)
                    .getAccess_token();
        });
    }

    @Severity(SeverityLevel.BLOCKER)
    @Test
    void correctTokenAndID() {
        var resp = step("Отправить GET запрос на получение информации о компании " +
                "с корректным campaignID c корректным логином и паролем", () -> {
            return given(campaignURI)
                    .header("Authorization",
                            "Bearer " + token)
                    .when()
                    .filter(new AllureRestAssured())
                    .get(Campaign.ID);
        });

        step("Код ответа должен быть 200", () -> {
            assertThat(resp.statusCode())
                    .isEqualTo(200);
        });

        step("Тело ответа должно содержать JSON объект с полем ID = 2757", () -> {
            var actual = resp.then().extract().as(CampaignInfoResponse.class);

            assertThat(actual.getId())
                    .isEqualTo(2757);
        });
    }

    @Test
    void WithNoToken() {
        var resp = step("Отправить GET запрос на получение информации о компании " +
                "с корректным campaignID c корректным логином и паролем без токена авторизации в запросе", () -> {
            return given(campaignURI)
                    .header("Authorization",
                            "Bearer " + "")
                    .when()
                    .filter(new AllureRestAssured())
                    .get(Campaign.ID);
        });

        step("Код ответа должен быть 401", () -> {
            assertThat(resp.statusCode())
                    .isEqualTo(401);
        });

        step("Ответ должен содержать текст: Unauthorized", () -> {
            var actual = resp.body().asString();

            assertThat(actual)
                    .isEqualTo("Unauthorized");
        });
    }

    @Test
    void WithIncorrectToken() {
        var resp = step("Отправить GET запрос на получение информации о компании " +
                "с корректным campaignID c корректным логином и паролем без токена авторизации в запросе", () -> {
            return given(campaignURI)
                    .header("Authorization",
                            "Bearer " + "IncorrectToken")
                    .when()
                    .filter(new AllureRestAssured())
                    .get(Campaign.ID);
        });

        step("Код ответа должен быть 401", () -> {
            assertThat(resp.statusCode())
                    .isEqualTo(401);
        });

        step("Ответ должен содержать текст: Unauthorized", () -> {
            var actual = resp.body().asString();

            assertThat(actual)
                    .isEqualTo("Unauthorized");
        });
    }
}
