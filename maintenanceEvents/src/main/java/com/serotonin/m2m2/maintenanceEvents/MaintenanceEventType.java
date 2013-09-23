package com.serotonin.m2m2.maintenanceEvents;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.rt.event.type.EventType;
import java.io.IOException;

public class MaintenanceEventType extends EventType
{
  public static final String TYPE_NAME = "MAINTENANCE";
  private int maintenanceId;

  public MaintenanceEventType()
  {
  }

  public MaintenanceEventType(int maintenanceId)
  {
    this.maintenanceId = maintenanceId;
  }

  public String getEventType()
  {
    return "MAINTENANCE";
  }

  public String getEventSubtype()
  {
    return null;
  }

  public String toString()
  {
    return "MaintenanceEventType(maintenanceId=" + this.maintenanceId + ")";
  }

  public int getDuplicateHandling()
  {
    return 2;
  }

  public int getReferenceId1()
  {
    return this.maintenanceId;
  }

  public int getReferenceId2()
  {
    return 0;
  }

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + this.maintenanceId;
    return result;
  }

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MaintenanceEventType other = (MaintenanceEventType)obj;

    return this.maintenanceId == other.maintenanceId;
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject)
    throws JsonException
  {
    super.jsonRead(reader, jsonObject);

    String xid = jsonObject.getString("XID");
    if (xid == null)
      throw new TranslatableJsonException("emport.error.eventType.missing.reference", new Object[] { "XID" });
    MaintenanceEventVO me = new MaintenanceEventDao().getMaintenanceEvent(xid);
    if (me == null)
      throw new TranslatableJsonException("emport.error.eventType.invalid.reference", new Object[] { "XID", xid });
    this.maintenanceId = me.getId();
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    super.jsonWrite(writer);
    writer.writeEntry("XID", new MaintenanceEventDao().getMaintenanceEvent(this.maintenanceId).getXid());
  }
}