package br.org.scadabr.dnp3.rt;

import br.org.scadabr.app.DNP3Master;
import br.org.scadabr.dnp3.vo.Dnp3SerialDataSourceVO;
import br.org.scadabr.protocol.dnp3.common.InitFeatures;

public class Dnp3SerialDataSource extends Dnp3DataSource
  implements InitFeatures
{
  private final Dnp3SerialDataSourceVO configuration;

  public Dnp3SerialDataSource(Dnp3SerialDataSourceVO configuration)
  {
    super(configuration);
    this.configuration = configuration;
  }

  public void initialize()
  {
    DNP3Master dnp3Master = new DNP3Master();

    dnp3Master.setCommType(1);
    dnp3Master.setBaudrate(this.configuration.getBaudRate());
    dnp3Master.setSerialPort(this.configuration.getCommPortId());
    super.initialize(dnp3Master);
  }
}