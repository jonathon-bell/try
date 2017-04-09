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

import javax.sound.midi.Sequencer

//****************************************************************************

object sequencer
{
  implicit final class SequencerEx(val s: Sequencer) extends AnyVal
  {
    def isPlaying: Boolean =
    {
      s.isRunning
    }

    def isLooping: Boolean =
    {
      s.getLoopCount != 0
    }

    def getEffectiveTempoInBPM: BPM =
    {
      s.getTempoFactor * s.getTempoInBPM
    }

    def setEffectiveTempoInBPM(bpm: BPM): Unit =
    {
      s.setTempoFactor(bpm.toFloat / s.getTempoInBPM)
    }

    def getTicksPerBeat: ℕ =
    {
      s.getSequence.getResolution
    }
  }
}

//****************************************************************************
