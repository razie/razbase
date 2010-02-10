package razie.base

/**
 * actions execute in a context of objects available at that time in that environment.
 * 
 * a script context is more complex than just AttributeAccess, it may include a hierarchy of
 * contexts, hardcode mappings etc. This class may go away and be replaced with the jdk1.6
 * scriptables.
 * 
 * it's used to run activities and scripts $
 * 
 * You can define functions, which are evaluated every time
 * 
 * @author razvanc99
 */
trait ActionContext extends AttrAccess {

}
