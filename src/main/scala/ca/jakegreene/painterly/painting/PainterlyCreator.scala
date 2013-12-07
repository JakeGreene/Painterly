package ca.jakegreene.painterly.painting

import processing.core.PImage
import processing.core.PConstants
import ca.jakegreene.processing.AdvancedPImage._
import ca.jakegreene.painterly.util.Blur
import processing.core.PApplet

class PainterlyCreator {
  
  def findStrokePoints(image: PImage, strokeSize: Int)(implicit applet: PApplet): Seq[(Int, Int, Int)] = {
    val threshold = 6
    val blurredImage = Blur.gaussian(image, strokeSize)
    val differenceImage = image.difference(blurredImage)
    val gridSize = strokeSize
    for {
      x <- 0 to (image.width - gridSize, gridSize)
      y <- 0 to (image.height - gridSize, gridSize)
      error = calculateAreaError(differenceImage, x, y, gridSize)
      if (error > threshold)
      (drawX, drawY) = maxError(differenceImage, x, y, gridSize)
    } yield (drawX, drawY, strokeSize)
  }
  
  private def calculateAreaError(differenceImage: PImage, x: Int, y: Int, width: Int)(implicit applet: PApplet): Float = {
    var sum: Float = 0
    for (x_i <- x to (x + width - 1)) {
      for (y_i <- y to (y + width - 1)) {
        val i = x_i + (y_i * differenceImage.width)
        val difference = applet.red(differenceImage.pixels(i))
        sum += difference
      }
    }
    val floatWidth: Float = width
    return sum / (floatWidth * floatWidth)
  }
  
  private def maxError(differenceImage: PImage, x: Int, y: Int, width: Int)(implicit applet: PApplet): (Int, Int) = {
    var max: (Int, Int, Float) = (x, y, 0)
    for (x_i <- x to (x + width - 1)) {
      for (y_i <- y to (y + width - 1)) {
        val i = x_i + (y_i * differenceImage.width)
        val difference = applet.red(differenceImage.pixels(i))
        if (difference > max._3) {
          max = (x_i, y_i, difference)
        }
      }
    }
    return (max._1, max._2)
  }
  
  private def drawStroke(image: PImage, x: Int, y: Int, size: Int, colour: Int) {
    val maxX = min(x + size, image.width)
    val minX = max(x - size, 0)
    val maxY = min(y + size, image.height)
    val minY = max(y - size, 0)
    
    for (x <- minX to maxX) {
      for (y <- minY to maxY) {
        val index = x + (y * image.width)
        image.pixels(index) = colour
      }
    } 
  }
  
   private def max(a: Int, b: Int): Int = {
     if (a > b) a else b
   }
   
   private def min(a: Int, b: Int): Int = {
     if (a < b) a else b
   }

}