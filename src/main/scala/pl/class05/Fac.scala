package pl.class05

import scala.annotation.tailrec

object Fac {
  
  def facNaive(n: Int): Int =
    if (n == 0) 1 else n * facNaive(n - 1)

  def facLoop(n0: Int): Int = {
    var acc = 1
    var n = n0

    while (n != 0) {
      acc = n * acc
      n = n - 1
    }

    acc
  }

  @tailrec def facTail(acc: Int, n: Int): Int = {
    if (n == 0) acc else facTail(n * acc, n - 1)
  }

  def fac(n0: Int): Int = facTail(1, n0)
}
