package br.org.scadabr.dnp3;

import br.org.scadabr.dnp3.dwr.DnpEditDwr;
import br.org.scadabr.dnp3.vo.Dnp3IpDataSourceVO;
import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class Dnp3IpDataSourceDefinition extends DataSourceDefinition
{
  public void preInitialize()
  {
    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "DNP3_IP", 20, null));
  }

  public String getDataSourceTypeName()
  {
    return "DNP3_IP";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.dnp3Ip";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new Dnp3IpDataSourceVO();
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