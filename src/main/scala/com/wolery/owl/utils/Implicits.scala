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

package com.wolery.owl.utils

//****************************************************************************

import scala.language.implicitConversions

import javafx.concurrent.Task
import javafx.event.Event
import javafx.event.EventHandler

//****************************************************************************

object implicits
{
  implicit
  def asEventHandler[E <: Event](lambda: E ⇒ Unit): EventHandler[E] = new EventHandler[E]
  {
    def handle(e: E) = lambda(e)
  }

  implicit
  def asTask[R](lambda: ⇒ R): Task[R] = new Task[R]
  {
    def call(): R = lambda
  }

  implicit
  def asRunnable(lambda: ⇒ Unit): Runnable =  new Runnable
  {
    def run(): Unit = lambda
  }
}

//****************************************************************************
