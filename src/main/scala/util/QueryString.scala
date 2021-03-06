package net.bhardy.braintree.scala.util

import net.bhardy.braintree.scala.Request
import java.io.UnsupportedEncodingException
import java.net.{URLDecoder, URLEncoder}

object QueryString {
  def encodeParam(key: String, value: String): String = {
    val encodedKey = encode(key)
    val encodedValue = encode(value)
    encodedKey + "=" + encodedValue
  }

  def encode(value: String): String = {
    try {
      URLEncoder.encode(value, DEFAULT_ENCODING)
    }
    catch {
      case e: UnsupportedEncodingException => {
        throw new IllegalStateException(DEFAULT_ENCODING + " encoding should always be available")
      }
    }
  }

  def decode(value: String): String = {
    try {
      URLDecoder.decode(value, DEFAULT_ENCODING)
    }
    catch {
      case e: UnsupportedEncodingException => {
        throw new IllegalStateException(DEFAULT_ENCODING + " encoding should always be available")
      }
    }
  }

  val DEFAULT_ENCODING: String = "UTF-8"
}

final class QueryString(content: String = "") {

  private val builder = new StringBuilder(content)

  def append(key: String, value: Any): QueryString = {
    value match {
      case null => this
      case None => this
      case Some(optionalRequest:Request) => appendRequest(key, optionalRequest) // ugly TODO make pretty
      case Some(x:AnyRef) => appendString(key, x.toString)
      case request: Request => appendRequest(key, request)
      case sMap: Map[_,_] => appendMap(key, sMap)
      case other => appendString(key, other.toString)
    }
  }

  def appendEncodedData(alreadyEncodedData: String): QueryString = {
    if (alreadyEncodedData != null && alreadyEncodedData.length > 0) {
      addItem(alreadyEncodedData)
    }
    this
  }

  override def toString: String = {
    builder.toString
  }

  private def appendString(key: String, value: String): QueryString = {
    if (key != null && !key.isEmpty && value != null) {
      addItem(QueryString.encodeParam(key, value))
    }
    this
  }

  private def addItem(item: String): QueryString = {
    builder append separator append item
    this
  }

  private def separator = {
    if (builder.length > 0) "&" else ""
  }

  private def appendRequest(parent: String, request: Request): QueryString = {
    if (request != null) {
      val requestQueryString = request.toQueryString(parent)
      if (requestQueryString.length > 0) {
        addItem(requestQueryString)
      }
    }
    this
  }

  private def appendMap(key: String, value: Map[_, _]): QueryString = {
    import scala.collection.JavaConversions._
    for (keyString <- value.keySet) {
      appendString("%s[%s]".format(key, keyString), value.get(keyString).toString)
    }
    this
  }

}