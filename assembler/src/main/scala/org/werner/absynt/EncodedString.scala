package org.werner.absynt

import org.werner.absynt.resource.UnlabeledEncodable

case class EncodedString(string: String) extends UnlabeledEncodable {
  def encodeByte: Seq[Byte] = string.getBytes.toList

  def size: Int = string.length()

  override def toString: String = s"""SETS "$string""""
}