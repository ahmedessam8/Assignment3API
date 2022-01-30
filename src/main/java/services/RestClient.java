package services;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestClient {
    private static final String ARGUMENT_SEPARATOR = "?";
    private final Map<String, String> sessionHeaders;
    private final String serviceURI;
    private String headerAuthorization;

    public RestClient(String serviceURI) {
        this.serviceURI = serviceURI;
        this.sessionHeaders = new HashMap();
        this.headerAuthorization = "";
    }

    public RestClient addHeaderVariable(String key, String value) {
        this.sessionHeaders.put(key, value);
        return this;
    }

    public RestClient addHeaders(Map<String, String> headers) {
        this.sessionHeaders.putAll(headers);
        return this;
    }

    private Response performRequest(Object[] params) {
        RestClient.Method method = (RestClient.Method)params[0];
        int targetStatusCode = (Integer)params[1];
        String serviceName = (String)params[2];
        String urlArguments = (String)params[3];
        List<List<Object>> parameters = (List)params[4];
        RestClient.ParametersType parametersType = (RestClient.ParametersType)params[5];
        Object requestBody = params[6];
        ContentType contentType = (ContentType)params[7];
        String request = this.prepareRequestURL(urlArguments, serviceName);
        RequestSpecification specs = this.prepareRequestSpecs(parameters, parametersType, requestBody, contentType);
        Response response = null;
        response = this.sendRequest(method, request, specs);
        if (response != null) {
            Assert.assertEquals(targetStatusCode, response.getStatusCode());
        }

        return response;
    }

    private String prepareRequestURL(String urlArguments, String serviceName) {
        return urlArguments != null && !urlArguments.equals("") ? this.serviceURI + serviceName + "?" + urlArguments : this.serviceURI + serviceName;
    }

    private RequestSpecification prepareRequestSpecs(List<List<Object>> parameters, RestClient.ParametersType parametersType, Object body, ContentType contentType) {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setContentType(contentType);
        if (body != null && contentType != null && !body.toString().equals("")) {
            this.prepareRequestBody(builder, body, contentType);
        } else if (parameters != null && !parameters.isEmpty() && !((List)parameters.get(0)).get(0).equals("")) {
            this.prepareRequestBody(builder, parameters, parametersType);
        }

        return builder.build();
    }

    private void prepareRequestBody(RequestSpecBuilder builder, Object body, ContentType contentType) {
        try {
            switch(contentType) {
                case JSON:
                    builder.setBody(body, ObjectMapperType.GSON);
                    break;
                case XML:
                    builder.setBody(body, ObjectMapperType.JAXB);
                    break;
                default:
                    builder.setBody(body);
            }
        } catch (Exception var5) {
            System.out.println(var5);
        }

    }

    private void prepareRequestBody(RequestSpecBuilder builder, List<List<Object>> parameters, RestClient.ParametersType parametersType) {
        parameters.forEach((param) -> {
            if (parametersType.equals(RestClient.ParametersType.FORM)) {
                builder.addFormParam(param.get(0).toString(), new Object[]{param.get(1)});
            } else {
                builder.addQueryParam(param.get(0).toString(), new Object[]{param.get(1)});
            }

        });
    }

    private Response sendRequest(RestClient.Method method, String request, RequestSpecification specs) {
        if (this.sessionHeaders.size() > 0) {
            switch(method) {
                case POST:
                    return (Response)((Response)RestAssured.given().headers(this.sessionHeaders).spec(specs).when().post(request, new Object[0])).andReturn();
                case PATCH:
                    return (Response)((Response)RestAssured.given().headers(this.sessionHeaders).spec(specs).when().patch(request, new Object[0])).andReturn();
                case PUT:
                    return (Response)((Response)RestAssured.given().headers(this.sessionHeaders).spec(specs).when().put(request, new Object[0])).andReturn();
                case GET:
                    return (Response)((Response)RestAssured.given().headers(this.sessionHeaders).spec(specs).when().get(request, new Object[0])).andReturn();
                case DELETE:
                    return (Response)((Response)RestAssured.given().headers(this.sessionHeaders).spec(specs).when().delete(request, new Object[0])).andReturn();
            }
        } else {
            switch(method) {
                case POST:
                    return (Response)((Response)RestAssured.given().spec(specs).when().post(request, new Object[0])).andReturn();
                case PATCH:
                    return (Response)((Response)RestAssured.given().spec(specs).when().patch(request, new Object[0])).andReturn();
                case PUT:
                    return (Response)((Response)RestAssured.given().spec(specs).when().put(request, new Object[0])).andReturn();
                case GET:
                    return (Response)((Response)RestAssured.given().spec(specs).when().get(request, new Object[0])).andReturn();
                case DELETE:
                    return (Response)((Response)RestAssured.given().spec(specs).when().delete(request, new Object[0])).andReturn();
            }
        }

        return null;
    }

    public Response performRequest(RestClient.Method method, int targetStatusCode, String serviceName) {
        return this.performRequest(new Object[]{method, targetStatusCode, serviceName, null, null, null, null, ContentType.ANY});
    }

    public Response performRequest(RestClient.Method method, int targetStatusCode, String serviceName, String urlArguments) {
        return this.performRequest(new Object[]{method, targetStatusCode, serviceName, urlArguments, null, null, null, ContentType.ANY});
    }

    public Response performRequest(RestClient.Method method, int targetStatusCode, String serviceName, ContentType contentType) {
        return this.performRequest(new Object[]{method, targetStatusCode, serviceName, null, null, null, null, contentType});
    }

    public Response performRequest(RestClient.Method method, int targetStatusCode, String serviceName, ContentType contentType, String urlArguments) {
        return this.performRequest(new Object[]{method, targetStatusCode, serviceName, urlArguments, null, null, null, contentType});
    }

    public Response performRequest(RestClient.Method method, int targetStatusCode, String serviceName, List<List<Object>> parameters, RestClient.ParametersType parametersType, ContentType contentType) {
        return this.performRequest(new Object[]{method, targetStatusCode, serviceName, null, parameters, parametersType, null, contentType});
    }

    public Response performRequest(RestClient.Method method, int targetStatusCode, String serviceName, Object requestBody, ContentType contentType) {
        return this.performRequest(new Object[]{method, targetStatusCode, serviceName, null, null, null, requestBody, contentType});
    }

    public static enum ParametersType {
        FORM,
        QUERY;

        private ParametersType() {
        }
    }

    public static enum Method {
        GET,
        POST,
        DELETE,
        PUT,
        PATCH;

        private Method() {
        }
    }
}