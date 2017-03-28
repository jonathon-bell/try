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

package com.wolery.owl.core

//****************************************************************************

case class Meter(count: ℕ,beat: ℕ)
{
  assert(count>0 && beat>0)

  override
  def toString(): String = s"$count / $beat"
}

//****************************************************************************
