package razie.om5

import razie.base._

//--------------- foundation 

case class Key (val meta:String, val id:String)
case class QKey (val q:Query) extends Key (q.meta, "")
case class Query (val meta:String, val name:String, val args:String*)

case class Entity (val key:Key) extends razie.AA {
   override def toString = "Entity: " + key + " - " + super.toString
}

trait IsSpec {
	def make : Entity
}
trait HasSpec {
   def specKey:Key
}

// defn is "name:type=defaultvalue"
case class Parm (val parmDefn:String) {
   lazy val spec = AttrSpec apply parmDefn
}

//--------------- entities

object Service {
   def apply (name:String) : Service = ServiceSpec (name) make
//   def CF (name:String) : CFService = ServiceSpec (name) make
//   def NF (name:String) : NFService = ServiceSpec (name) make
}

object Product {
   def apply (name:String) : Product = ProductSpec (name) make
}


     class Service (id:String, val specKey:Key) extends Entity (Key("Service", id)) with HasSpec
case class CFService (id:String, override specKey:Key) extends Service (id,specKey) with HasSpec
case class RFService (id:String, override specKey:Key) extends Service (id,specKey) with HasSpec
case class Product (id:String, val spec:ProductSpec) extends Entity (Key("Product", id)) with HasSpec {
  def decompose : Seq[CFService] = Nil
  def specKey:Key = spec.key
}

//--------------- specs

case class ServiceSpec (name:String) extends Entity (Key("SSpec", name)) with IsSpec {
	override def make = new Service ("", this.key)
 def actions : List[Action] = Nil 
	def parms : List[Parm] = Nil
}
     
case class ProductSpec (name:String) extends Entity (Key("PSpec", name)) with IsSpec {
	override def make () = new Product ("", this)

	def actions : List[Action] = Nil 
	def parms : List[Parm] = Nil
	
	def decompose : List[ServiceSpec] = Nil
}

case class Action (val name:String) {
	def apply[T<:Entity] (e:T, attr:String*) = new Item[T] (e, this) 
	def apply[T<:Entity] (e:Key, attr:String*) = new Item (e, this)
	def apply[T<:Entity] (e:Query, attr:String*) = new Item (QKey (e), this)
}

case object Add extends Action ("add")
case object Update extends Action ("update")
case object Delete extends Action ("delete")


object Implicits {
   implicit def P (s:String) : Parm = Parm (s)
   implicit def Parms (s:String*) : Seq[Parm] = s map P _ toList
}

import Implicits._

//---------------------------- orders

case class Item [T<:Entity] (val entityKey:Key, val action:Action, val attr:String*) 
   extends Entity (Key("Item", "")) {
	var entity : Option[T] = None
	
   def this (e:T, a:Action) = {
		this (e.key, a)
		this.entity = Some(e)
	}
	
	override def toString = "Item: " + action.name + " " + entity
}

class Request[T<:Entity] (val items:Seq[Item[_<:T]]) 
   extends Entity (Key("Request", ""))

case class Order[T<:Entity] (override items:Seq[Item[T]]) extends Request (items) {
	override def toString = "Order: " + items.mkString
}

//------------------------ gremlins

class Wf extends Entity (Key("Wf", ""))
class BigWf

//------------------------ specs

object Email extends ServiceSpec ("Email") {
   override def actions = Add :: Update :: Delete :: Nil
   val parmList @ List(user,domain,pass) = Parms ("user", "domain", "pass")
   def apply (user:String,domain:String,pass:String) = CFService ("?", this.key)
   override def parms = parmList
}

object Webspace extends ServiceSpec ("Email") {
   override def actions = Add :: Update :: Delete :: Nil
   val parmList @ List(domain) = Parms ("domain")
   def apply (domain:String) = CFService ("?", this.key)
   override def parms = parmList
}

object Internet extends ProductSpec ("Internet") {
   override def actions = Add :: Update :: Delete :: Nil
   val parmList @ List(user,domain,pass) = Parms ("user", "domain", "pass")
   override def parms = parmList
   
//   override def parms = parmList
//   override def parms = P("user") :: P("domain") :: P("pass") :: Nil
   
//   override def services = Email :: Webspace :: Nil
   
//   def apply (user:String, domain:String, pass:String) = new Product ("?", this) 
   def apply (user:String, domain:String, pass:String) : Product = new PInternet (user, domain, pass)
   
   override def decompose () = Email :: Webspace :: Nil
}

class PInternet (val user:String, val domain:String, val pass:String) extends Product ("Internet", Internet) {
//   override def actions = Add :: Update :: Delete :: Nil
//   val parmList @ List(user,domain,pass) = Parms ("user", "domain", "pass")
//   override def parms = parmList
   override def decompose () = 
      Email (user,domain,pass) :: 
      Webspace (domain) :: 
      Nil
   
//   override def services = Email :: Nil
}

//------------------------ decomposing

abstract class Decomp (val who:Entity with IsSpec) {
	def decompose : List[Entity]
}
abstract class ProdDecomp (override val who:ProductSpec) extends Decomp (who) {
	def decompose : List[Entity] 
}

object InternetProdDecomp extends ProdDecomp (Internet) {
   override def decompose () = Email :: Webspace :: Nil
}

//--------------------------- samples

object Samples {
	def serviceOrder = Order[Service] (
			Add (Service("email")) :: 
			Update (Service("phone"), "number", "(905)713-3503") :: 
			Delete (Service("voicemail")) :: Nil
			)
			
	def prodOrder = Order[Product] (
			Add (Internet(user="john", domain="me.com", pass="12345")) :: Nil
//			Add (Product ("?", Internet.key, (user="john", domain="me.com", pass="12345")) :: Nil
			)
}

object RunMe extends Application {
   val po = Samples.prodOrder
   println ("po: " + po)
   val so = OM.executepo(po)
   println ("so: " + so)
}

//-------------------------- apis

object OM {
	def start : BigWf = new BigWf
	def executerso (o:Order[RFService]) : Wf = null
	def executecso (o:Order[CFService]) : Order[RFService] = null
	def executepo  (o:Order[_<:Product]) : Order[CFService] = Order[CFService] (o.items .flatMap {
	   i => i.entity match {
	      case Some(p) => p.decompose.map (new Item(_,i.action))
	      case None => Nil
	   }
	})
}

//