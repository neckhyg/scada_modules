package com.serotonin.m2m2.persistent.common;

import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.i18n.TranslatableMessage;

public class PersistentAbortException extends TranslatableException
{
  private static final long serialVersionUID = 5178593744483624380L;

  public PersistentAbortException(TranslatableMessage message)
  {
    super(message);
  }
}