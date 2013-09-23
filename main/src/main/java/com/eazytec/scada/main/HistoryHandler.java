package com.eazytec.scada.main;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.db.dao.SystemSettingsDao;
import com.serotonin.m2m2.vo.DataPointExtendedNameComparator;
import com.serotonin.m2m2.vo.DataPointSummary;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.permission.Permissions;
import com.serotonin.m2m2.web.mvc.UrlHandler;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HistoryHandler implements UrlHandler{
    public View handleRequest(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception {
        User user = Common.getUser(request);

        List<DataPointSummary> allPoints = new DataPointDao().getDataPointSummaries(DataPointExtendedNameComparator.instance);
        List<DataPointSummary> userPoints = new LinkedList<DataPointSummary>();

        for (DataPointSummary dp : allPoints) {
            if (Permissions.hasDataPointReadPermission(user, dp)) {
                userPoints.add(dp);
            }
        }
        model.put("userPoints",userPoints);
        model.put("historyLimit", SystemSettingsDao.getIntValue("historyLimit"));
        return null;
    }
}
