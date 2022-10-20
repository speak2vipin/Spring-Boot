package com.vipin.practice.asynchronouscommunicationplainhttpclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {
  @Value("${thread.sleep}")
  private int threadSleep;
  
  @Value("${spring.pp.userpass}")
  private String userPass;
  
  @Value("${spring.pp.uri}")
  private String uri;
  
  static volatile int count;
  
  static volatile Long previousTime;
  
  @JmsListener(destination = "vipin.queue", concurrency = "150-250")
  public void debitCompletion(String uid) throws Exception {
    try {
      Thread.sleep(this.threadSleep);
      String resp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
      		+ "<ns0:debitcompletedrequest xmlns:ns0=\"http://www.vipin.com/em/emm\"><transactionid>" + 
        uid + "</transactionid>" + 
        "<externaltransactionid>757309322687872173</externaltransactionid>" + 
        "<receiverinfo> <fri>FRI:cisservice.sp/USER</fri> </receiverinfo>" + "<status>SUCCESSFUL</status>" + 
        "</ns0:debitcompletedrequest>";
      String httpurl = this.uri;
      HttpURLConnection httpCon = null;
      try {
        synchronized (this) {
          URL url = new URL(this.uri);
          httpCon = (HttpURLConnection)url.openConnection();
          httpCon.setDoOutput(true);
          httpCon.setRequestMethod("POST");
          httpCon.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
          httpCon.setRequestProperty("Accept", "text/xml");
          httpCon.setRequestProperty("Content-Type", "text/xml");
          httpCon.setRequestProperty("Content-Encoding", "UTF-8");
          String basicAuth = "Basic " + new String(DatatypeConverter.printBase64Binary(this.userPass.getBytes()));
          httpCon.setRequestProperty("Authorization", basicAuth);
          OutputStream os = httpCon.getOutputStream();
          OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
          osw.write(resp);
          osw.flush();
          osw.close();
          os.close();
          httpCon.connect();
          StringBuffer response = new StringBuffer();
          int responseCode = 0;
          responseCode = httpCon.getResponseCode();
          BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
          String inputLine;
          while ((inputLine = in.readLine()) != null)
            response.append(inputLine); 
          in.close();
        } 
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        httpCon.disconnect();
      } 
    } catch (InterruptedException e) {
      e.printStackTrace();
    } 
  }
  
  private static void writeUsingFileWriter(String data) {
    File file = new File("C:\\TPS_File.txt");
    FileWriter fr = null;
    try {
      fr = new FileWriter(file);
      fr.write(data);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        fr.close();
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } 
  }
}
