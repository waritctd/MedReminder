package com.waritctd.MedReminder.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import com.waritctd.MedReminder.service.TwilioService;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;

@SpringBootTest
@ExtendWith(MockitoExtension.class)  
class TwilioServiceTest {

    @Test
    void testSendSmsSuccess() {
        // Arrange
        TwilioService twilioService = new TwilioService();
        String toPhoneNumber = "+1234567890";  
        String messageBody = "Test message";
        
        Message mockMessage = mock(Message.class);
        
        when(mockMessage.getBody()).thenReturn(messageBody);
        
        MessageCreator mockMessageCreator = mock(MessageCreator.class);
        
        when(mockMessageCreator.create()).thenReturn(mockMessage);
        
        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            mockedMessage.when(() -> Message.creator(
                    any(PhoneNumber.class),
                    any(PhoneNumber.class),
                    anyString()
            )).thenReturn(mockMessageCreator);  

            twilioService.sendSms(toPhoneNumber, messageBody);

            verify(mockMessageCreator).create();  
            verify(mockMessage).getBody(); 
        }
    }
}

