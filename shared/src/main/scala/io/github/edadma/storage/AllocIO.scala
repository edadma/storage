package io.github.edadma.storage

import collection.mutable.ListBuffer

class AllocIO(io: IO) extends MemIO {
  private[storage] val backpatches = new ListBuffer[(IO, Long, Long)]
  private[storage] var base: Long = _

  private[storage] lazy val bucket = java.lang.Long.numberOfTrailingZeros(allocSize) - sizeShift

  charset = io.charset
  pwidth = io.pwidth
  cwidth = io.cwidth
  bucketsPtr = io.bucketsPtr

  def backpatch(io: IO, src: Long): Unit = backpatches += ((io, src, pos))

  def writeBackpatches(): Unit = {
    for ((io, src, target) <- backpatches)
      io.putBig(src, base + target)
  }

  private[storage] def allocSize = IO.power_ceil(buf.size + 1) max minblocksize

  override def toString = f"[alloc: base = $base%x, size = $size%x]"
}
