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
  
  implicit def PImage2AdvancedPImage(image: PImage) = new AdvancedPImage(image)
  
}

class AdvancedPImage(self: PImage) {
  import AdvancedPImage._
  /**
   * Create a difference image, calculating
   * the difference between the RGB of this
   * and other and setting it as the grey-scale
   * colour of the newly produced image
   */
  def difference(other: PImage)(implicit applet: PApplet): PImage = {
    
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

}