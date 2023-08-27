package com.apman.services;

import java.util.ArrayList;
import java.util.List;

import com.apman.App;
import com.infobip.ApiClient;
import com.infobip.ApiException;
import com.infobip.ApiKey;
import com.infobip.BaseUrl;
import com.infobip.api.EmailApi;

import io.github.cdimascio.dotenv.Dotenv;

public class Email {
    private static final Dotenv dotenv = App.getDotenv();
    private static final String BASE_URL = dotenv.get("INFOBIP_BASE_URL");
    private static final String API_KEY = dotenv.get("INFOBIP_API_KEY");
    private static final String SENDER_EMAIL_ADDRESS = dotenv.get("INFOBIP_SENDER_EMAIL_ADDRESS");


    // Create the API client and the Email API instances.
    private static final ApiClient apiClient = ApiClient.forApiKey(ApiKey.from(API_KEY))
            .withBaseUrl(BaseUrl.from(BASE_URL))
            .build();

    private static final EmailApi sendEmailApi = new EmailApi(apiClient);

    public static final void send(String receipientEmailAddress, String subject, String text) throws ApiException {

        // Create the email and send it.
        var to = new ArrayList<>(List.of(receipientEmailAddress));
        var emailResponse = sendEmailApi
                .sendEmail(to)
                .from(SENDER_EMAIL_ADDRESS)
                .subject(subject)
                .text(text)
                .execute();

        System.out.println("Response body: " + emailResponse);

            // Get delivery reports. It may take a few seconds to show the above-sent message.
        var reportsResponse = sendEmailApi.getEmailDeliveryReports().execute();
        System.out.println(reportsResponse.getResults());
    }

}
