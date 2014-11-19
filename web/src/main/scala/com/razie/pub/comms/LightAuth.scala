package com.razie.pub.comms

import razie.base._
import java.net._

/** 
 * simple rule-based authentication - init to replace default LightAuth and then add rules. Rules are executed in order and the first match wins. After you add all the rules, make sure to lock this so  
 * 
 * here's the normal init sequence:
 * 
 * <code>
 * LightAuth.underLockAndKey {
 *   LightAuth.init
 *   LightAuth.ipMatches (".*", LightAuthType.INCLOUD)
 * }
 * </code>
 * 
 * @author razvanc
 */
object LightAuth {
  def me = LightAuthBase.instance.asInstanceOf[RuleBasedAuth]
  
  def init (prefix:String="") { LightAuthBase.init(new RuleBasedAuth(prefix)) }

  // add a rule: client IPs that match will get the level
  def ipMatches (regexp:String, perm:LightAuthType) {
    me.rule(
          (s:Socket, u:String, h:AttrAccess) => 
          if (s.getInetAddress().getHostAddress().matches(regexp)) Some(perm)
          else None
          )
  }

  // add a user rule
  def rule(r:(Socket, String, AttrAccess) => Option[LightAuthType]) {
     me.rule(r)
  }
  
  def lock { me.lock }
  
  def underLockAndKey (f: => Unit) {
     f
     lock
  }
}

/** since the defaults are based on agent clouds, here's one you can use for any other project */
class RuleBasedAuth (prefix:String="") extends LightAuthBase (prefix) {
  private[this] val rules = razie.Listi [(Socket, String, AttrAccess) => Option[LightAuthType]] ()
  private[this] var locked = false; // TODO stupid anti-hack
  
  def rule(r:(Socket, String, AttrAccess) => Option[LightAuthType]) {
     if (!locked) rules.append (r)
  }
  def lock { locked=true; LightAuthBase.lock(); }
  
  override def iauthorize (socket:Socket , url:String , httpArgs:AttrAccess ) : LightAuthType = {
    rules.foreach (r => {r(socket, url, httpArgs).map (perm=>{
       razie.Log.log("HTTP_CLIENT_AUTH: ip=" + socket.getInetAddress.getHostAddress + " as " + perm)
       return perm 
    })})
    // failed all rules, go to super
    super.iauthorize(socket, url, httpArgs)
  }
}
