package services;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.approvaltests.Approvals;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matcher;

import java.util.Iterator;
import java.util.List;

public class ResponseValidator {
    public ResponseValidator() {
    }

    public static void assertThatStatusCodeMatchesTheExpected(Response response, int statusCode) {
        Assertions.assertThat(response.statusCode()).isEqualTo(statusCode);
    }

    public static void assertThatResponseMatchesTheApprovedResponse(Response response, List<String> dynamicFields) {
        String responseStr = response.body().prettyPrint();

        String field;
        for(Iterator var3 = dynamicFields.iterator(); var3.hasNext(); responseStr = responseStr.replaceFirst(String.format("\"%s\":.*", field), String.format("\"%s\": \"DYNAMIC_FIELD\",", field))) {
            field = (String)var3.next();
        }

        Approvals.verify(responseStr);
    }

    public static void assertThatResponseMatchesTheSchema(Response response, String schemaPath) {
        ((ValidatableResponse)((ValidatableResponse)response.then()).assertThat()).body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath), new Matcher[0]);
    }
}
