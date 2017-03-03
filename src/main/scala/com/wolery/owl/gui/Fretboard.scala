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

//****************************************************************************

package fretted
{
  case class Fretted(frets: ℕ,strings: Pitch*)
  {
    case class Point(fret: ℕ,string: ℕ)
    {
      def pitch: Pitch     = Fretted.this.pitch(fret,string)

      override
      def toString: String = s"$pitch ${string+1}:$fret"
    }

    def points: Seq[Point] =
    {
      for {f ← 0 to frets; s ← 0 until strings.size} yield
          Point(f,s)
    }

//  def points(pitch:Pitch): Seq[Point]
    def pitch (point: Point): Pitch      = pitch(point.string,point.fret)
    def pitch (fret: ℕ,string: ℕ): Pitch = strings(string) + fret
  }
}

package object fretted
{
  type Fret      = ℕ
  type Strng     = ℕ
  type Position  = ℕ

  val instrument = Fretted(24,E(2),A(2),D(3),G(3),B(3),E(4))
}

//****************************************************************************

class FretboardController extends Logging
{
  import fretted._

  @fx var frets:   Pane = _
  @fx var strings: Pane = _
  @fx var markers: Pane = _
  @fx var grid:    GridPane = _

  def initialize =
  {
    log.info("initialize")

  //grid.rowConstraints.   foreach(c ⇒ c.setFillHeight(false))
    grid.columnConstraints.foreach(c ⇒ c.setHalignment(HPos.CENTER))

    val scale = Scale(C,"altered").get

    for (p ← instrument.points if scale.contains(p.pitch.note))
    {
      add(new Bead(p.pitch.toString,"bead"),p)
    }
  }

  def add(n: Node,p: instrument.Point) =
  {
    GridPane.setConstraints(n,p.fret,instrument.strings.size - p.string - 1)

    grid.getChildren.add(n)
  }
}

//****************************************************************************
