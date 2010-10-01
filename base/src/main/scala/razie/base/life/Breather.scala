/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.base.life;

/**
 * beings live in the environment. They breathe at the mercy of the Lifegiver.
 * 
 * @author razvanc99
 */
trait Breather extends Being {
    /** beings take life one breath at a time, whenever the lifegiver wants */
    def breathe() : Unit
}
