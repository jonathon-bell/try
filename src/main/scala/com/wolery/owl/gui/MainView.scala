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

package com.wolery.owl.gui

import com.wolery.owl.utils.Logging

import de.codecentric.centerdevice.MenuToolkit
import javafx.fxml.{FXML ⇒ fx}
import javafx.scene.control.MenuBar
import javafx.scene.layout.{BorderPane,Pane}
import scalafx.Includes.jfxBorderPane2sfx
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.Node
import com.wolery.owl.core._

//****************************************************************************

class MainController extends Logging
{
  @fx var menubar: MenuBar = _

  def initialize() =
  {
    log.info("initialize")

    menubar.setUseSystemMenuBar(true)

    val tk = MenuToolkit.toolkit()

    tk.setApplicationMenu(tk.createDefaultApplicationMenu("Owl"))
  }
}

//****************************************************************************

class MainView extends PrimaryStage
{
  val (m,_) = load[BorderPane,MainController]      ("MainView")
  val (f,c) = load[Pane,      FretboardController] ("Fretboard-1")

  m.setCenter(f)

  resizable = false
  title     = "Owl"
  scene     = new Scene(m)
}

//****************************************************************************
