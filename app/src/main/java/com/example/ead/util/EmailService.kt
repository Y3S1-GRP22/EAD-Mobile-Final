package com.example.ead.util

import android.content.Context
import android.os.AsyncTask
import java.util.*
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


@Suppress("DEPRECATION")
class EmailService(private val context: Context, private val email: String, private val subject: String, private val message: String) :
    AsyncTask<Void?, Void?, Void?>() {

    private var session: Session? = null

    override fun doInBackground(vararg params: Void?): Void? {
        // SMTP server configuration
        val properties = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.socketFactory.port", "465")
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            put("mail.smtp.auth", "true")
            put("mail.smtp.port", "465")
        }

        session = Session.getDefaultInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication("nonamenecessary0612@gmail.com", "ekbgdpcvlpdiciws")
            }
        })

        try {
            val mimeMessage = MimeMessage(session).apply {
                setFrom(InternetAddress("nonamenecessary0612@gmail.com"))
                addRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
                subject = this@EmailService.subject
                setText(this@EmailService.message)
            }

            Transport.send(mimeMessage)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }

        return null
    }
}
