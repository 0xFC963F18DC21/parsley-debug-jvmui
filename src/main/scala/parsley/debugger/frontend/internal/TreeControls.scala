package parsley.debugger.frontend.internal

import scalafx.beans.binding.Bindings
import scalafx.geometry.{Orientation, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.{GridPane, HBox, Priority, TilePane, VBox}

private[frontend] class TreeControls(view: TreeDisplay) extends GridPane { outer =>
  add(view, 0, 0, 2, 1)

  alignment = Pos.Center

  // Buttons with fold controls.
  private val unfold = new Button {
    text = "Unfold All"
    onAction = _ => view.unfoldAll()

    prefWidth <== Bindings.createDoubleBinding(
      () => outer.layoutBounds().getWidth / 2,
      outer.layoutBounds
    )

    alignmentInParent = Pos.Center
  }

  private val fold = new Button {
    text = "Fold All"
    onAction = _ => view.foldAll()

    prefWidth <== Bindings.createDoubleBinding(
      () => outer.layoutBounds().getWidth / 2,
      outer.layoutBounds
    )

    alignmentInParent = Pos.Center
  }

  add(unfold, 0, 1)
  add(fold, 1, 1)
}
