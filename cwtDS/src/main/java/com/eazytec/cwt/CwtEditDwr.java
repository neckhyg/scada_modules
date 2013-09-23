package com.eazytec.cwt;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;

public class CwtEditDwr extends DataSourceEditDwr {
    @DwrPermission(user = true)
    public ProcessResult saveCwtDataSource(BasicDataSourceVO basic, int port,int bufferSize, int idleTime) {
        CwtDataSourceVO ds = (CwtDataSourceVO) Common.getUser().getEditDataSource();
        setBasicProps(ds, basic);
        ds.setPort(port);
        ds.setBufferSize(bufferSize);
        ds.setIdleTime(idleTime);
        return tryDataSourceSave(ds);
    }

    @DwrPermission(user = true)
    public ProcessResult saveCwtPointLocator(int id, String xid, String name, CwtPointLocatorVO locator) {
        return validatePoint(id, xid, name, locator, null);
    }

    @DwrPermission(user = true)
    public ProcessResult getCwtStatus() {
        CwtDataSourceVO ds = (CwtDataSourceVO) Common.getUser().getEditDataSource();
        CwtDataSourceRT rt = (CwtDataSourceRT) Common.runtimeManager.getRunningDataSource(ds.getId());

        ProcessResult processResult = new ProcessResult();

//        if (rt == null) {
//            processResult.addGenericMessage("dsEdit.persistent.status.notEnabled", new Object[0]);
//        } else {
//            int conns = rt.getConnectionCount();
//            if (conns == 0) {
//                processResult.addGenericMessage("dsEdit.persistent.status.noConnections", new Object[0]);
//            } else {
//                long now = System.currentTimeMillis();
//                for (int i = 0; i < conns; i++) {
//                    processResult.addGenericMessage("dsEdit.persistent.status.connection", new Object[]{rt.getConnectionAddress(i), DateUtils.getDuration(now - rt.getConnectionTime(i)), NumberUtils.countDescription(rt.getPacketsReceived(i))});
//                }
//            }
//        }

        return processResult;
    }
}
