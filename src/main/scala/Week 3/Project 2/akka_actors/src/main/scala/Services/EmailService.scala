package Services


import jakarta.mail._
import jakarta.mail.internet._

import java.util.Properties

class EmailService(smtpHost: String, smtpUser: String, smtpPass: String, smtpPort: String) {

  private val props = new Properties()
  props.put("mail.smtp.auth", "true")
  props.put("mail.smtp.starttls.enable", "true")
  props.put("mail.smtp.host", smtpHost)
  props.put("mail.smtp.port", smtpPort)

  private val session = Session.getInstance(props, new Authenticator() {
    override protected def getPasswordAuthentication =
      new PasswordAuthentication(smtpUser, smtpPass)
  })

  def sendEmail(to: String, subject: String, body: String): Unit = {
    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress(smtpUser))
    message.setRecipients(Message.RecipientType.TO, to)
    message.setSubject(subject)
    message.setText(body)

    Transport.send(message)
    println(s"Email sent to $to")
  }
}

