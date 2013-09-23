package com.serotonin.m2m2.snmp.rt;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.rt.dataImage.types.AlphanumericValue;
import com.serotonin.m2m2.rt.dataImage.types.BinaryValue;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataImage.types.MultistateValue;
import com.serotonin.m2m2.rt.dataImage.types.NumericValue;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.snmp.vo.SnmpPointLocatorVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Opaque;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.Variable;

public class SnmpPointLocatorRT extends PointLocatorRT
{
  private static final Log LOG = LogFactory.getLog(SnmpPointLocatorRT.class);
  private final SnmpPointLocatorVO vo;
  private final OID oid;

  public SnmpPointLocatorRT(SnmpPointLocatorVO vo)
  {
    this.vo = vo;
    this.oid = new OID(vo.getOid());
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public OID getOid() {
    return this.oid;
  }

  public SnmpPointLocatorVO getVO() {
    return this.vo;
  }

  public DataValue variableToValue(Variable variable) {
    switch (this.vo.getDataTypeId()) {
    case 1:
      return new BinaryValue(StringUtils.equals(variable.toString(), this.vo.getBinary0Value()));
    case 2:
      return new MultistateValue(variable.toInt());
    case 3:
      if ((variable instanceof OctetString)) {
        try {
          return NumericValue.parseNumeric(variable.toString());
        }
        catch (NumberFormatException e)
        {
        }
      }
      return new NumericValue(variable.toInt());
    case 4:
      return new AlphanumericValue(variable.toString());
    }

    throw new ShouldNeverHappenException("Unknown data type id: " + this.vo.getDataTypeId());
  }

  public Variable valueToVariable(DataValue value) {
    return valueToVariableImpl(value, this.vo.getSetType());
  }

  public static Variable valueToVariableImpl(DataValue value, int setType) {
    switch (setType) {
    case 1:
      if ((value instanceof NumericValue))
        return new Integer32(value.getIntegerValue());
      if ((value instanceof BinaryValue)) {
        return new Integer32(value.getBooleanValue() ? 1 : 0);
      }
      LOG.warn("Can't convert value '" + value + "' (" + value.getDataType() + ") to Integer32");
      return new Integer32(0);
    case 2:
      return new OctetString(DataTypes.valueToString(value));
    case 3:
      return new OID(DataTypes.valueToString(value));
    case 4:
      return new IpAddress(DataTypes.valueToString(value));
    case 5:
      return new Counter32(()value.getDoubleValue());
    case 6:
      return new Gauge32(()value.getDoubleValue());
    case 7:
      return new TimeTicks(()value.getDoubleValue());
    case 8:
      return new Opaque(DataTypes.valueToString(value).getBytes());
    case 9:
      return new Counter64(()value.getDoubleValue());
    }

    throw new ShouldNeverHappenException("Unknown set type id: " + setType);
  }
}