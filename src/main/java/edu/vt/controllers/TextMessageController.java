/*
 * Created by Osman Balci on 2022.1.7
 * Copyright © 2022 Osman Balci. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.globals.Methods;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Named("textMessageController")
@RequestScoped

// This class sends a Text Message (a.k.a. SMS) to a cellular (mobile) phone
public class TextMessageController {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String cellPhoneNumber;         // Cell phone number to which Text Message to be sent
    private String cellPhoneCarrierDomain;  // Cell phone carrier company's gateway domain name
    private String mmsTextMessage;          // Text message content

    Properties emailServerProperties;       // java.util.Properties
    Session emailSession;                   // javax.mail.Session
    MimeMessage mimeEmailMessage;           // javax.mail.internet.MimeMessage

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String enteredCellPhoneNumber) {
        /*
        The input mask we used in the JSF XHTML page formats the cell phone number as
        (540) 123-4567 to make it visually easy for the user to enter the phone number.

        However, the cell phone number must be formatted only with numbers as 5401234567
        since it is required by the cell phone carrier's gateway for processing.

        Therefore, we need to remove the non-numeric characters inserted by the input mask.

        We remove all non-numeric characters from the entered cell phone number by using
        Regular Expression (RegEx). RegEx "[^0-9.]" means "if not a digit 0 to 9".
         */
        this.cellPhoneNumber = enteredCellPhoneNumber.replaceAll("[^0-9.]", "");
    }

    public String getCellPhoneCarrierDomain() {
        return cellPhoneCarrierDomain;
    }

    public void setCellPhoneCarrierDomain(String cellPhoneCarrierDomain) {
        this.cellPhoneCarrierDomain = cellPhoneCarrierDomain;
    }

    public String getMmsTextMessage() {
        return mmsTextMessage;
    }

    public void setMmsTextMessage(String mmsTextMessage) {
        this.mmsTextMessage = mmsTextMessage;
    }

    /*
    ================
    Instance Methods
    ================
     */
    public void clearTextMessage() {
        cellPhoneNumber = "";
        cellPhoneCarrierDomain = null;
        mmsTextMessage = "";
    }

    /*
    ===================================================================================
    We send the text message in an email to the cell phone carrier's MMS gateway, which
    converts the email into a text message and sends it to the subscriber's cell phone.
    ===================================================================================
     */
    public void sendTextMessage() throws AddressException, MessagingException {

        // Set Email Server Properties
        emailServerProperties = System.getProperties();
        emailServerProperties.put("mail.smtp.port", "587");
        emailServerProperties.put("mail.smtp.auth", "true");
        emailServerProperties.put("mail.smtp.starttls.enable", "true");

        try {
            // Create an email session using the email server properties set above
            emailSession = Session.getDefaultInstance(emailServerProperties, null);
            /*
             Create a Multi-purpose Internet Mail Extensions (MIME) style email
             message from the MimeMessage class under the email session created.
             */
            mimeEmailMessage = new MimeMessage(emailSession);

            /*
             Specify the email address to send the email message containing the text message as

                 5401234567@CellPhoneCarrier's MMS gateway domain

             The designated cell phone number will be charged for the text messaging by its carrier.
             Here are the MMS gateway domain names for some cell phone carriers:

                 mms.att.net         for AT&T            5401234567@mms.att.net
                 pm.sprint.com       for Sprint          5401234567@pm.sprint.com
                 tmomail.net         for T-Mobile        5401234567@tmomail.net
                 vzwpix.com          for Verizon         5401234567@vzwpix.com
                 vmpix.com           for Virgin Mobile   5401234567@vmpix.com

             When the email message is sent to the cell phone number at the MMS gateway domain,
             the message is automatically sent to the cell phone as an MMS text message by the
             domain and the carrier charges the cell phone number for the text messaging.
             */
            mimeEmailMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(cellPhoneNumber + "@" + cellPhoneCarrierDomain));

            /*
             Since some cell phones may not be able to process text messages in the HTML format,
             send the email message containing the text message in Plain Text format.
             */
            mimeEmailMessage.setContent(mmsTextMessage, "text/plain");

            // Create a transport object that implements the Simple Mail Transfer Protocol (SMTP)
            Transport transport = emailSession.getTransport("smtp");

            /*
            Connect to Gmail's SMTP server using the username and password provided.
            For the Gmail's SMTP server to accept the unsecure connection, the
            Cloud.Software.Email@gmail.com account's "Allow less secure apps" option is set to be ON.
             */
            transport.connect("smtp.gmail.com", "Cloud.Software.Email@gmail.com", "nzrvymgfrxpbdmwy");

            // Send the email message containing the text message to the specified email address
            transport.sendMessage(mimeEmailMessage, mimeEmailMessage.getAllRecipients());

            // Close this service and terminate its connection
            transport.close();

            Methods.showMessage("Information", "Success!", "Text Message is Sent!");
            clearTextMessage();

        } catch (AddressException ae) {
            Methods.showMessage("Fatal Error", "Email Address Exception Occurred!",
                    "See: " + ae.getMessage());

        } catch (MessagingException me) {
            Methods.showMessage("Fatal Error",
                    "Email Messaging Exception Occurred! Internet Connection Required!",
                    "See: " + me.getMessage());
        }
    }

}
