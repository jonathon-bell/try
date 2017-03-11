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

//****************************************************************************

import Math.{max,min}

import com.wolery.owl.core._
import com.wolery.owl.Instrument
import com.wolery.owl.gui.load

import javafx.scene.Node

//****************************************************************************

case class StringedInstrument(frets: ℕ,strings: Pitch*) extends Instrument
{
  case class Cell(string: ℕ,fret: ℕ)
  {
    def pitch = strings(string) + fret
  }

  def lowest: Pitch =
  {
    strings(0)
  }

  def highest: Pitch =
  {
    strings.last + frets
  }

  def cells: Seq[Cell] =
  {
    for {s ← 0 until strings.size; f ← 0 to frets} yield
      Cell(s,f)
  }

  def cells(pitch: Pitch): Seq[Cell] =
  {
    for (s ← 0 until strings.size if strings(s) <= pitch && pitch<= strings(s)+frets) yield
      Cell(s,pitch-strings(s))
  }

  def position(fret: ℕ): Seq[Cell] =
  {
    for {f ← max(fret-2,0) to min(fret+3,frets);
         s ← 0 until strings.size} yield
      Cell(s,f)
  }

  def strings(pitch: Pitch): Seq[ℕ] =
  {
    for {s ← 0 until strings.size if strings(s)<=pitch && pitch<=strings(s)} yield
      s
  }

  def view(fxml: String): (Node,StringedController) =
  {
    load(fxml,new StringedController(this))
  }
}

//****************************************************************************
