package com.serotonin.m2m2.meta;

import com.serotonin.m2m2.i18n.TranslatableMessage;

public class MetaPointExecutionException extends RuntimeException
{
  private static final long serialVersionUID = 1L;
  private final TranslatableMessage message;

  public MetaPointExecutionException(TranslatableMessage message)
  {
    this.message = message;
  }

  public TranslatableMessage getErrorMessage() {
    return this.message;
  }
}