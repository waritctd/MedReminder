package com.waritctd.MedReminder.service;

import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class TwilioService {

    public static final String ACCOUNT_SID = "AC831bc0c88980101b9124f5e426356848";
    public static final String AUTH_TOKEN = "576a0e11d9ed8dd5374dd6f7ee0ed3cb";

    public void sendSms(String toPhoneNumber, String messageBody) {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            String fromPhoneNumber = "+12184844635";  //twilio phone num

            // Send the SMS using Twilio API
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(fromPhoneNumber), 
                    messageBody
            ).create();

            System.out.println("SMS sent successfully: " + message.getSid());
        } catch (Exception e) {
            e.printStackTrace();  //stack trace
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
    }
}
