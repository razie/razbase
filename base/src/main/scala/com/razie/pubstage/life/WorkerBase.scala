package com.razie.pubstage.life

trait WorkerBase {
  val delegate : WorkerBase
  
  def dying(): Boolean = delegate.dying()
  def candienow(): Unit = delegate.candienow()
  def updateProgress(newProgress: Int, newProgressCode: String): Unit = 
    delegate.updateProgress(newProgress, newProgressCode)
}

object WorkerBase extends WorkerBase {
  val delegate = new WorkerBaseImpl()
  
  override def dying(): Boolean = {false}
  override def candienow(): Unit = {}
  override def updateProgress(newProgress: Int, newProgressCode: String): Unit = {}
}
