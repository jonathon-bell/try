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

package com.wolery.owl.gui

import com.wolery.owl.core._
import com.wolery.owl.utils.Logging

import javafx.fxml.{FXML ⇒ fx}
import javafx.scene.Node
import javafx.scene.layout.{Pane,GridPane}
import javafx.geometry.HPos
import scalafx.Includes._
import Math.{max,min}

//****************************************************************************

case class Fretted(frets: ℕ,strings: Pitch*)
{
  case class Point(fret: ℕ,string: ℕ)
  {
    def pitch: Pitch                   = strings(string) + fret

    def place(node: Node): Node        =
    {
      GridPane.setConstraints(node,fret,strings.size - 1 - string)
      node
    }

    override
    def toString: String               = s"$pitch ${string+1}:$fret"
  }

  def points: Seq[Point]               =
  {
    for {f ← 0 to frets; s ← 0 until strings.size} yield
      Point(f,s)
  }

  def points(pitch: Pitch): Seq[Point] =
  {
    for (s ← 0 until strings.size if strings(s) <= pitch && pitch<= strings(s)+frets) yield
      Point(pitch-strings(s),s)
  }

  def position(fret: ℕ): Seq[Point]     =
  {
    for {f ← max(fret-2,0) to min(fret+3,frets);
         s ← 0 until strings.size} yield
      Point(f,s)
  }

  def strings(pitch: Pitch): Seq[ℕ] =
  {
    for {s ← 0 until strings.size if strings(s)<=pitch && pitch<=strings(s)} yield
      s
  }
}

//****************************************************************************

class FretboardController extends Logging
{
  @fx var frets:   Pane = _
  @fx var strings: Pane = _
  @fx var markers: Pane = _
  @fx var grid:    GridPane = _

  val instrument = Fretted(24,E(2),A(2),D(3),G(3),B(3),E(4))

  def initialize =
  {
    log.info("initialize")

  //grid.rowConstraints.   foreach(c ⇒ c.setFillHeight(false))
    grid.columnConstraints.foreach(c ⇒ c.setHalignment(HPos.CENTER))

    val scale = Scale(F,"whole tone").get
    val fifth = instrument.position(5)

    for (p ← instrument.position(5))
    {
      if  (scale.contains(p.pitch.note) && fifth.contains(p))
        add(new Bead(p.pitch.note.toString,"bead"),p)
      else
        add(new Bead(p.pitch.toString,"bead-white-text"),p)
    }
  }

  def add(node: Node,point: instrument.Point) =
  {
    GridPane.setConstraints(node,point.fret,instrument.strings.size - 1 - point.string)

    grid.getChildren.add(node)
  }
}

//****************************************************************************

//****************************************************************************
