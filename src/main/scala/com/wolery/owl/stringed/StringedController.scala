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

import com.wolery.owl.Controller
import com.wolery.owl.core.Pitch
import com.wolery.owl.core.Scale
import com.wolery.owl.gui.Bead
import com.wolery.owl.message._
import com.wolery.owl.utils.implicits._
import com.wolery.owl.ℤ

import javafx.application.Platform.{ runLater ⇒ defer }
import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.Node
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.RowConstraints
import javafx.util.Duration
import javax.sound.midi.MetaMessage
import javax.sound.midi.MidiMessage
import javax.sound.midi.ShortMessage
import javax.sound.midi.ShortMessage.NOTE_OFF
import javax.sound.midi.ShortMessage.NOTE_ON

//****************************************************************************

class StringedController(val instrument: StringedInstrument) extends Controller
{
  @fx
  var root: Pane                   = _
  val rows: Seq[RowConstraints]    = makeRows
  val cols: Seq[ColumnConstraints] = makeCols
  val chan = 0
  val grid: GridPane = newGrid()
  val beads: Seq[Bead] = for (s<-instrument.stops) yield
    {
      val b = newBead(1,s.pitch)
      b.setVisible(false)
      grid.add(b,s.col,s.row)
      b
    }

  def initialize(): Unit =
  {
    root.getChildren.add(grid)
  }

  def view: Pane= root

  def send(message: MidiMessage,timestamp: Long): Unit =
  {
    message match
    {
      case m: ShortMessage if m.getChannel==chan ⇒ m.getCommand match
      {
        case NOTE_OFF ⇒ defer(onNoteOff(Pitch(m.getData1)))
        case NOTE_ON  ⇒ defer(onNoteOn (Pitch(m.getData1)))
        case _        ⇒
      }
      case m: MetaMessage ⇒ m.getType match
      {
        case HARMONY  ⇒ defer(onHarmony(harmony(m)))
        case _        ⇒
      }
      case   _        ⇒
    }
  }

  def onNoteOn(pitch: Pitch) =
  {
    for (stop ← instrument.stops(pitch))
    {
      beads(stop.index).setVisible(true)
    }
  }

  def onNoteOff(pitch: Pitch) =
  {
    for (stop ← instrument.stops(pitch))
    {
      beads(stop.index).setVisible(false)
    }
  }

  def onHarmony(scale: Scale) =
  {
    println(scale)

    for (stop ← instrument.stops if scale.contains(stop.pitch.note))
    {
      beads(stop.index).setVisible(true)
    }
  }

  def fade(from:Double,to: Double,ms:Int = 2000)(node: Node):javafx.animation. Transition =
  {
    val t = new javafx.animation.FadeTransition(Duration.millis(ms),node)
    t.setFromValue(from)
    t.setToValue  (to)
    t
  }

  def newGrid(): GridPane =
  {
    val g = new GridPane
  //g.gridLinesVisible = true
    g.getRowConstraints.addAll   (rows:_*)
    g.getColumnConstraints.addAll(cols:_*)
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
    val n = instrument.frets
    val w = 100.0 / n

    for (c ← 0 until n) yield
    {
      new ColumnConstraints(){setPercentWidth(w);setHalignment(javafx.geometry.HPos.CENTER)}
    }
  }
}

//****************************************************************************
