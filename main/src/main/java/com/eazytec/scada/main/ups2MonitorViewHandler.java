package com.eazytec.scada.main;

import com.serotonin.db.pair.IntStringPair;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.gviews.GraphicalView;
import com.serotonin.m2m2.gviews.GraphicalViewDao;
import com.serotonin.m2m2.gviews.GraphicalViewsCommon;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.web.mvc.UrlHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.View;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-9-23
 * Time: 上午9:34
 * To change this template use File | Settings | File Templates.
 */
public class ups2MonitorViewHandler implements UrlHandler {
    public View handleRequest(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model)
            throws Exception
    {
        GraphicalViewDao viewDao = new GraphicalViewDao();
        User user = Common.getUser(request);

        List views = viewDao.getViewNames(user.getId());
        model.put("views", views);

        GraphicalView currentView = null;
        try
        {
            currentView = viewDao.getViewByXid("ups2Monitor");
        }
        catch (NumberFormatException e)
        {
        }

        if ((currentView == null) && (views.size() > 0)) {
            currentView = viewDao.getView(((IntStringPair)views.get(0)).getKey());
        }
        if (currentView != null) {
            GraphicalViewsCommon.ensureViewPermission(user, currentView);

            currentView.validateViewComponents(false);

            model.put("currentView", currentView);
            model.put("owner", Boolean.valueOf(currentView.getUserAccess(user) == 3));

            GraphicalViewsCommon.setUserView(user, currentView);
        }
        return null;
    }
}
