//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $$Header:$$
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

import Math.{ max, min }

import com.wolery.owl.core._
import com.wolery.owl.Instrument
import com.wolery.owl.gui.load

import javafx.scene.Node

//****************************************************************************

case class StringedInstrument(frets: ℕ,strings: Pitch*) extends Instrument
{
  type Stop = (ℕ,ℕ)

  def lowest:  Pitch = strings(0)
  def highest: Pitch = strings.last + frets

  def pitch(stop: Stop): Pitch = stop match
  {
    case (string,fret) => strings(string) + fret
  }

  def stops: Seq[Stop] =
  {
    for {s ← 0 until strings.size; f ← 0 to frets} yield
      (s,f)
  }

  def stops(pitch: Pitch): Seq[Stop] =
  {
    for (s ← 0 until strings.size if strings(s) <= pitch && pitch<= strings(s)+frets) yield
      (s,pitch-strings(s))
  }

  def position(fret: ℕ): Seq[Stop] =
  {
    for {f ← max(fret-2,0) to min(fret+3,frets);
         s ← 0 until strings.size} yield
      (s,f)
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
