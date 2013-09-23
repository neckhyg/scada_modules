package com.serotonin.m2m2.pop3;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.DataSourceUtils;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import com.serotonin.m2m2.web.taglib.Functions;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class Pop3EditDwr extends DataSourceEditDwr {
    @DwrPermission(user = true)
    public ProcessResult savePop3DataSource(BasicDataSourceVO basic, int updatePeriods, int updatePeriodType, String pop3Server, String username, String password) {
        Pop3DataSourceVO ds = (Pop3DataSourceVO) Common.getUser().getEditDataSource();

        setBasicProps(ds, basic);
        ds.setUpdatePeriods(updatePeriods);
        ds.setUpdatePeriodType(updatePeriodType);
        ds.setPop3Server(pop3Server);
        ds.setUsername(username);
        ds.setPassword(password);

        return tryDataSourceSave(ds);
    }

    @DwrPermission(user = true)
    public ProcessResult savePop3PointLocator(int id, String xid, String name, Pop3PointLocatorVO locator) {
        return validatePoint(id, xid, name, locator, null);
    }

    @DwrPermission(user = true)
    public String testPop3ValueParams(String testData, String valueRegex, int dataTypeId, String valueFormat) {
        try {
            Pattern valuePattern = Pattern.compile(valueRegex);
            DecimalFormat decimalFormat = null;
            if ((dataTypeId == 3) && (!StringUtils.isBlank(valueFormat)))
                decimalFormat = new DecimalFormat(valueFormat);
            DataValue value = DataSourceUtils.getValue(valuePattern, testData, dataTypeId, valueFormat, null, decimalFormat, null);

            return translate("common.result", new Object[0]) + ": " + value.toString();
        } catch (TranslatableException e) {
            return translate(e.getTranslatableMessage());
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @DwrPermission(user = true)
    public String testPop3TimeParams(String testData, String timeRegex, String timeFormat) {
        try {
            Pattern timePattern = Pattern.compile(timeRegex);
            DateFormat dateFormat = new SimpleDateFormat(timeFormat);
            long time = DataSourceUtils.getValueTime(System.currentTimeMillis(), timePattern, testData, dateFormat, null);

            return Functions.getTime(time);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}