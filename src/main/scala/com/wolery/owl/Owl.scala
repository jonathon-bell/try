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
import com.wolery.owl.utils.implicits.asTask
import com.wolery.owl.utils.load

import javafx.concurrent.Task
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import javafx.stage.StageStyle
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequencer
import javax.sound.midi.Synthesizer
import javafx.fxml.{ FXML ⇒ fx }

import javafx.scene.control.Label
import java.util.ResourceBundle

//****************************************************************************

object owl extends utils.Application
{
  val sequencer:   Sequencer   = MidiSystem.getSequencer()
  val synthesizer: Synthesizer = MidiSystem.getSynthesizer()

  val initialize:Task[Unit] =
  {
  //updateMessage("Loading...")
    load.font("fontawesome-webfont")
    synthesizer.open()
  //synthesizer.loadAllInstruments(load.soundbank("FluidR3 GM2-2"))
    sequencer.open()
    sequencer.getTransmitter.setReceiver(synthesizer.getReceiver)
  }

  def start(stage: Stage): Unit =
  {
    /*
    val (m,_) = load.view("TransportView",new TransportController)

    new Stage(StageStyle.DECORATED)
    {
      setResizable(false)
      setTitle     ("Owl")
      setScene     (new Scene(m))
      show()
    }
*/
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
    val (t,_) = load.view("TransportView",new TransportController)

    m.asInstanceOf[BorderPane].setCenter(c.view)
    m.asInstanceOf[BorderPane].setBottom(t)

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
