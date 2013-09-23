package com.serotonin.m2m2.opc;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.beans.DataPointDefaulter;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import com.serotonin.validation.StringValidation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class OPCDAEditDwr extends DataSourceEditDwr {
    private static final Log LOG = LogFactory.getLog(OPCDAEditDwr.class);

    @DwrPermission(user = true)
    public ProcessResult saveOPCDataSource(BasicDataSourceVO basic, String host, String domain, String user, String password, String server, int updatePeriods, int updatePeriodType) {
        OPCDataSourceVO ds = (OPCDataSourceVO) Common.getUser().getEditDataSource();

        setBasicProps(ds, basic);
        ds.setHost(host);
        ds.setDomain(domain);
        ds.setUser(user);
        ds.setPassword(password);
        ds.setServer(server);
        ds.setUpdatePeriods(updatePeriods);
        ds.setUpdatePeriodType(updatePeriodType);

        return tryDataSourceSave(ds);
    }

    @DwrPermission(user = true)
    public ProcessResult saveOPCPointLocator(int id, String xid, String name, OPCPointLocatorVO locator) {
        return validatePoint(id, xid, name, locator, null);
    }

    @DwrPermission(user = true)
    public ProcessResult searchOpcServer(String host, String domain, String user, String password) {
        ProcessResult response = new ProcessResult();
        try {
            List serverList = OPCUtils.listOPCServers(user, password, host, domain);
            response.addData("servers", serverList);
        } catch (Exception e) {
            response.addGenericMessage("common.default", new Object[]{e.getMessage()});
        }
        return response;
    }

    @DwrPermission(user = true)
    public ProcessResult listOPCTags(String host, String domain, String user, String password, String serverName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("List tags.");
        }
        ProcessResult response = new ProcessResult();

        List opcItems = new ArrayList();
        try {
            if (LOG.isDebugEnabled())
                LOG.debug("Try list opc tags.");
            opcItems = OPCUtils.browseOPCTags(user, password, host, domain, serverName);
            if (LOG.isDebugEnabled())
                LOG.debug("Result size: " + opcItems.size());
        } catch (Exception e) {
            LOG.error(e);
            response.addGenericMessage("common.default", new Object[]{e.getMessage()});
        }

        response.addData("tags", opcItems);

        return response;
    }

    @DwrPermission(user = true)
    public void saveOPCTags(OPCItem[] opcItems) {
        for (int i = 0; i < opcItems.length; i++)
            new OPCItem("", 0, false);
    }

    @DwrPermission(user = true)
    public ProcessResult saveMultipleOPCPointLocator(String[] tags, int[] dataTypes, boolean[] settables, OPCPointLocatorVO[] locators) {
        return validateMultipleOPCPoints(tags, dataTypes, settables, locators, null);
    }

    private ProcessResult validateMultipleOPCPoints(String[] tags, int[] dataTypes, boolean[] settables, OPCPointLocatorVO[] locators, DataPointDefaulter defaulter) {
        ProcessResult response = new ProcessResult();
        OPCDataSourceVO ds = (OPCDataSourceVO) Common.getUser().getEditDataSource();

        if (ds.isNew())
            response.addContextualMessage("addBtn", "dsEdit.opc.validate.dataSourceNotSaved", new Object[0]);
        else {
            for (int i = 0; i < locators.length; i++) {
                DataPointVO dp = getPoint(-1, defaulter);
                dp.setName(tags[i]);
                locators[i].setTag(tags[i]);
                locators[i].setDataTypeId(dataTypes[i]);
                locators[i].setSettable(settables[i]);
                dp.setPointLocator(locators[i]);

                if (StringUtils.isEmpty(dp.getXid()))
                    response.addContextualMessage("xid", "validate.required", new Object[0]);
                else if (!new DataPointDao().isXidUnique(dp.getXid(), -1))
                    response.addContextualMessage("xid", "validate.xidUsed", new Object[0]);
                else if (StringValidation.isLengthGreaterThan(dp.getXid(), 50)) {
                    response.addContextualMessage("xid", "validate.notLongerThan", new Object[]{Integer.valueOf(50)});
                }

                if (!response.getHasMessages()) {
                    Common.runtimeManager.saveDataPoint(dp);
                    response.addData("id", Integer.valueOf(dp.getId()));
                    response.addData("points", getPoints());
                }
            }
        }

        return response;
    }
}