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

object PainterlyCreator {
  case class Style(threshold: Int, strokeWidths: Seq[Int], strokeMinLength: Int, strokeMaxLength: Int)
  val impressionist = Style(10, Seq(8, 6, 2), 4, 16)
  val expressionist = Style(15, Seq(8, 4, 2), 10, 16)
  val pointalist = Style(1, Seq(4, 2), 0, 1)
  
  
  def apply(image: PImage)(implicit applet: PApplet) = new PainterlyCreator(image)
}

class PainterlyCreator(image: PImage)(implicit applet: PApplet) {
  import PainterlyCreator._
  
  private case class Layer(points: Seq[(Int, Int)], reference: PImage)
  
  val execService = Executors.newCachedThreadPool()
  implicit val execContext = ExecutionContext.fromExecutorService(execService)


  def paint(style: Style): PImage = {
    // Use the largest strokes first
    val futureTopLayers = style.strokeWidths.sortWith(_ > _) map { size =>
      Future { (createLayer(size, style), size) }
    }

    val futureLayers = Future.sequence(futureTopLayers)
    val layers = Await.result(futureLayers, 60.second)
    
    //val finalImage = image.get()
    // Sometimes areas will not be drawn over. Fill these places with a blurry version of the original
    val baseImage = Blur.gaussian(image.get(), style.strokeWidths.head)
    //val finalImage = new PImage(image.width, image.height)
    val finalImage = layers.foldLeft(baseImage)((builtImage, layerData) => {
      val layer = layerData._1
      val size = layerData._2
      val paintedLayer = paintLayer(layer, size, style, builtImage)
      builtImage.blend(paintedLayer, 0, 0, builtImage.width, builtImage.height, 
                                     0, 0, paintedLayer.width, paintedLayer.height, 
                                     PConstants.BLEND)
      builtImage                             
    })
    return finalImage
  }

  private def createLayer(strokeSize: Int, style: Style): Layer = {
    val blurredImage = Blur.gaussian(image, strokeSize)
    val differenceImage = image.difference(blurredImage)
    val gridSize = strokeSize
    val points = for {
      x <- 0 to (image.width - gridSize, gridSize)
      y <- 0 to (image.height - gridSize, gridSize)
      error = calculateAreaError(differenceImage, x, y, gridSize)
      if (error > style.threshold)
      (drawX, drawY) = maxError(differenceImage, x, y, gridSize)
    } yield (drawX, drawY)
    return Layer(points, blurredImage)
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
  
  private def paintLayer(layer: Layer, strokeSize: Int, style: Style, baseImage: PImage): PImage = {
    val gradient = GradientImage(layer.reference)
    val graphics = applet.createGraphics(image.width, image.height)
    graphics.beginDraw()
    graphics.background(0, 0, 0, 0) // Full Transparency allows the image to be blended
    layer.points.foreach {
      case (x, y) => {
        drawStroke((x, y), strokeSize, gradient, graphics, style, baseImage)
      }
    }
    graphics.endDraw()
    return graphics
  }

  private def drawStroke(startPoint: (Int, Int), strokeWidth: Int, gradient: GradientImage, renderer: PGraphics, style: Style, baseImage: PImage) {
    val startX = startPoint._1
    val startY = startPoint._2
    val strokeColour = gradient.image.get(startX, startY)
    renderer.stroke(strokeColour)
    renderer.strokeWeight(strokeWidth/2)
    var (lastX, lastY) = (startX, startY)
    var (lastXDir, lastYDir) = (0f, 0f)
    
    var count = 0
    for (i <- 0 to (style.strokeMaxLength - 1)) {
      // Detect vanishing gradient
      if (gradient.gradientMagnitude(lastX, lastY) == 0) return
      // We need to avoid painting too far out of the lines
      val originalDelta = diff(gradient.image.get(lastX, lastY), baseImage.get(lastX, lastY))
      val strokeDelta = diff(gradient.image.get(lastX, lastY), strokeColour)
      if (i > style.strokeMinLength && originalDelta < strokeDelta) return
      
      val (gx, gy) = gradient.gradientDirection(lastX, lastY)
      // Choose the normal to the gradient direction which will produce the most curvature in the stroke
      val (dx, dy) = if ((lastXDir * -gy) + (lastYDir * gx) < 0) (-gy, gx) else (gy, -gx)
      val x = max(min(lastX + strokeWidth*dx, gradient.image.width - 1), 0).toInt
      val y = max(min(lastY + strokeWidth*dy, gradient.image.height - 1), 0).toInt
      renderer.line(lastX, lastY, x, y)
      count += 1
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