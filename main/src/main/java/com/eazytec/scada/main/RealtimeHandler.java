package com.eazytec.scada.main;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.db.dao.SystemSettingsDao;
import com.serotonin.m2m2.rt.dataImage.PointValueFacade;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.vo.DataPointExtendedNameComparator;
import com.serotonin.m2m2.vo.DataPointSummary;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.permission.Permissions;
import com.serotonin.m2m2.web.mvc.UrlHandler;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class RealtimeHandler implements UrlHandler{
    public View handleRequest(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception {

//        long startTime = Long.parseLong(request.getParameter("startTime"));
        User user = Common.getUser(request);

        List<DataPointSummary> allPoints = new DataPointDao().getDataPointSummaries(DataPointExtendedNameComparator.instance);
        List<DataPointSummary> userPoints = new LinkedList<DataPointSummary>();

//        PointValueFacade pointValueFacade = null;
//        Map<Integer,List<PointValueTime>> mapValues = new HashMap<Integer, List<PointValueTime>>();
        //int pointIndex = -1;
        for (DataPointSummary dp : allPoints) {
            if (Permissions.hasDataPointReadPermission(user, dp)) {
                userPoints.add(dp);
//                if (dp.getId() == pointId)
//                    pointIndex = userPoints.size() - 1;
//                pointValueFacade = new PointValueFacade(dp.getId());
//                List<PointValueTime> pointList = pointValueFacade.getPointValues(startTime);

//                mapValues.put(dp.getId(),pointList);
            }
        }
        model.put("userPoints", userPoints);
        model.put("realtimeLimit", SystemSettingsDao.getIntValue("realtimeLimit"));
        return null;
    }
}
