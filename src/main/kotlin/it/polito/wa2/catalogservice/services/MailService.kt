package it.polito.wa2.catalogservice.services

interface MailService {
    fun sendMessage(toMail: String, subject: String, mailBody: String)
}