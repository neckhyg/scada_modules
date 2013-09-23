package com.serotonin.m2m2.opc;

import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class OPCPointLocatorRT extends PointLocatorRT {
    private final OPCPointLocatorVO vo;

    public OPCPointLocatorRT(OPCPointLocatorVO vo) {
        this.vo = vo;
    }

    public boolean isSettable() {
        return this.vo.isSettable();
    }

    public OPCPointLocatorVO getVo() {
        return this.vo;
    }
}