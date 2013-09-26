package com.braintreegateway

import com.braintreegateway.exceptions.NotFoundException
import com.braintreegateway.util.Http
import java.util.{List =>JUList}
import scala.collection.JavaConversions._


/**
 * Provides methods to create, delete, find, and update {@link Customer}
 * objects. This class does not need to be instantiated directly. Instead, use
 * {@link BraintreeGateway#customer()} to get an instance of this class:
 *
 * <pre>
 * BraintreeGateway gateway = new BraintreeGateway(...);
 * gateway.customer().create(...)
 * </pre>
 *
 * For more detailed information on {@link Customer Customers}, see <a href="http://www.braintreepayments.com/gateway/customer-api" target="_blank">http://www.braintreepaymentsolutions.com/gateway/customer-api</a>
 */
class CustomerGateway(http: Http, configuration: Configuration) {
  /**
   * Finds all Customers and returns a {@link ResourceCollection}.
   *
   * @return a { @link ResourceCollection}.
   */
  def all = {
    val response = http.post("/customers/advanced_search_ids")
    new ResourceCollection[Customer](new CustomerPager(this, new CustomerSearchRequest), response)
  }

  private[braintreegateway] def fetchCustomers(query: CustomerSearchRequest, ids: JUList[String]): JUList[Customer] = {
    query.ids.in(ids)
    val response = http.post("/customers/advanced_search", query)
    response.findAll("customer").map{new Customer(_)}
  }

  /**
   * Please use gateway.transparentRedirect().confirmCustomer() instead
   */
  @Deprecated def confirmTransparentRedirect(queryString: String) = {
    val trRequest = new TransparentRedirectRequest(configuration, queryString)
    val node = http.post("/customers/all/confirm_transparent_redirect_request", trRequest)
    new Result[Customer](node, classOf[Customer])
  }

  /**
   * Creates a {@link Customer}.
   *
   * @param request
     * the request.
   * @return a { @link Result}.
   */
  def create(request: CustomerRequest) = {
    val node = http.post("/customers", request)
    new Result[Customer](node, classOf[Customer])
  }

  /**
   * Deletes a {@link Customer} by id.
   *
   * @param id
     * the id of the { @link Customer}.
   * @return a { @link Result}.
   */
  def delete(id: String) = {
    http.delete("/customers/" + id)
    new Result[Customer]
  }

  /**
   * Finds a {@link Customer} by id.
   *
   * @param id
     * the id of the { @link Customer}.
   * @return the { @link Customer} or raises a
   *                     { @link com.braintreegateway.exceptions.NotFoundException}.
   */
  def find(id: String) = {
    if (id == null || (id.trim == "")) throw new NotFoundException
    new Customer(http.get("/customers/" + id))
  }

  /**
   * Finds all Transactions that match the query and returns a {@link ResourceCollection}.
   * See: <a href="http://www.braintreepayments.com/gateway/transaction-api#searching" target="_blank">http://www.braintreepaymentsolutions.com/gateway/transaction-api#searching</a>
   * @return a { @link ResourceCollection}.
   */
  def search(query: CustomerSearchRequest) = {
    val node = http.post("/customers/advanced_search_ids", query)
    new ResourceCollection[Customer](new CustomerPager(this, query), node)
  }

  /**
   * Updates a {@link Customer}.
   *
   * @param id
     * the id of the { @link Customer}.
   * @param request
     * the request.
   * @return a { @link Result}.
   */
  def update(id: String, request: CustomerRequest) = {
    val node = http.put("/customers/" + id, request)
    new Result[Customer](node, classOf[Customer])
  }
}