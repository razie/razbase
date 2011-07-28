package com.razie.pubstage.data;

import java.util.ArrayList;


/**
 * a list structure
 * 
 * TODO detailed docs
 * 
 * @author razvanc
 * @param [T]
 */
trait ListStruc[T] extends Structure[T] {
  def foreach (f : T => Unit) : Unit
  def map[B] (f : T => B) : ListStruc[B]
  def flatMap[B] (f : T => ListStruc[B]) : ListStruc[B]
  def filter (f : T => Boolean) : ListStruc[T]
}

class ListStrucImpl[T] (val t:List[T]) extends StrucImpl[T] (t.head) with ListStruc[T] {
  override def foreach (f : T => Unit) { t foreach f }
  override def map[B] (f : T => B) : ListStruc[B] = new ListStrucImpl (t map f)
  override def flatMap[B] (f : T => ListStruc[B]) : ListStruc[B] = new ListStrucImpl (t flatMap {x:T => (f(x)).asInstanceOf[ListStrucImpl[B]].t} )
  override def filter (f : T => Boolean) : ListStruc[T] = new ListStrucImpl (t filter f)
}
