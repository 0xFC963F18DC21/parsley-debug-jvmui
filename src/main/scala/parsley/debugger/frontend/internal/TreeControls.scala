package parsley.debugger.frontend.internal

import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.{GridPane, Priority}

private[frontend] class TreeControls(outer: Scene, view: TreeDisplay) extends GridPane {
  prefWidth  <== outer.width
  prefHeight <== outer.height

  add(view, 0, 0, 2, 1)

  // Buttons with fold controls.
  private val unfold = new Button {
    text = "Unfold All"
    onAction = _ => view.unfoldAll()

    hgrow = Priority.Always

    alignmentInParent = Pos.Center
  }

  private val fold = new Button {
    text = "Fold All"
    onAction = _ => view.foldAll()

    hgrow = Priority.Always

    alignmentInParent = Pos.Center
  }

  add(unfold, 0, 1)
  add(fold, 1, 1)
}
