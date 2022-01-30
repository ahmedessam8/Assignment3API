import Controllers.ResourceController;
import com.github.javafaker.Faker;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import services.ResponseValidator;

import java.util.List;

public class UsersTest {
    ResourceController resourcesController = new ResourceController();
    Faker fakerObject = new Faker();
    int num = randomNumber();


    @Test
    public void verify_getUsers_should_return_UserEmail() {

        Response response = resourcesController.getUsers(num, 200);
        JsonPath jsonPathEvaluator = response.jsonPath();
        String email = jsonPathEvaluator.get("email");
        System.out.println("email received from Response " + email);
    }

    @Test
    public void verify_userPosts_should_beInt_inRange() {
        Response response1 = resourcesController.getPosts(num, 200);
        List<Integer> postIdList = response1.jsonPath().getList("id");
        Assert.assertTrue(postIdList.stream().anyMatch(x -> x >= 1 && x <= 100));
    }

    @Test
    public void verify_CreatingNewPostBySameUser_shouldBeCreatedSuccessfully() {
        Response response = resourcesController.createNewPost(resourcesController.preparePostBody(num), 201);
        ResponseValidator.assertThatStatusCodeMatchesTheExpected(response,201);
    }

    public int randomNumber() {
        return (fakerObject.number().numberBetween(1, 10));
    }
}
