package com.btg.prueba_tecnica_seti.utils;

import com.infobip.ApiException;
import com.infobip.api.SmsApi;
import com.infobip.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SmsInfobip {

    private final SmsApi smsApi;

    @Value("${infobip.sender}")
    private String sender;

    public void enviarSms(String to, String text) {
        SmsMessage message = new SmsMessage()
                .sender(sender)
                .addDestinationsItem(new SmsDestination().to(to))
                .content(new SmsTextContent().text(text));
        SmsRequest request = new SmsRequest().messages(List.of(message));

        try {
            SmsResponse response = smsApi.sendSmsMessages(request).execute();
            System.out.println("Enviado SMS — bulkId: " + response.getBulkId());
        } catch (ApiException e) {
            System.err.println("Error enviado SMS — status: " + e.responseStatusCode());
        }
    }

}
