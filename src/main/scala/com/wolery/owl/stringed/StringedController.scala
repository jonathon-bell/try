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
import javafx.scene.layout.GridPane
import javafx.geometry.HPos
import scalafx.Includes._

import com.wolery.owl._
import com.wolery.owl.gui.Bead
import javafx.scene.input.MouseEvent

//****************************************************************************

class StringedController(val instrument: StringedInstrument) extends Controller
{
  @fx var grid:  GridPane = _

  def initialize =
  {
  //grid.rowConstraints.   foreach(c ⇒ c.setFillHeight(false))
    grid.columnConstraints.foreach(c ⇒ c.setHalignment(HPos.CENTER))
  }

  def update(layer: Layer,chords: Seq[Chord]) =
  {
    for {chord ← chords;
         pitch ← chord;
         stop@(s,f)  ← instrument.stops(pitch)}
    {
      val node = bead(layer,pitch)

      grid.add(node,f,instrument.strings.size - 1 - s)
    }
  }

  def bead(layer: Layer,p: Pitch): Bead = layer match
  {
    case 'harmony ⇒ new Bead(p.note.toString,"bead-white-text")
    case 'melody  ⇒ new Bead(p.toString,     "bead")
  }

  def onMouseClicked(e: MouseEvent) =
  {
    println("clicked")
  }
}

//****************************************************************************
