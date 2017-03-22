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

import com.wolery.owl.core._
import com.wolery.owl.core.Scale
import com.wolery.owl.utils.Logging
import com.wolery.owl.utils.load

import javafx.event.ActionEvent
import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.control.MenuBar
import javax.sound.midi.MetaEventListener
import javax.sound.midi.MetaMessage

//****************************************************************************

class MainController(controller: Controller) extends Logging
{
  @fx
  var menubar   : MenuBar    = _
  val instrument: Instrument = controller.instrument
  val playable  : Pitches    = instrument.playable

  def initialize() =
  {
    log.info("initialize")

    menubar.setUseSystemMenuBar(true)

    val tk = de.codecentric.centerdevice.MenuToolkit.toolkit()

    tk.setApplicationMenu(tk.createDefaultApplicationMenu("Owl"))
  }

  def onCIonian(ae: ActionEvent) = {}
  def onCWholeTone(ae: ActionEvent) = {}

  def onPlay() =
  {
    owl.sequencer.getTransmitter.setReceiver(controller)
    owl.sequencer.addMetaEventListener(new MetaEventListener{def meta(m:MetaMessage):Unit = controller.send(m,-1)})

    owl.sequencer.setSequence(load.sequence("sample"))
    controller.send(message.harmony(Scale(C,"whole tone").get),-1)
    owl.sequencer.setTempoInBPM(10)
    owl.sequencer.start()
  }

  def onClose() =
  {
    owl.sequencer.stop()
  }
}

//****************************************************************************
