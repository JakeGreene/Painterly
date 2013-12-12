package ca.jakegreene.painterly.painting

import processing.core.PImage
import processing.core.PConstants
import ca.jakegreene.processing.AdvancedPImage._
import ca.jakegreene.processing.Colour._
import ca.jakegreene.painterly.util.Blur
import processing.core.PApplet
import ca.jakegreene.processing.GradientImage
import processing.core.PGraphics
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import scala.concurrent.Await
import scala.concurrent.duration._

class PainterlyCreator(threshold: Int, minStrokeLength: Int, maxStrokeLength: Int)(implicit applet: PApplet) {
  
  val execService = Executors.newCachedThreadPool()
  implicit val execContext = ExecutionContext.fromExecutorService(execService)


  def paint(image: PImage, strokeSizes: Seq[Int]): PImage = {
    // Use the largest strokes first
    val futureLayers = Future.sequence(strokeSizes.sortWith(_ > _) map { size =>
      Future { paintLayer(image, size) }
    })
    
    val layers = Await.result(futureLayers, 60.second)
    
    val finalImage = image.get()
    layers foreach { layer =>
      finalImage.blend(layer, 0, 0, finalImage.width, finalImage.height, 0, 0, layer.width, layer.height, PConstants.BLEND)
    }
    return finalImage
  }

  def paintLayer(image: PImage, strokeSize: Int): PImage = {
    val blurredImage = Blur.gaussian(image, strokeSize)
    val differenceImage = image.difference(blurredImage)
    val gridSize = strokeSize
    val points = for {
      x <- 0 to (image.width - gridSize, gridSize)
      y <- 0 to (image.height - gridSize, gridSize)
      error = calculateAreaError(differenceImage, x, y, gridSize)
      if (error > threshold)
      (drawX, drawY) = maxError(differenceImage, x, y, gridSize)
    } yield (drawX, drawY)

    val gradient = GradientImage(blurredImage)
    val graphics = applet.createGraphics(image.width, image.height)
    graphics.beginDraw()
    graphics.background(0, 0, 0, 0) // Full Transparency allows the image to be blended
    points.foreach {
      case (x, y) => {
        drawStroke(x, y, strokeSize, gradient, image, graphics)
      }
    }
    graphics.endDraw()
    return graphics
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

  private def drawStroke(startX: Int, startY: Int, strokeWidth: Int, referenceImage: GradientImage, original: PImage, renderer: PGraphics) {
    val strokeColour = referenceImage.image.get(startX, startY)
    renderer.stroke(strokeColour)
    renderer.strokeWeight(strokeWidth)
    var (lastX, lastY) = (startX, startY)
    var (lastXDir, lastYDir) = (0f, 0f)
    
    for (i <- 0 to maxStrokeLength) {
      // Detect vanishing gradient
      if (referenceImage.gradientMagnitude(lastX, lastY) == 0) return
      // We need to avoid painting too far out of the lines
      val originalDelta = diff(referenceImage.image.get(lastX, lastY), original.get(lastX, lastY))
      val strokeDelta = diff(referenceImage.image.get(lastX, lastY), strokeColour)
      if (i > minStrokeLength && originalDelta < strokeDelta) return
      
      val (gx, gy) = referenceImage.gradientDirection(lastX, lastY)
      // Choose the normal to the gradient direction which will produce the most curvature in the stroke
      val (dx, dy) = if ((lastXDir * -gy) + (lastYDir * gx) < 0) (-gy, gx) else (gy, -gx)
      val x = max(min(lastX + strokeWidth*dx, referenceImage.image.width - 1), 0).toInt
      val y = max(min(lastY + strokeWidth*dy, referenceImage.image.height - 1), 0).toInt
      renderer.line(lastX, lastY, x, y)
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