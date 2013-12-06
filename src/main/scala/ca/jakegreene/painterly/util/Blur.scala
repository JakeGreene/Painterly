package ca.jakegreene.painterly.util

import processing.core.PImage

object Blur {
  def gaussian(image: PImage, radius: Int): PImage = {
    val blurredImage = new PImage(image.width, image.height)
    blurredImage.copy(image, 0, 0, image.width, image.height, 0, 0, image.width, image.height)
    val gaussian = new Convolver(radius)
    gaussian.blur(blurredImage, 0, 0, image.width, image.height)
    return blurredImage
  }
  
}