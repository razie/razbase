package razie.base.life

import razie.base.ActionItem

/**
 * beings live in the environment. They live  at the mercy of the Lifegiver.
 * 
 * <p>
 * Each Being must be able to be nice and answer the simple questions below
 * 
 * <p>The underlying principle is that any active object in an application (a server thread for example) 
 * must answer these questions. It is unacceptable for a user to NOT know exactly what is going on at all times.
 * 
 * @author razvanc99
 */
trait Being {
    /** beings should be nice and tell who they are */
    def whoAreYou() : ActionItem
    
    /** beings should be nice and answer what they're doing right now (status in nerdsspeak) */
    def whatAreYouDoing() : ActionItem
}
