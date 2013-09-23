package com.serotonin.m2m2.meta;

import com.serotonin.db.MappedRowCallback;
import com.serotonin.db.pair.IntStringPair;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.db.dao.PointValueDao;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.IdPointValueTime;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.script.CompiledScriptExecutor;
import com.serotonin.m2m2.rt.script.DataPointStateException;
import com.serotonin.m2m2.rt.script.ResultTypeException;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.vo.permission.Permissions;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import com.serotonin.m2m2.web.taglib.Functions;
import com.serotonin.timer.SimulationTimer;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetaEditDwr extends DataSourceEditDwr {
    private DataPointRT activeRT;

    @DwrPermission(user = true)
    public ProcessResult saveMetaDataSource(BasicDataSourceVO basic) {
        MetaDataSourceVO ds = (MetaDataSourceVO) Common.getUser().getEditDataSource();
        setBasicProps(ds, basic);
        return tryDataSourceSave(ds);
    }

    @DwrPermission(user = true)
    public ProcessResult saveMetaPointLocator(int id, String xid, String name, MetaPointLocatorVO locator) {
        return validatePoint(id, xid, name, locator, null);
    }

    @DwrPermission(user = true)
    public ProcessResult validateScript(String script, List<IntStringPair> context, int dataTypeId) {
        ProcessResult response = new ProcessResult();
        try {
            Map convertedContext = CompiledScriptExecutor.convertContext(context);
            CompiledScript s = CompiledScriptExecutor.compile(script);
            PointValueTime pvt = CompiledScriptExecutor.execute(s, convertedContext, System.currentTimeMillis(), dataTypeId, -1L);

            if (pvt.getTime() == -1L)
                response.addContextualMessage("script", "dsEdit.meta.test.success", new Object[]{pvt.getValue()});
            else
                response.addContextualMessage("script", "dsEdit.meta.test.successTs", new Object[]{pvt.getValue(), Functions.getTime(pvt.getTime())});
        } catch (DataPointStateException e) {
            response.addMessage("context", e.getTranslatableMessage());
        } catch (ScriptException e) {
            response.addContextualMessage("script", "dsEdit.meta.test.scriptError", new Object[]{e.getMessage()});
        } catch (ResultTypeException e) {
            response.addMessage("script", e.getTranslatableMessage());
        }

        return response;
    }

    @DwrPermission(user = true)
    public TranslatableMessage generateMetaPointHistory(int pointId) {
        DataPointDao dataPointDao = new DataPointDao();
        PointValueDao pointValueDao = new PointValueDao();
        DataPointVO pvo = dataPointDao.getDataPoint(pointId);

        Permissions.ensureDataSourcePermission(Common.getUser(), pvo.getDataSourceId());

        pvo.getEventDetectors().clear();
        MetaPointLocatorVO plvo = (MetaPointLocatorVO) pvo.getPointLocator();
        final HistoricalMetaPointLocatorRT plrt = new HistoricalMetaPointLocatorRT(plvo);
        final SimulationTimer simTimer = new SimulationTimer();
        DataPointRT prt = new DataPointRT(pvo, plrt);
        prt.initializeHistorical();
        try {
            long from = 0L;
            List dataPointIds = new ArrayList();
            for (IntStringPair ivp : plvo.getContext()) {
                long incep = pointValueDao.getInceptionDate(ivp.getKey());
                if (incep == -1L) {
                    DataPointVO cvo = dataPointDao.getDataPoint(ivp.getKey());
                    TranslatableMessage localTranslatableMessage2 = new TranslatableMessage("dsEdit.meta.generate.noData", new Object[]{cvo.getName()});
                    return localTranslatableMessage2;
                }
                if (from < incep)
                    from = incep;
                dataPointIds.add(Integer.valueOf(ivp.getKey()));
            }

            long to = pointValueDao.getInceptionDate(pointId);
            if (to == -1L) {
                to = System.currentTimeMillis();
            }
            simTimer.fastForwardTo(from);
            try {
                plrt.initialize(simTimer, prt);
                pointValueDao.getPointValuesBetween(dataPointIds, from, to, new MappedRowCallback() {
                    public void row(IdPointValueTime ipvt, int index) {
                        simTimer.fastForwardTo(ipvt.getTime());
                        plrt.pointUpdated(ipvt);
                    }

                    @Override
                    public void row(Object o, int i) {
                    }
                });
                simTimer.fastForwardTo(to);
                plrt.terminate();
            } catch (ScriptException e) {
                TranslatableMessage localTranslatableMessage1 = new TranslatableMessage("dsEdit.meta.generate.scriptError", new Object[]{CompiledScriptExecutor.prettyScriptMessage(e).getMessage()});

                prt.terminateHistorical();
                return localTranslatableMessage1;
            } catch (MetaPointExecutionException e) {
                TranslatableMessage localTranslatableMessage1 = new TranslatableMessage("dsEdit.meta.generate.error", new Object[]{e.getErrorMessage(), Long.valueOf(plrt.getUpdates())});

                prt.terminateHistorical();
                return localTranslatableMessage1;
            }
            if (plrt.getUpdates() > 0L) {
                activeRT = Common.runtimeManager.getDataPoint(pointId);
                if (activeRT != null) {
                    activeRT.resetValues();
                }
            }
            TranslatableMessage activeRT = new TranslatableMessage("dsEdit.meta.generate.success", new Object[]{Long.valueOf(plrt.getUpdates())});
            return activeRT;
        } finally {
            prt.terminateHistorical();
        }
    }
}