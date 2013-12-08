package ca.jakegreene.processing

import processing.core.PImage
import processing.core.PApplet
import scala.language.implicitConversions

object AdvancedPImage {
  
  private[AdvancedPImage] case class Colour(red: Float, green: Float, blue: Float)
  
  private[AdvancedPImage] def toColour(colour: Int)(implicit applet: PApplet): Colour = {
    Colour(applet.red(colour), applet.green(colour), applet.blue(colour))
  }
  
  private[AdvancedPImage] def diff(base: Colour, other: Colour): Float = {
    val redDiff = (base.red - other.red)
    val greenDiff = (base.green - other.green)
    val blueDiff = (base.blue - other.blue)
    val diffSquared = (redDiff * redDiff) + (greenDiff * greenDiff) + (blueDiff * blueDiff)
    val diff = Math.sqrt(diffSquared).asInstanceOf[Float]
    return diff
  }
  
  implicit def PImage2AdvancedPImage(image: PImage)(implicit applet: PApplet) = new AdvancedPImage(image)(applet)
  
}

class AdvancedPImage(protected val self: PImage)(protected implicit val applet: PApplet) {
  import AdvancedPImage._
  /**
   * Create a difference image, calculating
   * the difference between the RGB of this
   * and other and setting it as the grey-scale
   * colour of the newly produced image
   */
  def difference(other: PImage): PImage = {
    
    if (other.width != self.width || other.height != self.height) {
      throw new IllegalArgumentException("Provided PImage does not have equal height, width")  
    }
    
    val selfData = self.pixels
    val otherData = other.pixels
    val diffImage = new PImage(self.width, self.height)
    for (x <- 0 to (self.width - 1)) {
      for (y <- 0 to (self.height - 1)) {
        val i = x + (y*self.width)
        val selfColour = toColour(selfData(i))
        val otherColour = toColour(otherData(i))
        val difference = diff(selfColour, otherColour)
        diffImage.pixels(i) = applet.color(difference)
      }
    }
    
    return diffImage
  }
  
  def convolve(kernel: Kernel): PImage = {
    val pixels = convolvePixels(kernel)
    val image = new PImage(self.width, self.height)
    image.pixels = pixels.map{ case (red, green, blue) => applet.color(red, green,blue)}.toArray
    return image
  }
  
  protected def convolvePixels(kernel: Kernel): Seq[(Int, Int, Int)] = {
    for {
      y <- 0 to (self.height - 1)
      x <- 0 to (self.width - 1)
      (red, green, blue) = applyKernel(self, kernel, x, y)
    } yield (red, green, blue)
  }
  
  protected def applyKernel(image: PImage, kernel: Kernel, x: Int, y: Int): (Int, Int, Int) = {
    val red = applyKernelForColour(applet.red, image, kernel, x, y)
    val green = applyKernelForColour(applet.green, image, kernel, x, y)
    val blue = applyKernelForColour(applet.blue, image, kernel, x, y)
    return (red, green, blue)
  }
  
  private def applyKernelForColour(toColour: Int => Float, image: PImage, kernel: Kernel, x: Int, y: Int): Int = {
    val xOffset = kernel.width / 2
    val yOffset = kernel.height / 2
    val factors = for {
      x_k <- 0 to (kernel.width - 1)
      y_k <- 0 to (kernel.height - 1)
      x_i = x + x_k - xOffset + 1
      y_i = y + y_k - yOffset + 1
      index = min(x_i + (y_i * image.width), image.pixels.length - 1)
    } yield (toColour(image.pixels(index)) * kernel.get(x_k, y_k))
    return factors.sum.toInt
  }
  
  def luminance(x: Int, y: Int): Float = {
    val pixel = self.get(x, y)
    return colourLuminance(applet.red(pixel), applet.green(pixel), applet.blue(pixel))
  }
  
  protected def colourLuminance(red: Float, green: Float, blue: Float): Float = {
    // These magic numbers are from the Digital CCIR601 standard
    return (0.299f * red) + (0.587f * green) + (0.114f * blue)
  }
  
  private def min(a: Int, b: Int): Int = {
    if (a < b) a else b
  }

}