import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;


public class TestRunner {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    private static RequestSpecification request;
    private static Faker faker;
    private static String name;
    private static String email;
    private static String comment;
    private static int maxUserId;
    private static int maxId;

    @BeforeAll
    public static void beforeAll() {
        request = RestAssured.given();
        faker = new Faker();
        name = faker.address().firstName();
        email = faker.internet().emailAddress();
        comment = faker.backToTheFuture().quote();
    }

    @Test
    @Order(1)
    public void getMaxUserIdTest() {
        Response response = request.get(BASE_URL + "posts/");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        JsonPath json = response.jsonPath();
        List<Integer> usersId = json.get("userId");

        maxUserId = usersId.stream()
                .distinct()
                .mapToInt(v -> v)
                .max()
                .orElseThrow(NoSuchElementException::new);

        assertThat(maxUserId).isNotNull();
    }

    @Test
    @Order(2)
    public void getMaxIdTest() {
        Response response = request.get(BASE_URL + "posts?userId=" + maxUserId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        JsonPath json = response.jsonPath();
        List<Integer> usersId = json.get("id");

        maxId = usersId.stream()
                .mapToInt(v -> v)
                .max()
                .orElseThrow(NoSuchElementException::new);

        assertThat(maxId).isNotNull();
    }

    @Test
    @Order(3)
    public void createCommentForGivenPostId() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("postId", maxId);
        requestParams.put("name", name);
        requestParams.put("email", email);
        requestParams.put("body", comment);
        request.header("Content-Type", "application/json");
        request.body(requestParams.toString());

        Response response = request.post(BASE_URL + "comments/");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SC_CREATED);

        JsonPath json = response.jsonPath();
        String respName = json.get("name");
        String respEmail = json.get("email");
        String respBody = json.get("body");

        assertThat(respName).isEqualTo(name);
        assertThat(respEmail).isEqualTo(email);
        assertThat(respBody).contains(comment);
    }
}
