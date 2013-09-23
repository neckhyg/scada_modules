package br.org.scadabr.dnp3.rt;

import br.org.scadabr.dnp3.vo.Dnp3PointLocatorVO;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class Dnp3PointLocatorRT extends PointLocatorRT
{
  private final Dnp3PointLocatorVO vo;

  public Dnp3PointLocatorRT(Dnp3PointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public Dnp3PointLocatorVO getVO() {
    return this.vo;
  }
}