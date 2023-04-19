package parsley.debugger.frontend.internal

import parsley.debugger.ParseAttempt
import scalafx.beans.binding.Bindings
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.ScrollPane
import scalafx.scene.paint.Color
import scalafx.scene.text.{FontWeight, Text, TextFlow}

private[frontend] class InputHighlighter(
  fullInput: String,
  selected: ObjectProperty[Option[ParseAttempt]]
) extends ScrollPane { outer =>
  // Produce our local bindings for the text contents of our three texts.
  private val beforeBinding = Bindings.createStringBinding(
    () => {
      if (selected().isDefined) fullInput.slice(0, selected().get.fromOffset)
      else fullInput
    },
    selected
  )
  private val duringBinding = Bindings.createStringBinding(
    () => {
      if (selected().isDefined)
        fullInput.slice(selected().get.fromOffset, selected().get.toOffset)
      else ""
    },
    selected
  )
  private val afterBinding  = Bindings.createStringBinding(
    () => {
      if (selected().isDefined) fullInput.slice(selected().get.toOffset, fullInput.length)
      else ""
    },
    selected
  )

  // Produce the textFlow object needed for this display.
  private val before = new Text {
    text <== beforeBinding
    font = monoFont(1.5)
  }

  private val during = new Text {
    text <== duringBinding
    font = monoFont(1.5, FontWeight.Black)
    fill = Color.Green
    underline = true
  }

  private val after = new Text {
    text <== afterBinding
    font = monoFont(1.5)
  }

  private val completed = new TextFlow(before, during, after) { outer =>
    prefWidth  <== outer.width
    prefHeight <== outer.height
  }

  // Finally set up our scrollable.
  prefWidth  <== outer.width
  prefHeight <== outer.height
  fitToWidth = true

  background = DefaultBackground

  content = completed
}
