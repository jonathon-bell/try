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

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

import com.wolery.owl.core.Scale

import javax.sound.midi.MetaMessage

//****************************************************************************

object message
{
  private
  def encode[α](byte: Byte,any: α): MetaMessage =
  {
    val b = new ByteArrayOutputStream()
    val o = new ObjectOutputStream(b)

    o.writeObject(any)
    o.close

    new MetaMessage(byte,b.toByteArray,b.size)
  }

  private
  def decode[α](byte: Byte,meta: MetaMessage): α =
  {
    assert(meta.getType == byte)

    val b = new ByteArrayInputStream(meta.getData)
    val i = new ObjectInputStream(b)

    val t = i.readObject.asInstanceOf[α]
    i.close
    t
  }

  val HARMONY:  Byte = 0x60
  val POSITION: Byte = 0x61

  def harmony(scale: Scale): MetaMessage = encode[Scale](HARMONY,scale)
  def harmony(meta: MetaMessage): Scale  = decode[Scale](HARMONY,meta)

  def position(position: ℕ): MetaMessage = encode[ℕ]    (POSITION,position)
  def position(meta: MetaMessage): ℕ     = decode[ℕ]    (POSITION,meta)
}

//****************************************************************************
