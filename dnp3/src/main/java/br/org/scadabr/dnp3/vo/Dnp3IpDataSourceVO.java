package br.org.scadabr.dnp3.vo;

import br.org.scadabr.dnp3.rt.Dnp3IpDataSource;
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

public class Dnp3IpDataSourceVO extends Dnp3DataSourceVO<Dnp3IpDataSourceVO>
{

  @JsonProperty
  private String host = "localhost";

  @JsonProperty
  private int port = 20000;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.host + ":" + this.port });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new Dnp3IpDataSource(this);
  }

  public String getHost()
  {
    return this.host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);
    if (StringUtils.isBlank(this.host))
      response.addContextualMessage("host", "validate.required", new Object[0]);
    if ((this.port <= 0) || (this.port > 65535))
      response.addContextualMessage("port", "validate.invalidValue", new Object[0]);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, Dnp3IpDataSourceVO from)
  {
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.host);
    out.writeInt(this.port);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.host = SerializationHelper.readSafeUTF(in);
      this.port = in.readInt();
    }
  }
}