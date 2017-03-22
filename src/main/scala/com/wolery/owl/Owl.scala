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

import com.wolery.owl.core._
import com.wolery.owl.utils.load

import javafx.concurrent.Task
import javafx.scene.layout.BorderPane
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle
import javax.sound.midi._

//****************************************************************************

object owl extends utils.Application
{
  val sequencer:   Sequencer   = MidiSystem.getSequencer()
  val synthesizer: Synthesizer = MidiSystem.getSynthesizer()

  val initialize = new Task[Unit]
  {
    def call(): Unit =
    {
      updateMessage("Loading...")

      synthesizer.open()
    //synthesizer.loadAllInstruments(load.soundbank("fluid-soundbank"))
      sequencer.open()

      sequencer.getTransmitter.setReceiver(synthesizer.getReceiver)

    //val console = new utils.DumpReceiver()
    //sequencer.getTransmitter.setReceiver(console)
    //sequencer.addMetaEventListener(new MetaEventListener() {def meta(m:MetaMessage) = console.send(m,-1)})
    }
  }

  def start(stage: Stage): Unit =
  {
     splash(stage,initialize,() ⇒ mainView())
  }

  override
  def stop() =
  {
    sequencer.close()
    synthesizer.close()
  }

  def mainView(): Unit =
  {
    val instrument = stringed.StringedInstrument(24,E(2),A(2),D(3),G(3),B(3),E(4))

    val (_,c) = instrument.view ("Guitar")
    val (m,_) = load.view("MainView",new MainController(c))

    m.asInstanceOf[BorderPane].setCenter(c.view)

    new Stage(StageStyle.DECORATED)
    {
      setResizable(false)
      setTitle     ("Owl")
      setScene     (new Scene(m))
      show()
    }
  }
}

//****************************************************************************
