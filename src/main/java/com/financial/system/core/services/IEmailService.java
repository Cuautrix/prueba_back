package com.financial.system.core.services;

import com.financial.system.core.models.entities.Client;
import org.springframework.scheduling.annotation.Async;

public interface IEmailService {
 @Async("taskExecutor")
 void sendEmail(Client client, String subject);

 @Async("taskExecutor")
 void sendRequestChangePassword(Client client, String subject, String token);

}
