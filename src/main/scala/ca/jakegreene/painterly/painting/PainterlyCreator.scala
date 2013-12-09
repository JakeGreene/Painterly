package ca.jakegreene.painterly.painting

import processing.core.PImage
import processing.core.PConstants
import ca.jakegreene.processing.AdvancedPImage._
import ca.jakegreene.painterly.util.Blur
import processing.core.PApplet
import ca.jakegreene.processing.GradientImage

class PainterlyCreator()(implicit applet: PApplet) {

  def paint(image: PImage, strokeSizes: Seq[Int]) {
    // Use the largest strokes first
    strokeSizes.sortWith(_ > _).foreach { size =>
      val threshold = 2
      val blurredImage = Blur.gaussian(image, size)
      val differenceImage = image.difference(blurredImage)
      val gridSize = size
      val points = for {
        x <- 0 to (image.width - gridSize, gridSize)
        y <- 0 to (image.height - gridSize, gridSize)
        error = calculateAreaError(differenceImage, x, y, gridSize)
        if (error > threshold)
        (drawX, drawY) = maxError(differenceImage, x, y, gridSize)
      } yield (drawX, drawY)
      
      val gradient = GradientImage(blurredImage)
      points.foreach {case (x, y) => {
        drawStroke(x, y, size, gradient)
      }}
    }
  }

  private def calculateAreaError(differenceImage: PImage, x: Int, y: Int, width: Int): Float = {
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

  private def maxError(differenceImage: PImage, x: Int, y: Int, width: Int): (Int, Int) = {
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

  private def drawStroke(startX: Int, startY: Int, strokeWidth: Int, referenceImage: GradientImage) {
    val maxStrokeLength = 2
    val strokeColour = referenceImage.image.get(startX, startY)
    applet.stroke(strokeColour)
    applet.strokeWeight(strokeWidth)
    var (lastX, lastY) = (startX, startY)
    var (lastXDir, lastYDir) = (0f, 0f)
    
    for (i <- 0 to maxStrokeLength) {
      if (referenceImage.gradientMagnitude(lastX, lastY) == 0) return
      val (gx, gy) = referenceImage.gradientDirection(lastX, lastY)
      // Choose the normal to the gradient direction which will produce the least curvature in the stroke
      val (dx, dy) = if ((lastXDir * -gy) + (lastYDir * gx) < 0) (gy, -gx) else (-gy, gx)
      val x = max(min(lastX + strokeWidth*dx, referenceImage.image.width - 1), 0).toInt
      val y = max(min(lastY + strokeWidth*dy, referenceImage.image.height - 1), 0).toInt
      applet.line(lastX, lastY, x, y)
      lastX = x
      lastY = y
      lastXDir = dx
      lastYDir = dy
    }
  }

  private def max(a: Int, b: Int): Int = {
    if (a > b) a else b
  }
  
  private def max(a: Float, b: Float): Float = {
    if (a > b) a else b
  }

  private def min(a: Int, b: Int): Int = {
    if (a < b) a else b
  }
  
  private def min(a: Float, b: Float): Float = {
    if (a < b) a else b
  }

}