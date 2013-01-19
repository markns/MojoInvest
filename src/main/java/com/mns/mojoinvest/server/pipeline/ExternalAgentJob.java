package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;


class ExternalAgentJob extends Job1<String, String> {

    private static final Logger log = Logger.getLogger(ExternalAgentJob.class.getName());

    @Override
    public Value<String> run(String userEmail) {

        // Invoke ComplexJob on three promised values
        PromisedValue<String> x = newPromise(String.class);

//    FutureValue<Integer> intermediate = futureCall(new ComplexJob(), x, y, z);

        // Kick off the process of retrieving the data from the external agent
        getFromUser(userEmail, x.getHandle());

        // Send the user the intermediate result and ask for one more integer
//    FutureValue<Integer> oneMoreInt = futureCall(new PromptJob(), intermediate, immediate(userEmail));

        // Invoke MultJob on intermediate and oneMoreInt
        return x;
    }

    public static void getFromUser(String userEmail, String promiseHandle) {
        // 1. Send the user an e-mail containing the prompt.
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        try {

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(userEmail, "Mojoinvest Admin"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
            msg.setSubject("Provide ishares session id");

            String url = "http://www.mojoinvest.com/tools/isession?ph=" + promiseHandle;
            log.info(url);
            String text = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n" +
                    "\"http://www.w3.org/TR/html4/strict.dtd\">\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<title> Page title</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "Enter ishares session id " +
                    "<a href=\"" + url + "\">here</a>" +
                    "</body>\n" +
                    "</html>\n";

            // HTML version
            final MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(text, "text/html");
            // Create the Multipart.  Add BodyParts to it.
            final Multipart mp = new MimeMultipart();
            mp.addBodyPart(htmlPart);
            // Set Multipart as the message's content
            msg.setContent(mp);

            Transport.send(msg);

        } catch (AddressException e) {
            log.warning("Unable to send email " + e.toString());
        } catch (MessagingException e) {
            log.warning("Unable to send email " + e.toString());
        } catch (UnsupportedEncodingException e) {
            log.warning("Unable to send email " + e.toString());
        }


        // 2. Ask user to submit one more integer on some web page.
        // 3. promiseHandle is a query string argument
        // 4. Handler for submit invokes submitPromisedValue(promiseHandle, value)
    }
}