package com.eazytec.cwt;

import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class CwtPointLocatorRT extends PointLocatorRT{
    private final CwtPointLocatorVO vo;

    public CwtPointLocatorRT(CwtPointLocatorVO vo) {
        this.vo = vo;
    }

    public boolean isSettable() {
        return this.vo.isSettable();
    }

    public CwtPointLocatorVO getPointLocatorVO(){
        return this.vo;
    }
}
