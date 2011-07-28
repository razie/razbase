package com.razie.pubstage.life

import razie.base.ActionItem

class WorkerBaseImpl extends WorkerBase {
  val delegate = null
  
  override def dying(): Boolean = Worker.dying()

  override def candienow(): Unit = {
    if (Worker.dying()) {
      throw new BeingDyingRtException();
    }
  }

  override def updateProgress(newProgress: Int, newProgressCode: String): Unit = {
    Worker.updateProgress(newProgress, newProgressCode)
  }
}

