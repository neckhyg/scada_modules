package com.serotonin.m2m2.wibox.request;

import com.aginova.business.ProductInformation;
import com.aginova.crossbow.Message;
import com.serotonin.m2m2.wibox.MoteData;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DataRequest extends WiBoxRequest
{
  private static final Log LOG = LogFactory.getLog(DataRequest.class);
  private final MoteData moteData;
  private final String timestamp;
  private final String dataId;
  private final String dataIndex;
  private final String dataType;
  private final String data;
  private final String clbr;
  private Map<String, Object> convertedData;

  public DataRequest(String password, MoteData moteData, String timestamp, String dataId, String dataIndex, String dataType, String data, String clbr)
  {
    super(password);
    this.moteData = moteData;
    this.timestamp = timestamp;
    this.dataId = dataId;
    this.dataIndex = dataIndex;
    this.dataType = dataType;
    this.data = data;
    this.clbr = clbr;

    if ("true".equals(clbr))
      LOG.warn("Received a data request where calibration is required");
  }

  public long getUTC() {
    return toUTC(Integer.parseInt(this.timestamp));
  }

  public Object getConverted(String key) throws Exception {
    if (this.convertedData == null) {
      this.convertedData = new HashMap();
      this.convertedData.put("data_type", this.dataType);
      this.convertedData.put("data", this.data);
      Message message = new Message("Data", this.convertedData);
      ProductInformation.getInstance().convertData(this.moteData.getProductNumber(), message, (Object[][][])null);
      this.convertedData.remove("data_type");
      this.convertedData.remove("data");
    }

    return this.convertedData.get(key);
  }

  public String getMoteId() {
    return this.moteData.getModeId();
  }

  public MoteData getMoteData()
  {
    return this.moteData;
  }

  public String getTimestamp()
  {
    return this.timestamp;
  }

  public String getDataId()
  {
    return this.dataId;
  }

  public String getDataIndex()
  {
    return this.dataIndex;
  }

  public String getDataType()
  {
    return this.dataType;
  }

  public String getData()
  {
    return this.data;
  }

  public String getClbr()
  {
    return this.clbr;
  }

  public String describe()
  {
    try
    {
      getConverted("");
    }
    catch (Exception e) {
      return "Data: moteId=" + getMoteId() + ", conversion error=" + e.getMessage();
    }
    return "Data: moteId=" + getMoteId() + ", data=" + this.convertedData;
  }
}