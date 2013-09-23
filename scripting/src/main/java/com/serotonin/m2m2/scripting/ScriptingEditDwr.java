package com.serotonin.m2m2.scripting;

import com.serotonin.db.pair.IntStringPair;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.rt.RuntimeManager;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.IDataPointValueSource;
import com.serotonin.m2m2.rt.script.PointValueSetter;
import com.serotonin.m2m2.rt.script.ScriptError;
import com.serotonin.m2m2.rt.script.ScriptUtils;
import com.serotonin.m2m2.rt.script.WrapperContext;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.vo.dataSource.PointLocatorVO;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import com.serotonin.web.taglib.Functions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

public class ScriptingEditDwr extends DataSourceEditDwr {
    @DwrPermission(user = true)
    public ProcessResult saveDataSource(BasicDataSourceVO basic, List<IntStringPair> context, String script, String cronPattern, int executionDelaySeconds, int logLevel) {
        ScriptDataSourceVO ds = (ScriptDataSourceVO) Common.getUser().getEditDataSource();

        setBasicProps(ds, basic);
        ds.setContext(context);
        ds.setScript(script);
        ds.setCronPattern(cronPattern);
        ds.setExecutionDelaySeconds(executionDelaySeconds);
        ds.setLogLevel(logLevel);

        return tryDataSourceSave(ds);
    }

    @DwrPermission(user = true)
    public ProcessResult savePointLocator(int id, String xid, String name, ScriptPointLocatorVO locator) {
        return validatePoint(id, xid, name, locator, null);
    }

    @DwrPermission(user = true)
    public ProcessResult validateDataSourceScript(List<IntStringPair> context, String script, int logLevel) {
        ProcessResult response = new ProcessResult();

        ScriptDataSourceVO ds = (ScriptDataSourceVO) Common.getUser().getEditDataSource();

        final StringWriter scriptOut = new StringWriter();
        PrintWriter scriptWriter = new PrintWriter(scriptOut);

        ScriptEngine engine = ScriptUtils.newEngine();
        ScriptUtils.prepareEngine(engine);

        ScriptLog scriptLog = new ScriptLog(scriptWriter, logLevel);
        engine.put("LOG", scriptLog);

        WrapperContext wrapperContext = new WrapperContext(System.currentTimeMillis());
        ScriptUtils.wrapperContext(engine, wrapperContext);

        engine.getContext().setWriter(scriptWriter);

        PointValueSetter loggingSetter = new PointValueSetter() {
            public void set(IDataPointValueSource point, Object value, long timestamp) {
                scriptOut.append("Setting point " + ((DataPointRT) point).getVO().getName() + " to " + value + "\r\n");
            }
        };
        for (IntStringPair ivp : context) {
            DataPointRT dprt = Common.runtimeManager.getDataPoint(ivp.getKey());
            if (dprt != null) {
                ScriptUtils.addToContext(engine, ivp.getValue(), dprt, dprt.getVO().getPointLocator().isSettable() ? loggingSetter : null);
            }

        }

        for (DataPointVO dpvo : new DataPointDao().getDataPoints(ds.getId(), null)) {
            DataPointRT dprt = Common.runtimeManager.getDataPoint(dpvo.getId());
            if (dprt != null) {
                ScriptPointLocatorVO locator = (ScriptPointLocatorVO) dpvo.getPointLocator();
                ScriptUtils.addToContext(engine, locator.getVarName(), dprt, loggingSetter);
            }
        }
        try {
            ScriptUtils.executeGlobalScripts(engine);
            ScriptUtils.execute(engine, script, null);
        } catch (ScriptError e) {
            if (e.getColumnNumber() == -1)
                response.addGenericMessage("globalScript.rhinoException", new Object[]{e.getMessage(), Integer.valueOf(e.getLineNumber())});
            else {
                response.addGenericMessage("globalScript.rhinoExceptionCol", new Object[]{e.getMessage(), Integer.valueOf(e.getLineNumber()), Integer.valueOf(e.getColumnNumber())});
            }
        }

        response.addData("out", Functions.lfToBr(Functions.crlfToBr(scriptOut.toString())));

        return response;
    }
}