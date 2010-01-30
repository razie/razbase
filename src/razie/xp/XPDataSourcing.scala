/*
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package razie.xp

import razie.{GPath, XP, M}

/** 
 * Generic data sourcing API
 */
trait XPUntypedDataSouce[T<:Any] {
   /** find something... */
   def xp (xpath:GPath) : M[T]
   def xpa (xpath:GPath) : M[String]
}

/** 
 * Generic data sourcing API
 * 
 * Types are: L - container, E - element (any), A - attribute (String)
 */
trait XPDataSouce[E<:Any, L[_], A] {
   /** find one element */
   def xpe (xpath:GPath) : Option[E]
   /** find a list of elements */
   def xpl (xpath:GPath) : L[E]
   /** find one attribute */
   def xpa (xpath:GPath) : Option[A]
   /** find a list of attributes */
   def xpla (xpath:GPath) : L[A]
}
