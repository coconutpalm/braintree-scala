package net.bhardy.braintree.scala.gw

import net.bhardy.braintree.scala.org.apache.commons.codec.binary.Base64
import net.bhardy.braintree.scala.util.Crypto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import net.bhardy.braintree.scala.WebhookNotifications

class WebhookTestingGateway(configuration: Configuration) {

  private def buildPayload(kind: WebhookNotifications.Kind, id: String): String = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
    val timestamp = dateFormat.format(new Date)
    val payload = "<notification><timestamp type=\"datetime\">" + timestamp + "</timestamp><kind>" + kind + "</kind><subject>" + subjectXml(kind, id) + "</subject></notification>"
    Base64.encodeBase64String(payload.getBytes).trim
  }

  private def publicKeySignaturePair(stringToSign: String): String = {
    String.format("%s|%s", configuration.publicKey, new Crypto().hmacHash(configuration.privateKey, stringToSign))
  }

  def sampleNotification(kind: WebhookNotifications.Kind, id: String) = {
    Map(
      "payload" -> buildPayload(kind, id),
      "signature" ->  publicKeySignaturePair(buildPayload(kind, id))
    )
  }

  private def subjectXml(kind: WebhookNotifications.Kind, id: String): String = {
    if (kind eq WebhookNotifications.Kind.SUB_MERCHANT_ACCOUNT_APPROVED) {
      merchantAccountXmlActive(id)
    }
    else if (kind eq WebhookNotifications.Kind.SUB_MERCHANT_ACCOUNT_DECLINED) {
      merchantAccountXmlDeclined(id)
    }
    else if (kind eq WebhookNotifications.Kind.TRANSACTION_DISBURSED) {
      transactionXml(id)
    }
    else {
      subscriptionXml(id)
    }
  }

  private def merchantAccountXmlDeclined(id: String): String = {
    "<api-error-response> <message>Credit score is too low</message> <errors> <errors type=\"array\"/> <merchant-account> <errors type=\"array\"> <error> <code>82621</code> <message>Credit score is too low</message> <attribute type=\"symbol\">base</attribute> </error> </errors> </merchant-account> </errors> <merchant-account> <id>" + id + "</id> <status>suspended</status> <master-merchant-account> <id>master_ma_for_" + id + "</id> <status>suspended</status> </master-merchant-account> </merchant-account> </api-error-response>"
  }

  private def merchantAccountXmlActive(id: String): String = {
    "<merchant-account><id>" + id + "</id><master-merchant-account><id>master_merchant_account</id><status>active</status></master-merchant-account><status>active</status></merchant-account>"
  }

  private def subscriptionXml(id: String): String = {
    "<subscription><id>" + id + "</id><transactions type=\"array\"></transactions><add_ons type=\"array\"></add_ons><discounts type=\"array\"></discounts></subscription>"
  }

  private def transactionXml(id: String): String = {
    "<transaction><id>" + id + "</id><amount>100</amount><disbursement-details><disbursement-date type=\"datetime\">2013-07-09T18:23:29Z</disbursement-date></disbursement-details><billing></billing><credit-card></credit-card><customer></customer><descriptor></descriptor><shipping></shipping><subscription></subscription></transaction>"
  }
}