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
//*  See Also: http://tpolecat.github.io/2015/07/30/infer.html
//*
//*
//****************************************************************************

package com.wolery.owl.utils

//****************************************************************************

import javafx.fxml.FXMLLoader
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequence
import javax.sound.midi.Soundbank
import javafx.scene.layout.Pane

//****************************************************************************

object load
{
  def view[Controller](name: String): (Pane,Controller) =
  {
    val l = new FXMLLoader(getClass.getResource(s"/fxml/$name.fxml"))
    val n = l.load[Pane]
    val c = l.getController[Controller]

    (n,c)
  }

  def view[Controller](name: String,controller: Controller): (Pane,Controller) =
  {
    val l = new FXMLLoader(getClass.getResource(s"/fxml/$name.fxml"))

    l.setController(controller)

    (l.load[Pane],controller)
  }

  def soundbank(name: String): Soundbank =
  {
    MidiSystem.getSoundbank(getClass.getResource(s"/sf2/$name.sf2"))
  }

  def sequence(name: String): Sequence =
  {
    MidiSystem.getSequence(getClass.getResource(s"/midi/$name.mid"))
  }
}

//****************************************************************************
