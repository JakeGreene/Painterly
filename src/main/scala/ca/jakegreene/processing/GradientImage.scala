package ca.jakegreene.processing

import processing.core.PImage
import processing.core.PApplet

case class Gradient(pixels: Array[Array[Float]])

object GradientImage {
  def apply(image: PImage)(implicit applet: PApplet) = new GradientImage(image) 
}

class GradientImage(val image: PImage)(implicit apple: PApplet) extends AdvancedPImage(image) { 
  val xGradient = createGradient(Kernel.sobelKernelX)
  val yGradient = createGradient(Kernel.sobelKernelY)
  
  private def createGradient(sobel: Kernel): Gradient = {
    /*
     * The magnitude of the values will be in the range [-1020, 1020] due to the nature of the Sobel filter.
     * The extreme cases have 255 on one side and 0 on the other, resulting in 1*255 + 2*255 + 1*255 = 1020
     * and (-1)*255 + (-2)*255 + (-1)*255 = -1020
     * 
     */ 
    val gradientValues = this.convolvePixels(sobel).map{ case (red, green, blue) => colourLuminance(red, green, blue)}
    val gradientMatrix = gradientValues.grouped(self.width).map(_.toArray).toArray
    		
    return Gradient(gradientMatrix)
  }
  
  def xGradientImage(): PImage = {
    toImage(xGradient)
  }
  
  def yGradientImage(): PImage = {
    toImage(yGradient)
  }
  
  private def toImage(gradient: Gradient): PImage = {
    // The range needs to be shifted from [-1020, 1020] to [0, 255] so that it can be drawn
    val pixels = gradient.pixels.flatMap(row => row.map(pixel => applet.color((pixel + 1020) / 8.0f))).toArray
    val image = new PImage(gradient.pixels(0).length, gradient.pixels.length)
    image.pixels = pixels
    return image
  }
  
  /**
   * Returns the unit vector for the gradient
   * direction at (x,y)
   * This implies |(gx, gy)| = 1
   */
  def gradientDirection(x: Int, y: Int): (Float, Float) = {
    val gx = xGradient.pixels(y)(x)
    val gy = yGradient.pixels(y)(x)
    val mag = gradientMagnitude(x, y)
    return (gx/mag, gy/mag)
  }
  
  def gradientMagnitude(x: Int, y: Int): Float = {
    val gx = xGradient.pixels(y)(x)
    val gy = yGradient.pixels(y)(x)
    return Math.sqrt((gx*gx) + (gy*gy)).toFloat
  }
}