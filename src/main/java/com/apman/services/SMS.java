package com.apman.services;

import java.util.Collections;

import com.apman.App;
import com.infobip.ApiClient;
import com.infobip.ApiException;
import com.infobip.ApiKey;
import com.infobip.BaseUrl;
import com.infobip.api.SmsApi;
import com.infobip.model.SmsAdvancedTextualRequest;
import com.infobip.model.SmsDestination;
import com.infobip.model.SmsTextualMessage;

import io.github.cdimascio.dotenv.Dotenv;

public class SMS {
    private static final Dotenv dotenv = App.getDotenv();
    private static final String BASE_URL = dotenv.get("INFOBIP_BASE_URL");
    private static final String API_KEY = dotenv.get("INFOBIP_API_KEY");

    // Create the API client and the Send SMS API instances.

    private static final ApiClient apiClient = ApiClient.forApiKey(ApiKey.from(API_KEY))
        .withBaseUrl(BaseUrl.from(BASE_URL))
        .build();
    private static SmsApi sendSmsApi = new SmsApi(apiClient);
    
    public static void send(String phNumber, String message) throws ApiException {
        // Create a message to send.
        var smsMessage = new SmsTextualMessage()
                .addDestinationsItem(new SmsDestination().to(phNumber))
                .text(message);

        // Create a send message request.
        var smsMessageRequest = new SmsAdvancedTextualRequest()
                .messages(Collections.singletonList(smsMessage));


        // Send the message.
        var smsResponse = sendSmsApi.sendSmsMessage(smsMessageRequest).execute();
        System.out.println("Response body: " + smsResponse);

        // Get delivery reports. It may take a few seconds to show the above-sent message.
        var reportsResponse = sendSmsApi.getOutboundSmsMessageDeliveryReports().execute();
        System.out.println(reportsResponse.getResults());

    }
}
