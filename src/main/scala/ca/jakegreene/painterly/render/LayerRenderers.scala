package ca.jakegreene.painterly.render

import ca.jakegreene.painterly.painting.Layer
import ca.jakegreene.painterly.painting.Stroke

object LayerRenderers {
  def drawInOrder(renderStroke: StrokeRenderer)(layer: Layer, depth: Int) {
    layer.strokes.foreach(renderStroke(_, depth))
  }
}
