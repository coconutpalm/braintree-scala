package net.bhardy.braintree.scala.search

class MultipleValueNode[T <: SearchRequest[T], S](nodeName: String, parent: T) extends SearchNode[T](nodeName, parent) {

  def in(items: List[S]): T = {
    assembleMultiValueCriteria(items)
  }

  def in(items: S*): T = {
     in(items.toList)
  }

  def is(item: S): T = {
     in(item)
  }
}