package br.org.scadabr.dnp3.vo;

import br.org.scadabr.dnp3.rt.Dnp3SerialDataSource;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class Dnp3SerialDataSourceVO extends Dnp3DataSourceVO<Dnp3SerialDataSourceVO>
{

  @JsonProperty
  private String commPortId;

  @JsonProperty
  private int baudRate = 9600;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.commPortId });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new Dnp3SerialDataSource(this);
  }

  public int getBaudRate()
  {
    return this.baudRate;
  }

  public void setBaudRate(int baudRate) {
    this.baudRate = baudRate;
  }

  public String getCommPortId() {
    return this.commPortId;
  }

  public void setCommPortId(String commPortId) {
    this.commPortId = commPortId;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);
    if (StringUtils.isBlank(this.commPortId))
      response.addContextualMessage("commPortId", "validate.required", new Object[0]);
    if (this.baudRate <= 0)
      response.addContextualMessage("baudRate", "validate.invalidValue", new Object[0]);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, Dnp3SerialDataSourceVO from)
  {
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.commPortId);
    out.writeInt(this.baudRate);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.commPortId = SerializationHelper.readSafeUTF(in);
      this.baudRate = in.readInt();
    }
  }
}