package br.org.scadabr.dnp3;

import br.org.scadabr.dnp3.dwr.DnpEditDwr;
import br.org.scadabr.dnp3.vo.Dnp3SerialDataSourceVO;
import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class Dnp3SerialDataSourceDefinition extends DataSourceDefinition
{
  public String getDataSourceTypeName()
  {
    return "DNP3_SERIAL";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.dnp3Serial";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new Dnp3SerialDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editDnp3.jsp";
  }

  public Class<?> getDwrClass()
  {
    return DnpEditDwr.class;
  }
}