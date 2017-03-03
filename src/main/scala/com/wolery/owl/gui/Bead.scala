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

import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.text.Text

//****************************************************************************

class Bead(text: String,style: String = "bead") extends StackPane
{
  val t = new Text(text)

  t.getStyleClass().add(style+"-text")
    getStyleClass().add(style)
    getChildren.add(t)

  setMinSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE)
  setMaxSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE)
  setPrefSize(30,30)
}

//****************************************************************************
