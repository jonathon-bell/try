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
import javax.sound.midi._
import com.wolery.owl.core.Meter
import com.wolery.owl.core.Bool
import java.io.InputStream

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

final class Sequencer2(m_seq: javax.sound.midi.Sequencer)
{
  private var m_meter: Tick ⇒ Meter = t ⇒ Meter(4,4)
  private var m_tempo: Tick ⇒ Tempo = t ⇒ 120.0

  /**
   * Sets the current sequence on which the sequencer operates.
   *
   * This method can be called even if the Sequencer</code> is closed.
   *
   * @param  sequence The sequence to be loaded.
   *
   * @throws InvalidMidiDataException if the sequence contains invalid
   *         MIDI data, or is not supported.
   */
  def setSequence(sequence: Sequence): Unit =
  {
    m_seq.setSequence(sequence)

    TempoMap.getTimingMaps(sequence) match
    {
      case (m,t) => m_meter = m; m_tempo = t
    }
  }



  /**
   * Sets the current sequence on which the sequencer operates.
   * The stream must point to MIDI file data.
   *
   * <p>This method can be called even if the
   * <code>Sequencer</code> is closed.
   *
   * @param stream stream containing MIDI file data.
   * @throws IOException if an I/O exception occurs during reading of the stream.
   * @throws InvalidMidiDataException if invalid data is encountered
   * in the stream, or the stream is not supported.
   */
  def setSequence(stream: InputStream): Unit =
  {
    m_seq.setSequence(stream)

    TempoMap.getTimingMaps(m_seq.getSequence) match
    {
      case (m,t) => m_meter = m; m_tempo = t
    }
  }

  /**
   * Obtains the sequence on which the Sequencer is currently operating.
   *
   * <p>This method can be called even if the
   * <code>Sequencer</code> is closed.
   *
   * @return the current sequence, or <code>null</code> if no sequence is currently set.
   */
  def getSequence(): Sequence = m_seq.getSequence

  /**
   * Starts playback of the MIDI data in the currently
   * loaded sequence.
   * Playback will begin from the current position.
   * If the playback position reaches the loop end point,
   * and the loop count is greater than 0, playback will
   * resume at the loop start point for the number of
   * repetitions set with <code>setLoopCount</code>.
   * After that, or if the loop count is 0, playback will
   * continue to play to the end of the sequence.
   *
   * <p>The implementation ensures that the synthesizer
   * is brought to a consistent state when jumping
   * to the loop start point by sending appropriate
   * controllers, pitch bend, and program change events.
   *
   * @throws IllegalStateException if the <code>Sequencer</code> is
   * closed.
   *
   * @see #setLoopStartPoint
   * @see #setLoopEndPoint
   * @see #setLoopCount
   * @see #stop
   */
  def start(): Unit = m_seq.start()

  /**
   * Stops recording, if active, and playback of the currently loaded sequence,
   * if any.
   *
   * @throws IllegalStateException if the <code>Sequencer</code> is
   * closed.
   *
   * @see #start
   * @see #isRunning
   */
  def stop(): Unit = m_seq.stop()

  /**
   * Indicates whether the Sequencer is currently running.  The default is <code>false</code>.
   * The Sequencer starts running when either <code>{@link #start}</code> or <code>{@link #startRecording}</code>
   * is called.  <code>isRunning</code> then returns <code>true</code> until playback of the
   * sequence completes or <code>{@link #stop}</code> is called.
   * @return <code>true</code> if the Sequencer is running, otherwise <code>false</code>
   */
  def isRunning(): Bool = m_seq.isRunning

  /**
   * Obtains the current tempo, expressed in beats per minute.  The
   * actual tempo of playback is the product of the returned value
   * and the tempo factor.
   *
   * @return the current tempo in beats per minute
   *
   * @see #getTempoFactor
   * @see #setTempoInBPM(float)
   * @see #getTempoInMPQ
   */
  def getTempoInBPM(): Tempo =
  {
    m_seq.getTempoFactor * m_seq.getTempoInBPM
  }

  /**
   * Sets the tempo in beats per minute.   The actual tempo of playback
   * is the product of the specified value and the tempo factor.
   *
   * @param bpm desired new tempo in beats per minute
   * @see #getTempoFactor
   * @see #setTempoInMPQ(float)
   * @see #getTempoInBPM
   */
  def setTempoInBPM(bpm: Tempo): Unit =
  {
    m_seq.setTempoFactor(bpm.toFloat / m_seq.getTempoInBPM)
  }

  /**
   * Obtains the length of the current sequence, expressed in MIDI ticks,
   * or 0 if no sequence is set.
   * @return length of the sequence in ticks
   */
  def getTickLength(): Tick = m_seq.getTickLength

