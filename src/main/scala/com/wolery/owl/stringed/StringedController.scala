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

import com.wolery.owl.core._
import javafx.fxml.{FXML ⇒ fx}
import javafx.scene.Node
import javafx.scene.layout.{Pane,GridPane}
import javafx.geometry.HPos
import scalafx.Includes._

import com.wolery.owl._
import com.wolery.owl.gui.Bead

//****************************************************************************

class StringedController(instrument: StringedInstrument) extends Controller
{
  @fx var strings: Pane     = _
  @fx var frets:   Pane     = _
  @fx var markers: Pane     = _
  @fx var grid:    GridPane = _

  def initialize =
  {
  //grid.rowConstraints.   foreach(c ⇒ c.setFillHeight(false))
    grid.columnConstraints.foreach(c ⇒ c.setHalignment(HPos.CENTER))
  }

  def update(track: ℕ,chords: Seq[Chord]) =
  {
    for {chord ← chords;
         pitch ← chord;
         stop@(s,f)  ← instrument.stops(pitch)}
    {
      val node = bead(track,pitch)

      GridPane.setConstraints(node,f,instrument.strings.size - 1 - s)
      grid.getChildren.add(node)
    }
  }

  def bead(track: ℕ,p: Pitch): Bead = track match
  {
    case 0 => new Bead(p.note.toString,"bead-white-text")
    case 1 => new Bead(p.toString,     "bead")
  }
}

//****************************************************************************
