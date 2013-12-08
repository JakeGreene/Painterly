

package ca.jakegreene.painterly

import ca.jakegreene.painterly.painting.PainterlyCreator
import ca.jakegreene.processing.AdvancedPImage.PImage2AdvancedPImage
import ca.jakegreene.processing.GradientImage
import ca.jakegreene.processing.Kernel
import processing.core.PApplet
import processing.core.PConstants

object Painterly {
  def main(args: Array[String]) {
    val frame = new javax.swing.JFrame("Painterly")
    val app = new PainterlyApplet()
    frame.getContentPane().add(app)
    app.init
    
    frame.setSize(1800, 700)
    frame.setVisible(true)
  }
}

class PainterlyApplet extends PApplet {
  implicit val applet = this
  
  val image = loadImage("Domo_lizard_smaller.png")  
  val painter = new PainterlyCreator()
  val sobelY = Array(Array[Float](1, 2, 1),
                    Array[Float](0, 0, 0),
                    Array[Float](-1, -2, -1))
  val sobelYKernel = Kernel(sobelY)
  val sobelX = Array(Array[Float](1, 0, -1),
                     Array[Float](2, 0, -2),
                     Array[Float](1, 0, -1))
  val sobelXKernel = Kernel(sobelX)
  
  
  override def setup() {
    /*
     * The default render mode allows for bevel and
     * line-connector types.
     * 
     * P3D (needed for depth) does not support these
     * functions
     */
    size(1800, 700, PConstants.P3D);
    frameRate(30)
    noLoop()
    noStroke()
  }
  
  override def draw() {
    background(255);
    textSize(24)
    fill(0, 120, 120)
    text("Original Image", 20, 20)
    image(image, 0, 50)
    val gradientImage = GradientImage(image)
    text("Vertical Edges", 620, 20)
    image(gradientImage.yGradientImage, 600, 50)
    text("Horizontal Edges", 1220, 20)
    image(gradientImage.xGradientImage, 1200, 50)
  }
  
  private def drawLayer(strokeSize: Int) {
    val strokePoints = painter.findStrokePoints(image, strokeSize)
    strokePoints.foreach {
      case (x, y, size) => {
        val colour = image.pixels(x + (y*image.width))
        fill(colour)
        ellipse(x, y, size, size)
      }
    }
  }
}