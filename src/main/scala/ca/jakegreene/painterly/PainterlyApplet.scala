

package ca.jakegreene.painterly

import scala.collection.mutable.ListBuffer

import ca.jakegreene.painterly.painting.PainterlyCreator
import ca.jakegreene.processing.AdvancedPImage.PImage2AdvancedPImage
import ca.jakegreene.processing.GradientImage
import ca.jakegreene.processing.Kernel
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage

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
  println(s"Image (${image.width}, ${image.height})")
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
    //text("Original Image", 20, 20)
    image(image, 0, 0)
    // text("Painted Image", 620, 20)
    val painting = painter.paint(image, Seq(20, 10, 5))
  }
}