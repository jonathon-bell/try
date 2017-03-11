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

package com.wolery.owl

//****************************************************************************

trait Controller
{
  def instrument: Instrument

  def view: Node

  def update(layer: ℤ,chords: Seq[Chord]): Unit
}

//****************************************************************************
