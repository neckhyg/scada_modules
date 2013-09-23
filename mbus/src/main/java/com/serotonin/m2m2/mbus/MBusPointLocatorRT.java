package com.serotonin.m2m2.mbus;

import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import net.sf.mbus4j.MBusAddressing;
import net.sf.mbus4j.dataframes.MBusMedium;
import net.sf.mbus4j.dataframes.datablocks.DataBlock;
import net.sf.mbus4j.dataframes.datablocks.dif.DataFieldCode;
import net.sf.mbus4j.dataframes.datablocks.dif.FunctionField;
import net.sf.mbus4j.dataframes.datablocks.vif.Vife;
import net.sf.mbus4j.master.ValueRequestPointLocator;

public class MBusPointLocatorRT extends PointLocatorRT
{
  private final MBusPointLocatorVO vo;

  public MBusPointLocatorRT(MBusPointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return false;
  }

  public MBusPointLocatorVO getVo()
  {
    return this.vo;
  }

  public ValueRequestPointLocator<DataPointRT> createValueRequestPointLocator(DataPointRT point) {
    ValueRequestPointLocator result = new ValueRequestPointLocator();
    result.setAddressing(MBusAddressing.fromLabel(this.vo.getAddressing()));
    result.setAddress(this.vo.getAddress());
    result.setDeviceUnit(this.vo.getDeviceUnit());
    result.setDifCode(DataFieldCode.fromLabel(this.vo.getDifCode()));
    result.setFunctionField(FunctionField.fromLabel(this.vo.getFunctionField()));
    result.setIdentnumber(this.vo.getIdentNumber());
    result.setManufacturer(this.vo.getManufacturer());
    result.setMedium(MBusMedium.fromLabel(this.vo.getMedium()));
    result.setReference(point);
    result.setResponseFrameName(this.vo.getResponseFrame());
    result.setStorageNumber(this.vo.getStorageNumber());
    result.setTariff(this.vo.getTariff());
    result.setVersion(this.vo.getVersion());
    result.setVif(DataBlock.getVif(this.vo.getVifType(), this.vo.getVifLabel(), this.vo.getUnitOfMeasurement(), this.vo.getSiPrefix(), this.vo.getExponent()));

    if (this.vo.getVifeLabels().length == 0) {
      result.setVifes(DataBlock.EMPTY_VIFE);
    }
    else {
      Vife[] vifes = new Vife[this.vo.getVifeLabels().length];
      for (int i = 0; i < this.vo.getVifeLabels().length; i++) {
        vifes[i] = DataBlock.getVife(this.vo.getVifeTypes()[i], this.vo.getVifeLabels()[i]);
      }
      result.setVifes(vifes);
    }
    return result;
  }
}