package Controllers;

import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import services.RestClient;

import java.util.*;

import static apiConfigs.EndPoints.*;

public class ResourceController extends Controller {
    Faker fakerObject = new Faker();


    public Response getUsers(int userId, int targetStatusCode) {

        return restClient.performRequest(RestClient.Method.GET, targetStatusCode, GET_USERS + "/" + userId);
    }

    public Response getPosts(int userId, int targetStatusCode) {
        List<List<Object>> params = Arrays.asList(Arrays.asList("userId", userId));
        return restClient.performRequest(RestClient.Method.GET, targetStatusCode, GET_POSTS, params,
                RestClient.ParametersType.QUERY, ContentType.JSON);
    }
    public Response createNewPost(Object body , int targetStatusCode){

        return restClient.performRequest(RestClient.Method.POST, targetStatusCode, POST_POSTS, body, ContentType.JSON);
    }

    public Object preparePostBody(int userId){
        List<Map<String, Object>> body = new ArrayList<>();
        Map<String, Object> bodyPost = new HashMap<>();
        bodyPost.put("userId", userId);
        bodyPost.put("title", "test tempora rem veritatis voluptas quo dolores vero");
        bodyPost.put("body", "Test Body");
        body.add(bodyPost);
        return body ;
    }
}
