package com.braintreegateway.integrationtest

import _root_.org.scalatest.matchers.MustMatchers
import com.braintreegateway._
import com.braintreegateway.testhelpers.GatewaySpec
import java.util.Random

class MerchantAccountSpec extends GatewaySpec with MustMatchers {

  describe("create") {
    onGatewayIt("requires no id") {
      gateway =>
        val result = gateway.merchantAccount.create(creationRequest)
        result must be ('success)
        val ma = result.getTarget
        ma.getStatus must be === MerchantAccount.Status.PENDING
        ma.getMasterMerchantAccount.getId must be === "sandbox_master_merchant_account"
        ma must be ('subMerchant)
        ma.getMasterMerchantAccount must not be ('subMerchant)
    }

    onGatewayIt("will use id if passed") {
      gateway =>
        val randomNumber = new Random().nextInt % 10000
        val subMerchantAccountId = "sub_merchant_account_id_%d".format(randomNumber)
        val request = creationRequest.id(subMerchantAccountId)
        val result = gateway.merchantAccount.create(request)
        result must be ('success)
        val ma = result.getTarget
        ma.getStatus must be === MerchantAccount.Status.PENDING
        subMerchantAccountId must be ===  ma.getId
        ma.getMasterMerchantAccount.getId must be === "sandbox_master_merchant_account"
        ma must be ('subMerchant)
        ma.getMasterMerchantAccount must not be ('subMerchant)
    }

    onGatewayIt("handles unsuccessful results") {
      gateway =>
        val result = gateway.merchantAccount.create(new MerchantAccountRequest)
        val errors = result.getErrors.forObject("merchant-account").onField("master_merchant_account_id")
        errors.size must be === 1
        val code = errors.get(0).getCode
        code must be === ValidationErrorCode.MERCHANT_ACCOUNT_MASTER_MERCHANT_ACCOUNT_ID_IS_REQUIRED
    }
  }

  private def creationRequest = {
    new MerchantAccountRequest().
      applicantDetails.firstName("Joe").lastName("Bloggs").email("joe@bloggs.com").phone("555-555-5555").
      address.streetAddress("123 Credibility St.").postalCode("60606").locality("Chicago").region("IL").done.
      dateOfBirth("10/9/1980").ssn("123-456-7890").routingNumber("122100024").accountNumber("98479798798").
      taxId("123456789").companyName("Calculon's Drama School").done.tosAccepted(true).
      masterMerchantAccountId("sandbox_master_merchant_account")
  }

}