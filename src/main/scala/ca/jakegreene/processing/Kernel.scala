package ca.jakegreene.processing

object Kernel {
  /**
   * The data is arranged (y)(x) so that 
   * the array can be built
   * Array( Array(...),
   *        Array(...),
   *        Array(...), ...
   *       )
   * and look natural
   */
  def apply(modifiers: Array[Array[Float]]) = new Kernel(modifiers)
}

class Kernel(val modifiers: Array[Array[Float]]) {
  def width = modifiers(0).length
  def height = modifiers.length

  def get(x: Int, y: Int): Float = {
    modifiers(y)(x) //(y, x) on purpose
  }
}