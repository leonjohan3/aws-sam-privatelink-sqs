package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import java.time.Duration;
import java.util.HashMap;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final SqsClient sqsClient = SqsClient.builder()
        .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .httpClient(UrlConnectionHttpClient.builder().connectionTimeout(Duration.ofMillis(5_000)).socketTimeout(Duration.ofMillis(5_000)).build())
        .build();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        final var headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        final var response = new APIGatewayProxyResponseEvent().withHeaders(headers);

        final var message = "blah";
        final var sendMessageRequest = SendMessageRequest.builder()
            .queueUrl(System.getenv("QUEUE_URL"))
            .messageBody(message)
            .build();
        final var sendMessageResponse = sqsClient.sendMessage(sendMessageRequest);
        final var output = String.format("{ \"hello\": \"world\", \"messageId\": \"%s\" }", sendMessageResponse.messageId());

        return response
            .withStatusCode(200)
            .withBody(output);
    }
}
