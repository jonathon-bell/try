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

package com.wolery.owl

//****************************************************************************

import core._
import javafx.scene.Node

//****************************************************************************

trait Instrument
{
  def range                    : (Pitch,Pitch)
  def pitches                  : Seq[Pitch] = range match {case (lo,hi)=> lo to hi}
/*
  def pitch(stop: Stop)        : Pitch

  def stops                    : Seq[Stop]
  def stops(pitch: Pitch)      : Seq[Stop]
//def stops(chord: Seq[Pitch]) : Seq[Seq[Stop]]
  def position(position: ℕ)    : Seq[Stop]
*/
  def view(fxml: String)       : (Node,Controller)
}

//****************************************************************************
