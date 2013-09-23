package com.serotonin.m2m2.galil.rt;

import com.serotonin.m2m2.galil.vo.VariablePointTypeVO;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.types.AlphanumericValue;
import com.serotonin.m2m2.rt.dataImage.types.BinaryValue;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataImage.types.MultistateValue;
import com.serotonin.m2m2.rt.dataImage.types.NumericValue;

public class VariablePointTypeRT extends PointTypeRT
{
  private final VariablePointTypeVO vo;

  public VariablePointTypeRT(VariablePointTypeVO vo)
  {
    super(vo);
    this.vo = vo;
  }

  protected String getPollRequestImpl()
  {
    if (this.vo.getDataTypeId() == 4)
      return new StringBuilder().append(this.vo.getVariableName()).append("={S6}").toString();
    return new StringBuilder().append(this.vo.getVariableName()).append("=").toString(); }
  public DataValue parsePollResponse(String data, String pointName) throws TranslatableException {
      //TODO
     return null;
  } // Byte code:
    //   0: aload_0
    //   1: getfield 2	com/serotonin/m2m2/galil/rt/VariablePointTypeRT:vo	Lcom/serotonin/m2m2/galil/vo/VariablePointTypeVO;
    //   4: invokevirtual 3	com/serotonin/m2m2/galil/vo/VariablePointTypeVO:getDataTypeId	()I
    //   7: iconst_4
    //   8: if_icmpne +12 -> 20
    //   11: new 11	com/serotonin/m2m2/rt/dataImage/types/AlphanumericValue
    //   14: dup
    //   15: aload_1
    //   16: invokespecial 12	com/serotonin/m2m2/rt/dataImage/types/AlphanumericValue:<init>	(Ljava/lang/String;)V
    //   19: areturn
    //   20: aload_1
    //   21: invokestatic 13	java/lang/Double:parseDouble	(Ljava/lang/String;)D
    //   24: dstore_3
    //   25: aload_0
    //   26: getfield 2	com/serotonin/m2m2/galil/rt/VariablePointTypeRT:vo	Lcom/serotonin/m2m2/galil/vo/VariablePointTypeVO;
    //   29: invokevirtual 3	com/serotonin/m2m2/galil/vo/VariablePointTypeVO:getDataTypeId	()I
    //   32: iconst_1
    //   33: if_icmpne +22 -> 55
    //   36: new 14	com/serotonin/m2m2/rt/dataImage/types/BinaryValue
    //   39: dup
    //   40: dload_3
    //   41: dconst_0
    //   42: dcmpl
    //   43: ifeq +7 -> 50
    //   46: iconst_1
    //   47: goto +4 -> 51
    //   50: iconst_0
    //   51: invokespecial 15	com/serotonin/m2m2/rt/dataImage/types/BinaryValue:<init>	(Z)V
    //   54: areturn
    //   55: aload_0
    //   56: getfield 2	com/serotonin/m2m2/galil/rt/VariablePointTypeRT:vo	Lcom/serotonin/m2m2/galil/vo/VariablePointTypeVO;
    //   59: invokevirtual 3	com/serotonin/m2m2/galil/vo/VariablePointTypeVO:getDataTypeId	()I
    //   62: iconst_2
    //   63: if_icmpne +13 -> 76
    //   66: new 16	com/serotonin/m2m2/rt/dataImage/types/MultistateValue
    //   69: dup
    //   70: dload_3
    //   71: d2i
    //   72: invokespecial 17	com/serotonin/m2m2/rt/dataImage/types/MultistateValue:<init>	(I)V
    //   75: areturn
    //   76: new 18	com/serotonin/m2m2/rt/dataImage/types/NumericValue
    //   79: dup
    //   80: dload_3
    //   81: invokespecial 19	com/serotonin/m2m2/rt/dataImage/types/NumericValue:<init>	(D)V
    //   84: areturn
    //   85: astore_3
    //   86: new 21	com/serotonin/m2m2/i18n/TranslatableException
    //   89: dup
    //   90: new 22	com/serotonin/m2m2/i18n/TranslatableMessage
    //   93: dup
    //   94: ldc 23
    //   96: iconst_1
    //   97: anewarray 24	java/lang/Object
    //   100: dup
    //   101: iconst_0
    //   102: aload_1
    //   103: aastore
    //   104: invokespecial 25	com/serotonin/m2m2/i18n/TranslatableMessage:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   107: invokespecial 26	com/serotonin/m2m2/i18n/TranslatableException:<init>	(Lcom/serotonin/m2m2/i18n/TranslatableMessage;)V
    //   110: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   20	54	85	java/lang/NumberFormatException
    //   55	75	85	java/lang/NumberFormatException
    //   76	84	85	java/lang/NumberFormatException }

  protected String getSetRequestImpl(DataValue value) {
          StringBuilder data = new StringBuilder();
    data.append(this.vo.getVariableName()).append('=');

    if (this.vo.getDataTypeId() == 1)
      data.append(value.getBooleanValue() ? '1' : '0');
    else if (this.vo.getDataTypeId() == 2)
      data.append(value.getIntegerValue());
    else if (this.vo.getDataTypeId() == 3)
      data.append(value.getDoubleValue());
    else {
      data.append('"').append(value.getStringValue()).append('"');
    }
    return data.toString();
      }

    public DataValue parseSetResponse(String data)
    throws TranslatableException
  {
    if (!"".equals(data))
      throw new TranslatableException(new TranslatableMessage("event.galil.unexpected", new Object[] { data }));
    return null;
  }
}