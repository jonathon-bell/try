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

//****************************************************************************

import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Region.USE_PREF_SIZE
import scalafx.scene.layout.StackPane
import scalafx.scene.text.Text
import scalafx.Includes._

//****************************************************************************

class Bead(text: String,style: String = "bead") extends StackPane
{
  val t = new Text(text)

  t.setStyle("-fx-fill:inherit;")

  this.getStyleClass.add(style)
  this.getChildren.add(t)

  this.setMinSize(USE_PREF_SIZE,USE_PREF_SIZE)
  this.setMaxSize(USE_PREF_SIZE,USE_PREF_SIZE)
  this.setPrefSize(30,30)

  this.onMouseClicked = (e: MouseEvent) => {println("click!")}
}

//****************************************************************************
