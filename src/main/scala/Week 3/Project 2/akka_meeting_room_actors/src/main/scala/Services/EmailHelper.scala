package Services

import jakarta.mail.{Authenticator, Message, PasswordAuthentication, Session, Transport}
import jakarta.mail.internet.{InternetAddress, MimeMessage}

object EmailHelper {
  private val smtpHost = "smtp.gmail.com"
  private val smtpPort = "587"
  private val username = "amartyapatil879@gmail.com"
  private val password = "xcsi ygwl ltjq fzrw"

  private val session: Session = {
    val props = new java.util.Properties()
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.smtp.host", smtpHost)
    props.put("mail.smtp.port", smtpPort)

    Session.getInstance(props, new Authenticator() {
      override protected def getPasswordAuthentication: PasswordAuthentication =
        new PasswordAuthentication(username, password)
    })
  }

  def sendEmail(to: String, subject: String, body: String): Unit = {
    try {
      val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(username))
      message.setRecipients(Message.RecipientType.TO, to)
      message.setSubject(subject)
      message.setText(body)

      Transport.send(message)
      println(s"📨 Email sent to $to")
    } catch {
      case e: Exception =>
        println(s"❌ Email sending failed: ${e.getMessage}")
    }
  }
}
