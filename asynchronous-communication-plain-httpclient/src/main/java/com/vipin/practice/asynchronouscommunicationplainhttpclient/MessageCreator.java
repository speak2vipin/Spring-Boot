package com.vipin.practice.asynchronouscommunicationplainhttpclient;

import java.nio.ByteBuffer;
import java.util.UUID;
import javax.jms.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageCreator {
  @Autowired
  private JmsMessagingTemplate jmsMessagingTemplate;
  
  @Autowired
  private Queue queue;
  
  @RequestMapping({"/rest/debit"})
  @ResponseBody
  public String debitIntimation(String... arg0) throws Exception {
    byte[] byteArrray = UUID.randomUUID().toString().getBytes();
    Long uid = Long.valueOf(ByteBuffer.wrap(byteArrray).asLongBuffer().get());
    String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    		+ "<p:debitresponse xmlns:iso=\"urn:iso:std:iso:20022:tech:xsd\" "
    		+ "xmlns:op=\"http://www.vipin.com/em/emm/v1_0/common\" "
    		+ "xmlns:p=\"http://www.vipin.com/em/emm/financial/v1_0\" "
    		+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
    		+ "xsi:schemaLocation=\"http://www.vipin.com/em/emm/financial/v1_0 debit.xsd \">  <transactionid>" + 
      
      uid + 
      "</transactionid>" + 
      "<status>PENDING</status></p:debitresponse> ";
    this.jmsMessagingTemplate.convertAndSend(this.queue, uid);
    System.out.println("Message has been put to queue by sender");
    System.out.println("uid sent: " + uid);
    return response;
  }
}
