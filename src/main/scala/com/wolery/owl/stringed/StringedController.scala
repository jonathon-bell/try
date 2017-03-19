//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.stringed

import com.wolery.owl._
import com.wolery.owl.core.Pitch
import com.wolery.owl.gui.Bead

import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{ ColumnConstraints, GridPane, Pane, RowConstraints }
import javafx.util.Duration
import javax.sound.midi.MidiMessage

//****************************************************************************

class StringedController(val instrument: StringedInstrument) extends Controller
{
  @fx
  var root: Pane                   = _
  val rows: Seq[RowConstraints]    = makeRows
  val cols: Seq[ColumnConstraints] = makeCols

  def view: Pane= root

  def send(mm: MidiMessage,ts: Long) = {}

  def close(): Unit = {}

  def update(layer: ℤ,chords: Seq[Chord]) =
  {
    val gp = newGrid()

    for {chord ← chords;
         pitch ← chord;
         cell  ← instrument.cells(pitch)}
    {
      val bead = newBead(layer,pitch)

      gp.add(bead,cell.fret,instrument.strings.size-1 - cell.string)
    }

    fade(0,1,1000)(gp).play()
  }

  def fade(from:Double,to: Double,ms:Int = 2000)(node: Node):javafx.animation. Transition =
  {
    val t = new javafx.animation.FadeTransition(Duration.millis(ms),node)
    t.setFromValue(0)
    t.setToValue  (1)
    t
  }

  def newGrid(): GridPane =
  {
    val g = new GridPane
  //g.gridLinesVisible = true
    g.getRowConstraints.addAll   (rows:_*)
    g.getColumnConstraints.addAll(cols:_*)
    root.getChildren.add(g)
    g
  }

  def newBead(layer: ℤ,p: Pitch): Bead = layer match
  {
    case 0 ⇒ new Bead(p.note.toString,"bead-white-text")
    case 1 ⇒ new Bead(p.note.toString,"bead")
  }

  def makeRows: Seq[RowConstraints] =
  {
    val n = instrument.strings.size
    val h = 100.0 / n

    for (r ← 0 until n) yield
    {
      new RowConstraints(){setPercentHeight(h)}
    }
  }

  def makeCols: Seq[ColumnConstraints] =
  {
    val n = instrument.frets + 1
    val w = 100.0 / n

    for (c ← 0 until n) yield
    {
      new ColumnConstraints(){setPercentWidth(w);setHalignment(javafx.geometry.HPos.CENTER)}
    }
  }
}

//****************************************************************************
