package br.org.scadabr.dnp3.rt;

import br.org.scadabr.app.DNP3Master;
import br.org.scadabr.dnp3.vo.Dnp3IpDataSourceVO;
import br.org.scadabr.protocol.dnp3.common.InitFeatures;

public class Dnp3IpDataSource extends Dnp3DataSource
  implements InitFeatures
{
  private final Dnp3IpDataSourceVO configuration;

  public Dnp3IpDataSource(Dnp3IpDataSourceVO configuration)
  {
    super(configuration);
    this.configuration = configuration;
  }

  public void initialize()
  {
    DNP3Master dnp3Master = new DNP3Master();
    dnp3Master.setCommType(2);
    dnp3Master.setHost(this.configuration.getHost());
    dnp3Master.setPort(this.configuration.getPort());
    super.initialize(dnp3Master);
  }
}