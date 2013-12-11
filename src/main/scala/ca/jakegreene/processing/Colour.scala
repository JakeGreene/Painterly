package ca.jakegreene.processing

import processing.core.PApplet
import scala.language.implicitConversions

object Colour {
  /**
   * Convert an RGBA Int to a Colour object
   */
  def apply(colour: Int)(implicit applet: PApplet): Colour = {
    Colour(applet.red(colour), applet.green(colour), applet.blue(colour))
  }
  
  def diff(base: Colour, other: Colour): Float = {
    val redDiff = (base.red - other.red)
    val greenDiff = (base.green - other.green)
    val blueDiff = (base.blue - other.blue)
    val diffSquared = (redDiff * redDiff) + (greenDiff * greenDiff) + (blueDiff * blueDiff)
    val diff = Math.sqrt(diffSquared).asInstanceOf[Float]
    return diff
  }
  
  implicit def Int2Colour(rgba: Int)(implicit applet: PApplet) = Colour(rgba)
}

case class Colour(red: Float, green: Float, blue: Float)
