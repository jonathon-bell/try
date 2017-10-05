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
package utils

import java.util.prefs.{Preferences ⇒ JavaPreferences}

/**
 * TODO
 *
 * @tparam α  TODO
 *
 * @author Jonathon Bell
 */
trait Preference[α] // = Property?
{
  def name              : String
  def default           : α
  def value             : α
  def value_=(value: α) : Unit

  def reset()           : Unit   = update(default)
  def apply()           : α      = value
  def apply (value: α)  : Unit   = value_=(value)
  def update(value: α)  : Unit   = value_=(value)

  override
  def toString()        : String = apply.toString
}

/**
 * TODO
 *
 * @author Jonathon Bell
 */
abstract class Preferences(private val m_imp: JavaPreferences)
{
  def this(path: String)                     = this(JavaPreferences.userRoot.node(path))
  def this(clazz: Class[_])                  = this(JavaPreferences.userNodeForPackage(clazz))
  def this(parent: Preferences,path: String) = this(parent.m_imp.node(path))

  def construct[α](get: (String,α) ⇒ α,
                   put: (String,α) ⇒ Unit)(n: String,d: α): Preference[α] =
  {
    new Preference[α]
    {
      val name              : String = n
      val default           : α      = d
      def value             : α      = get(name,default)
      def value_=(value: α) : Unit   = put(name,value)
//    def reset()           : Unit   = update(default)
//    def apply()           : α      = value
//    def apply (value: α)  : Unit   = value_=(value)
//    def update(value: α)  : Unit   = value_=(value)
    }
  }

  type Constructor[α] = (String,α) ⇒ Preference[α]

  val string: Constructor[String] = construct(m_imp.get _,       m_imp.put _)
  val bool  : Constructor[Bool]   = construct(m_imp.getBoolean _,m_imp.putBoolean _)
  val int   : Constructor[Int]    = construct(m_imp.getInt _,    m_imp.putInt _)
  val long  : Constructor[Long]   = construct(m_imp.getLong _,   m_imp.putLong _)
  val float : Constructor[Float]  = construct(m_imp.getFloat _,  m_imp.putFloat _)
  val double: Constructor[Double] = construct(m_imp.getDouble _, m_imp.putDouble _)
}

//****************************************************************************
