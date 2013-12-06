package ca.jakegreene.painterly.util

import processing.core.PImage
import processing.core.PConstants

object Blur {
  def gaussian(image: PImage, radius: Int): PImage = {
    val blurredImage = new PImage(image.width, image.height)
    blurredImage.copy(image, 0, 0, image.width, image.height, 0, 0, image.width, image.height)
    blurredImage.filter(PConstants.BLUR, radius)
    return blurredImage
  }
  
}