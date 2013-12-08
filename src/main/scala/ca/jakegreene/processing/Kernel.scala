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
  
  private val sobelY = Array(Array[Float](1, 2, 1),
                    Array[Float](0, 0, 0),
                    Array[Float](-1, -2, -1))
  val sobelKernelY = Kernel(sobelY)
  private val sobelX = Array(Array[Float](1, 0, -1),
                     Array[Float](2, 0, -2),
                     Array[Float](1, 0, -1))
  val sobelKernelX = Kernel(sobelX) 
}

class Kernel(val modifiers: Array[Array[Float]]) {
  def width = modifiers(0).length
  def height = modifiers.length

  def get(x: Int, y: Int): Float = {
    modifiers(y)(x) //(y, x) on purpose
  }
}