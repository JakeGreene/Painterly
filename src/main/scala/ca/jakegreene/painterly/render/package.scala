package ca.jakegreene.painterly

import ca.jakegreene.painterly.painting.Stroke
import ca.jakegreene.painterly.painting.Layer

package object render {
  /**
   * A StrokeRenderer is a function
   * that takes a stroke and renders it
   * at a given depth
   */
  type StrokeRenderer = (Stroke, Int) => Unit
  
  /**
   * A LayerRenderer is a function
   * that takes a layer and renders all
   * of its contents at a given depth
   */
  type LayerRenderer = (Layer, Int) => Unit
}