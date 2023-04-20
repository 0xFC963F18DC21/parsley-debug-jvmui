package parsley.debugger.frontend.internal

import javafx.scene.{layout => jfxl}
import parsley.debugger.{DebugTree, ParseAttempt}
import scalafx.beans.binding.Bindings
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Pos
import scalafx.scene.control.ScrollPane
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.input.MouseButton
import scalafx.scene.layout.{GridPane, Priority, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.{FontWeight, Text}

private[frontend] class AttemptList(
  dtree: ObjectProperty[Option[DebugTree]],
  selected: ObjectProperty[Option[ParseAttempt]]
) extends ScrollPane { outer =>
  // Makes sure the content doesn't go off the sides:
  fitToWidth = true
  hgrow = Priority.Always

  background = DefaultBackground

  hbarPolicy = ScrollBarPolicy.AsNeeded
  vbarPolicy = ScrollBarPolicy.AsNeeded

  // Clear selected if right-clicked.
  onMouseClicked = event => {
    if (event.getButton == MouseButton.Secondary.delegate) {
      selected() = None
    }
  }

  // Contents.
  // Finally set content to the list of attempts.
  content <== Bindings.createObjectBinding(
    () => {
      // We also want to reset the scrollbar to the top, too.
      vvalue = 0

      val allList = new VBox()

      if (dtree().isDefined) {
        for (att <- dtree().get.parseResults.map(new Attempt(_, selected))) {
          allList.children.add(att)
        }
      }

      allList.delegate
    },
    dtree
  )
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

  hgap = relativeSize(1)
  vgap = relativeSize(0.5)

  padding = simpleInsets(1)

//  prefWidth <== outer.width

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
      text = if (att.fromOffset == att.toOffset) {
        "*** Parser did not consume input. ***"
      } else {
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
      text =
        if (att.fromOffset == att.toOffset) "N/A"
        else s"${att.fromPos} to ${att.toPos}"
    },
    1,
    1
  )

  add(
    new Text {
      text = if (att.success) "✓" else "✗"
      font = defaultFont(2.5, FontWeight.Black)
    },
    0,
    0,
    1,
    2
  )
}
