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

package com.wolery.owl.keyboard

//****************************************************************************

import com.wolery.owl.core._
import javafx.scene.Node
import com.wolery.owl.Instrument

//****************************************************************************

case class KeyboardInstrument(lowest: Pitch,highest: Pitch) extends Instrument
{
  type Stop       = Int
  type Controller = com.wolery.owl.stringed.StringedController

  def range: (Pitch,Pitch) =
  {
    (lowest,highest)
  }

  def pitch(key: Int): Pitch = lowest + key

  def stops: Seq[Stop] = ???

  def stops(pitch: Pitch): Seq[Stop] = ???

  def position(fret: ℕ): Seq[Stop] = ???

  def view(fxml: String): (Node,Controller) = ???
}

//****************************************************************************
