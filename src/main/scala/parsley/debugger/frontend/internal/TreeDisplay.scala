package parsley.debugger.frontend.internal

import parsley.debugger.DebugTree
import parsley.debugger.frontend.internal.TreeDisplay.mkTree
import scalafx.Includes._
import scalafx.beans.binding.Bindings
import scalafx.beans.property.{BooleanProperty, ObjectProperty}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.ScrollPane
import scalafx.scene.input.MouseButton
import scalafx.scene.layout.{
  Background,
  Border,
  BorderStrokeStyle,
  GridPane,
  Pane,
  Priority,
  StackPane
}
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, FontWeight, Text}

import scala.collection.mutable

private[frontend] class TreeDisplay(
  tree: DebugTree,
  selected: ObjectProperty[Option[DebugTree]]
) extends ScrollPane { outer =>
  // Set visual parameters.
  prefWidth  <== outer.width
  prefHeight <== outer.height

  alignmentInParent = Pos.Center

  padding = simpleInsets(2)

  background = DefaultBackground

  implicit private val foldSetters: mutable.ListBuffer[BooleanProperty] =
    new mutable.ListBuffer()

  content = mkTree(tree, selected)
  hvalue = 0.5 // Set the scroll to the centre horizontally.

  def foldAll(): Unit = for (unfolded <- foldSetters) {
    // False hides things, so is the folded state.
    unfolded() = false
  }

  def unfoldAll(): Unit = for (unfolded <- foldSetters) {
    // True is the unfolded state.
    unfolded() = true
  }
}

private[frontend] object TreeDisplay {
  def mkDTreeRect(dtree: DebugTree, selected: ObjectProperty[Option[DebugTree]]): Pane = {
    val nameText = new Text {
      text = dtree.internalName
      font = defaultFont(1, FontWeight.Black)
      hgrow = Priority.Always
      alignmentInParent = Pos.Center
    }

    val totalAttempts = dtree.parseResults.size
    val successes     = dtree.parseResults.count(_.success)
    val ratio         = successes / totalAttempts.toDouble

    val colourBinding = Bindings.createObjectBinding(
      () => {
        if (selected().contains(dtree)) simpleBackground(Color.Yellow).delegate
        else simpleBackground(lerpColour(FailureColour, SuccessColour, ratio)).delegate
      },
      selected
    )

    val succText = new Text {
      text = s"$successes / $totalAttempts"
      font = monoFont(1)
      hgrow = Priority.Always
      alignmentInParent = Pos.Center
    }

    val pane = new GridPane {
      padding = simpleInsets(0.5)
      vgap = relativeSize(0.5)
      alignmentInParent = Pos.Center
      hgrow = Priority.Always
      background <== colourBinding

      onMouseClicked = event => {
        if (event.getButton == MouseButton.Primary.delegate) {
          if (selected().contains(dtree)) selected() = None
          else selected() = Some(dtree)
        }
      }
    }

    pane.addColumn(0, nameText, succText)
    pane
  }

  def mkTree(dtree: DebugTree, selected: ObjectProperty[Option[DebugTree]])(implicit
    folds: mutable.ListBuffer[BooleanProperty]
  ): Pane = {
    val rootNode = mkDTreeRect(dtree, selected)
    val columns  = dtree.nodeChildren.size

    val treeGrid = new GridPane {
      hgap = relativeSize(1)
      vgap = relativeSize(1)
      alignmentInParent = Pos.Center
      background = DefaultBackground
    }

    treeGrid.add(rootNode, 0, 0, Math.max(columns, 1), 1)
    for ((pane, ix) <- dtree.nodeChildren.values.map(mkTree(_, selected)).zipWithIndex) {
      treeGrid.add(pane, ix, 1)
    }

    val unfolded = BooleanProperty(true)
    folds.append(unfolded)

    rootNode.onMouseClicked = event => {
      if (event.getButton == MouseButton.Secondary.delegate) {
        unfolded() = !unfolded()
        for (elem <- treeGrid.children.filterNot(_ == rootNode.delegate)) {
          elem.setVisible(unfolded())
          elem.setManaged(unfolded())
        }
      }
    }

    rootNode.border <== when(unfolded) choose Border.Empty otherwise simpleBorder(
      BorderStrokeStyle.Dotted
    )

    // Check if the renamed parser matches its internal name.
    // If it does, return as usual.
    // Otherwise, make a dotted box around it with the renamed name.
    if (dtree.parserName == dtree.internalName) treeGrid
    else {
      // This is the inner box with the dotted border
      val innerBox = new StackPane {
        padding = simpleInsets(2)
        alignmentInParent = Pos.Center
        alignment = Pos.Center
        border = simpleBorder(BorderStrokeStyle.Dashed)
        background = DefaultBackground
      }

      innerBox.children.add(treeGrid)

      // Wrap the inner box in another pane
      val spacer = new StackPane {
        padding = simpleInsets(1)
        alignment = Pos.Center
        alignmentInParent = Pos.Center
        background = DefaultBackground
      }

      spacer.children.add(innerBox)

      // Name in a white box.
      val name = new Text {
        text = dtree.parserName
        font = defaultFont(1, FontWeight.Black)
        alignmentInParent = Pos.Center
      }

      val panel = new Pane {
        alignmentInParent = Pos.Center
      }

      val halfX = Bindings.createDoubleBinding(
        () => panel.layoutBounds().maxX / 2,
        panel.layoutBounds
      )

      val whiteBox = new StackPane {
        alignment = Pos.Center
        alignmentInParent = Pos.TopCenter
        background = DefaultBackground
        padding = Insets(0, relativeSize(1), 0, relativeSize(1))

        private val xbind = Bindings.createDoubleBinding(
          () => halfX().doubleValue() - this.layoutBounds().maxX / 2,
          this.layoutBounds,
          halfX
        )

        translateX <== xbind
      }

      whiteBox.children.add(name)

      // The actual panel.
      panel.children.add(spacer)
      panel.children.add(whiteBox)

      val nameUnfolded = BooleanProperty(true)
      folds.append(nameUnfolded)

      whiteBox.onMouseClicked = event => {
        if (event.getButton == MouseButton.Secondary.delegate) {
          nameUnfolded() = !nameUnfolded()
          spacer.setVisible(nameUnfolded())
          spacer.setManaged(nameUnfolded())
        }
      }

      whiteBox.border <== when(nameUnfolded) choose Border.Empty otherwise simpleBorder(
        BorderStrokeStyle.Dotted
      )

      panel
    }
  }
}