  /**
   * Obtains the current position in the sequence, expressed in MIDI
   * ticks.  (The duration of a tick in seconds is determined both by
   * the tempo and by the timing resolution stored in the
   * <code>{@link Sequence}</code>.)
   *
   * @return current tick
   * @see #setTickPosition
   */
  def getTickPosition(): Tick = m_seq.getTickPosition

  /**
   * Sets the current sequencer position in MIDI ticks
   * @param tick the desired tick position
   * @see #getTickPosition
   */
  def setTickPosition(tick: Tick): Unit = m_seq.setTickPosition(tick)

  /**
   * Obtains the length of the current sequence, expressed in microseconds,
   * or 0 if no sequence is set.
   * @return length of the sequence in microseconds.
   */
  def getMicrosecondLength(): Long = m_seq.getMicrosecondLength

  /**
   * Obtains the current position in the sequence, expressed in
   * microseconds.
   * @return the current position in microseconds
   * @see #setMicrosecondPosition
   */
  def getMicrosecondPosition(): Long = m_seq.getMicrosecondPosition

  /**
   * Sets the current position in the sequence, expressed in microseconds
   * @param microseconds desired position in microseconds
   * @see #getMicrosecondPosition
   */
  def setMicrosecondPosition(microseconds: Long): Unit = m_seq.setMicrosecondPosition(microseconds)

  /**
   * Sets the mute state for a track.  This method may fail for a number
   * of reasons.  For example, the track number specified may not be valid
   * for the current sequence, or the sequencer may not support this functionality.
   * An application which needs to verify whether this operation succeeded should
   * follow this call with a call to <code>{@link #getTrackMute}</code>.
   *
   * @param track the track number.  Tracks in the current sequence are numbered
   * from 0 to the number of tracks in the sequence minus 1.
   * @param mute the new mute state for the track.  <code>true</code> implies the
   * track should be muted, <code>false</code> implies the track should be unmuted.
   * @see #getSequence
   */
  def setTrackMute(track: ℕ,mute: Bool): Unit = m_seq.setTrackMute(track,mute)

  /**
   * Obtains the current mute state for a track.  The default mute
   * state for all tracks which have not been muted is false.  In any
   * case where the specified track has not been muted, this method should
   * return false.  This applies if the sequencer does not support muting
   * of tracks, and if the specified track index is not valid.
   *
   * @param track the track number.  Tracks in the current sequence are numbered
   * from 0 to the number of tracks in the sequence minus 1.
   * @return <code>true</code> if muted, <code>false</code> if not.
   */
  def isTrackMuted(track: ℕ): Bool = m_seq.getTrackMute(track)

  /**
   * Sets the solo state for a track.  If <code>solo</code> is <code>true</code>
   * only this track and other solo'd tracks will sound. If <code>solo</code>
   * is <code>false</code> then only other solo'd tracks will sound, unless no
   * tracks are solo'd in which case all un-muted tracks will sound.
   * <p>
   * This method may fail for a number
   * of reasons.  For example, the track number specified may not be valid
   * for the current sequence, or the sequencer may not support this functionality.
   * An application which needs to verify whether this operation succeeded should
   * follow this call with a call to <code>{@link #getTrackSolo}</code>.
   *
   * @param track the track number.  Tracks in the current sequence are numbered
   * from 0 to the number of tracks in the sequence minus 1.
   * @param solo the new solo state for the track.  <code>true</code> implies the
   * track should be solo'd, <code>false</code> implies the track should not be solo'd.
   * @see #getSequence
   */
  def setTrackSolo(track: ℕ,solo: Bool): Unit = m_seq.setTrackSolo(track,solo)

  /**
   * Obtains the current solo state for a track.  The default mute
   * state for all tracks which have not been solo'd is false.  In any
   * case where the specified track has not been solo'd, this method should
   * return false.  This applies if the sequencer does not support soloing
   * of tracks, and if the specified track index is not valid.
   *
   * @param track the track number.  Tracks in the current sequence are numbered
   * from 0 to the number of tracks in the sequence minus 1.
   * @return <code>true</code> if solo'd, <code>false</code> if not.
   */
  def isTrackSolod(track: ℕ): Unit = m_seq.getTrackSolo(track)

  /**
   * Registers a meta-event listener to receive
   * notification whenever a meta-event is encountered in the sequence
   * and processed by the sequencer. This method can fail if, for
   * instance,this class of sequencer does not support meta-event
   * notification.
   *
   * @param listener listener to add
   * @return <code>true</code> if the listener was successfully added,
   * otherwise <code>false</code>
   *
   * @see #removeMetaEventListener
   * @see MetaEventListener
   * @see MetaMessage
   */
  def addMetaEventListener(listener: MetaEventListener): Unit =
  {
    m_seq.addMetaEventListener(listener)
  }

