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
import com.wolery.owl.gui.load
import com.wolery.owl.stringed.StringedInstrument
import com.wolery.owl.utils.Logging

import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.control.MenuBar
import javafx.scene.layout.BorderPane
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import javax.swing.event.MenuEvent
import javafx.event.ActionEvent

//****************************************************************************

class MainController(controller: Controller) extends Logging
{
  @fx var menubar: MenuBar = _

  def initialize() =
  {
    log.info("initialize")

    menubar.setUseSystemMenuBar(true)

    val tk = de.codecentric.centerdevice.MenuToolkit.toolkit()

    tk.setApplicationMenu(tk.createDefaultApplicationMenu("Owl"))
  }

  def onDelete(ae: ActionEvent) =
  {
    println("menu delete")
    val scale = Scale(F,"whole tone").get
    val all   = controller.instrument.playable

    controller.update('harmony,Seq(all))
    controller.update('melody, Seq(all.filter(p => scale.contains(p.note))))
  }
}

//****************************************************************************

class MainView extends PrimaryStage
{
  val instrument = StringedInstrument(24,E(2),A(2),D(3),G(3),B(3),E(4))

  val (n,c) = instrument.view ("Guitar")
  val (m,_) = load[BorderPane]("MainView",new MainController(c))

  m.setCenter(n)

  resizable = false
  title     = "Owl"
  scene     = new Scene(m)
}

//****************************************************************************
