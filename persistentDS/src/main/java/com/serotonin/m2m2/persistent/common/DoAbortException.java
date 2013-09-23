package com.serotonin.m2m2.persistent.common;

import com.serotonin.m2m2.i18n.TranslatableMessage;

public class DoAbortException extends Exception
{
  private static final long serialVersionUID = 5178593744483624380L;
  private final TranslatableMessage localizableMessage;

  public DoAbortException(TranslatableMessage message)
  {
    this.localizableMessage = message;
  }

  public TranslatableMessage getTranslatableMessage() {
    return this.localizableMessage;
  }
}