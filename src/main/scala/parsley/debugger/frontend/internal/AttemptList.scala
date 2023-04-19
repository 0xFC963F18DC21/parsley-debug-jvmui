package parsley.debugger.frontend.internal

import javafx.scene.{layout => jfxl}
import parsley.debugger.{DebugTree, ParseAttempt}
import scalafx.beans.binding.Bindings
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Pos
import scalafx.scene.control.ScrollPane
import scalafx.scene.input.MouseButton
import scalafx.scene.layout.{GridPane, Priority}
import scalafx.scene.paint.Color
import scalafx.scene.text.{FontWeight, Text}

private[frontend] class AttemptList(
  dtree: DebugTree,
  selected: ObjectProperty[Option[ParseAttempt]]
) extends ScrollPane { outer =>
  // Makes sure our bounds don't overflow:
  prefWidth  <== outer.width
  prefHeight <== outer.height

  // Makes sure the content doesn't go off the sides:
  fitToWidth = true

  background = DefaultBackground

  // Clear selected if right-clicked.
  onMouseClicked = event => {
    if (event.getButton == MouseButton.Secondary.delegate) {
      selected() = None
    }
  }

  // Contents.
  private val allList = new GridPane {
    alignment = Pos.Center
    alignmentInParent = Pos.TopLeft
    prefWidth <== outer.width
  }

  for ((att, ix) <- dtree.parseResults.zipWithIndex) {
    allList.add(new Attempt(att, selected), 0, ix)
  }

  // Finally set content to the list of attempts.
  content = allList
}

private[frontend] class Attempt(
  att: ParseAttempt,
  selected: ObjectProperty[Option[ParseAttempt]]
) extends GridPane { outer =>
  private val colorBinding = Bindings.createObjectBinding[jfxl.Background](
    () => {
      if (selected().contains(att)) simpleBackground(Color.Yellow).delegate
      else simpleBackground(if (att.success) SuccessColour else FailureColour)
    },
    selected
  )

  // Visual parameters.
  background <== colorBinding

  padding = simpleInsets(1)

  hgap = relativeSize(1)
  vgap = relativeSize(0.5)

  prefWidth <== outer.width

  hgrow = Priority.Always

  alignment = Pos.CenterLeft

  // Allow selection when this is left-clicked.
  onMouseClicked = event => {
    if (event.getButton == MouseButton.Primary.delegate) {
      if (selected().contains(att)) selected() = None
      else selected() = Some(att)
    }
  }

  // Contents.
  add(
    new Text {
      text = {
        val untilLB  = att.rawInput.takeWhile(!"\r\n".contains(_))
        val addition = if (att.rawInput.length > untilLB.length) " [...]" else ""

        untilLB + addition
      }
      font = monoFont(1, FontWeight.Bold)
    },
    1,
    0
  )

  add(
    new Text {
      text = s"${att.fromPos} to ${att.toPos}"
    },
    1,
    1
  )

  add(
    new Text {
      text = if (att.success) "✓" else "✗"
      font = defaultFont(2, FontWeight.Black)
    },
    0,
    0,
    1,
    2
  )
}
