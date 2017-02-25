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

import com.wolery.owl.utils.Logging

import de.codecentric.centerdevice.MenuToolkit
import scalafx.Includes.jfxParent2sfx
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import javafx.fxml.{FXML,FXMLLoader}
import javafx.scene.control.MenuBar
import javafx.scene.{layout ⇒ jfxl}

//****************************************************************************

class View extends Logging
{
  @FXML
  var menubar: MenuBar = _

  def initialize() =
  {
    log.info("INIT")
    log.info(menubar.toString)

    menubar.setUseSystemMenuBar(true)

    val tk = MenuToolkit.toolkit()

    tk.setApplicationMenu(tk.createDefaultApplicationMenu("Owl"))
  }
}

object Main extends JFXApp with Logging
{
  val xml  = getClass.getResource("/Main.fxml")
  val ldr  = new javafx.fxml.FXMLLoader(xml)

  val root = ldr.load[javafx.scene.layout.BorderPane]()

  {
    val XML = getClass.getResource("/Fretboard.fxml")
    val ldr = new javafx.fxml.FXMLLoader(XML)
    val f   = ldr.load[javafx.scene.Node]()

    root.setCenter(f)
  }

  stage = new PrimaryStage()
  {
    title     = "Owl"
    resizable = false
    scene     = new Scene(root,1500,400)
  }
}

//****************************************************************************