  /**
   * Removes the specified meta-event listener from this sequencer's
   * list of registered listeners, if in fact the listener is registered.
   *
   * @param listener the meta-event listener to remove
   * @see #addMetaEventListener
   */
  def removeMetaEventListener(listener: MetaEventListener): Unit =
  {
    m_seq.removeMetaEventListener(listener)
  }

  /**
   * Obtains the start position of the loop,
   * in MIDI ticks.
   *
   * @return the start position of the loop,
             in MIDI ticks (zero-based)
   * @see #setLoopStartPoint
   * @since 1.5
   */
  def getLoopStartPoint(): Tick = m_seq.getLoopStartPoint

  /**
   * Sets the first MIDI tick that will be
   * played in the loop. If the loop count is
   * greater than 0, playback will jump to this
   * point when reaching the loop end point.
   *
   * <p>A value of 0 for the starting point means the
   * beginning of the loaded sequence. The starting
   * point must be lower than or equal to the ending
   * point, and it must fall within the size of the
   * loaded sequence.
   *
   * <p>A sequencer's loop start point defaults to
   * start of the sequence.
   *
   * @param tick the loop's starting position,
   *        in MIDI ticks (zero-based)
   * @throws IllegalArgumentException if the requested
   *         loop start point cannot be set, usually because
   *         it falls outside the sequence's
   *         duration or because the start point is
   *         after the end point
   *
   * @see #setLoopEndPoint
   * @see #setLoopCount
   * @see #getLoopStartPoint
   * @see #start
   * @since 1.5
   */
  def setLoopStartPoint(tick: Tick): Unit = m_seq.setLoopStartPoint(tick)

  /**
   * Obtains the end position of the loop,
   * in MIDI ticks.
   *
   * @return the end position of the loop, in MIDI
   *         ticks (zero-based), or -1 to indicate
   *         the end of the sequence
   * @see #setLoopEndPoint
   * @since 1.5
   */
  def getLoopEndPoint(): Tick = m_seq.getLoopEndPoint

  /**
   * Sets the last MIDI tick that will be played in
   * the loop. If the loop count is 0, the loop end
   * point has no effect and playback continues to
   * play when reaching the loop end point.
   *
   * <p>A value of -1 for the ending point
   * indicates the last tick of the sequence.
   * Otherwise, the ending point must be greater
   * than or equal to the starting point, and it must
   * fall within the size of the loaded sequence.
   *
   * <p>A sequencer's loop end point defaults to -1,
   * meaning the end of the sequence.
   *
   * @param tick the loop's ending position,
   *        in MIDI ticks (zero-based), or
   *        -1 to indicate the final tick
   * @throws IllegalArgumentException if the requested
   *         loop point cannot be set, usually because
   *         it falls outside the sequence's
   *         duration or because the ending point is
   *         before the starting point
   *
   * @see #setLoopStartPoint
   * @see #setLoopCount
   * @see #getLoopEndPoint
   * @see #start
   * @since 1.5
   */
  def setLoopEndPoint(tick: Tick): Unit = m_seq.setLoopStartPoint(tick)

  /**
   * Obtains the number of repetitions for
   * playback.
   *
   * @return the number of loops after which
   *         playback plays to the end of the
   *         sequence
   * @see #setLoopCount
   * @see #start
   * @since 1.5
   */
  def isLooping: Bool = m_seq.getLoopCount < 0

  /**
   * Sets the number of repetitions of the loop for
   * playback.
   * When the playback position reaches the loop end point,
   * it will loop back to the loop start point
   * <code>count</code> times, after which playback will
   * continue to play to the end of the sequence.
   * <p>
   * If the current position when this method is invoked
   * is greater than the loop end point, playback
   * continues to the end of the sequence without looping,
   * unless the loop end point is changed subsequently.
   * <p>
   * A <code>count</code> value of 0 disables looping:
   * playback will continue at the loop end point, and it
   * will not loop back to the loop start point.
   * This is a sequencer's default.
   *
   * <p>If playback is stopped during looping, the
   * current loop status is cleared; subsequent start
   * requests are not affected by an interrupted loop
   * operation.
   *
   * @param count the number of times playback should
   *        loop back from the loop's end position
   *        to the loop's start position, or
   *        <code>{@link #LOOP_CONTINUOUSLY}</code>
   *        to indicate that looping should
   *        continue until interrupted
   *
   * @throws IllegalArgumentException if <code>count</code> is
   * negative and not equal to {@link #LOOP_CONTINUOUSLY}
   *
   * @see #setLoopStartPoint
   * @see #setLoopEndPoint
   * @see #getLoopCount
   * @see #start
   * @since 1.5
   */
  def setLooping(loop: Bool): Unit =
  {
    m_seq.setLoopCount(if (loop) -1 else 0)
  }
}

//****************************************************************************
