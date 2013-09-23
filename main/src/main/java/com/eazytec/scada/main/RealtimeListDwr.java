package com.eazytec.scada.main;

import com.serotonin.db.pair.IntStringPair;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.db.dao.UserDao;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.RuntimeManager;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.types.ImageValue;
import com.serotonin.m2m2.view.ShareUser;
import com.serotonin.m2m2.vo.DataPointExtendedNameComparator;
import com.serotonin.m2m2.vo.DataPointSummary;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.hierarchy.PointHierarchy;
import com.serotonin.m2m2.vo.permission.Permissions;
import com.serotonin.m2m2.web.dwr.ModuleDwr;
import com.serotonin.m2m2.web.dwr.beans.DataExportDefinition;
import com.serotonin.m2m2.web.dwr.longPoll.LongPollData;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import com.serotonin.m2m2.web.taglib.Functions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.directwebremoting.WebContextFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealtimeListDwr extends ModuleDwr{
    @DwrPermission(user = true)
    public Map<String, Object> init() {
        DataPointDao dataPointDao = new DataPointDao();
        Map<String, Object> data = new HashMap<String, Object>();

        PointHierarchy ph = dataPointDao.getPointHierarchy(true).copyFoldersOnly();
        User user = Common.getUser();
        List<DataPointVO> points = dataPointDao.getDataPoints(DataPointExtendedNameComparator.instance, false);
        for (DataPointVO point : points) {
            if (Permissions.hasDataPointReadPermission(user, point))
                ph.addDataPoint(point.getPointFolderId(), new DataPointSummary(point));
        }

        ph.parseEmptyFolders();

        RealtimeList realtimeList = new RealtimeListDao().getSelectedRealtimeList(user.getId());
        prepareRealtimeList(realtimeList, user);
        setRealtimeList(user, realtimeList);

        data.put("pointFolder", ph.getRoot());
        data.put("shareUsers", getShareUsers(user));
        data.put("selectedRealtimeList", getRealtimeListData(user, realtimeList));

        return data;
    }

    /**
     * Retrieves point state for all points on the current watch list.
     *
     * @return
     */
    public List<RealtimeListState> getPointData() {
        // Get the watch list from the user's session. It should have been set by the controller.
        return getPointDataImpl(getRealtimeList());
    }

    private List<RealtimeListState> getPointDataImpl(RealtimeList realtimeList) {
        if (realtimeList == null)
            return new ArrayList<RealtimeListState>();

        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        User user = Common.getUser(request);

        RealtimeListState state;
        List<RealtimeListState> states = new ArrayList<RealtimeListState>(realtimeList.getPointList().size());
        Map<String, Object> model = new HashMap<String, Object>();
        for (DataPointVO point : realtimeList.getPointList()) {
            // Create the watch list state.
            state = createRealtimeListState(request, point, Common.runtimeManager, model, user);
            states.add(state);
        }

        return states;
    }

    @DwrPermission(user = true)
    public void updateRealtimeListName(String name) {
        User user = Common.getUser();
        RealtimeList realtimeList = getRealtimeList(user);
        RealtimeListCommon.ensureRealtimeListEditPermission(user, realtimeList);
        realtimeList.setName(name);
        new RealtimeListDao().saveRealtimeList(realtimeList);
    }

    @DwrPermission(user = true)
    public IntStringPair addNewRealtimeList(int copyId) {
        User user = Common.getUser();

        RealtimeListDao realtimeListDao = new RealtimeListDao();
        RealtimeList realtimeList;

        if (copyId == Common.NEW_ID) {
            realtimeList = new RealtimeList();
            realtimeList.setName(translate("common.newName"));
        }
        else {
            realtimeList = new RealtimeListDao().getRealtimeList(getRealtimeList().getId());
            realtimeList.setId(Common.NEW_ID);
            realtimeList.setName(translate(new TranslatableMessage("common.copyPrefix", realtimeList.getName())));
        }
        realtimeList.setUserId(user.getId());
        realtimeList.setXid(realtimeListDao.generateUniqueXid());

        realtimeListDao.saveRealtimeList(realtimeList);

        setRealtimeList(user, realtimeList);
        realtimeListDao.saveSelectedRealtimeList(user.getId(), realtimeList.getId());

        return new IntStringPair(realtimeList.getId(), realtimeList.getName());
    }

    @DwrPermission(user = true)
    public void deleteRealtimeList(int realtimeListId) {
        User user = Common.getUser();

        RealtimeListDao realtimeListDao = new RealtimeListDao();
        RealtimeList realtimeList = getRealtimeList(user);
        if (realtimeList == null || realtimeListId != realtimeList.getId())
            realtimeList = realtimeListDao.getRealtimeList(realtimeListId);

        if (realtimeList == null || realtimeListDao.getRealtimeLists(user.getId()).size() == 1)
            // Only one watch list left. Leave it.
            return;

        // Allow the delete.
        if (realtimeList.getUserAccess(user) == ShareUser.ACCESS_OWNER)
            realtimeListDao.deleteRealtimeList(realtimeListId);
        else
            realtimeListDao.removeUserFromRealtimeList(realtimeListId, user.getId());
    }

    @DwrPermission(user = true)
    public Map<String, Object> setSelectedRealtimeList(int realtimeListId) {
        User user = Common.getUser();

        RealtimeListDao realtimeListDao = new RealtimeListDao();
        RealtimeList realtimeList = realtimeListDao.getRealtimeList(realtimeListId);
        RealtimeListCommon.ensureRealtimeListPermission(user, realtimeList);
        prepareRealtimeList(realtimeList, user);

        realtimeListDao.saveSelectedRealtimeList(user.getId(), realtimeList.getId());

        Map<String, Object> data = getRealtimeListData(user, realtimeList);
        // Set the watchlist in the user object after getting the data since it may take a while, and the long poll
        // updates will all be missed in the meantime.
        setRealtimeList(user, realtimeList);

        return data;
    }

    @DwrPermission(user = true)
    public RealtimeListState addToRealtimeList(int pointId) {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        User user = Common.getUser();
        DataPointVO point = new DataPointDao().getDataPoint(pointId);
        if (point == null)
            return null;
        RealtimeList realtimeList = getRealtimeList(user);

        // Check permissions.
        Permissions.ensureDataPointReadPermission(user, point);
        RealtimeListCommon.ensureRealtimeListEditPermission(user, realtimeList);

        // Add it to the watch list.
        realtimeList.getPointList().add(point);
        new RealtimeListDao().saveRealtimeList(realtimeList);
        updateSetPermission(point, realtimeList.getUserAccess(user), new UserDao().getUser(realtimeList.getUserId()));

        // Return the watch list state for it.
        return createRealtimeListState(request, point, Common.runtimeManager, new HashMap<String, Object>(), user);
    }

    @DwrPermission(user = true)
    public void removeFromRealtimeList(int pointId) {
        // Remove the point from the user's list.
        User user = Common.getUser();
        RealtimeList realtimeList = getRealtimeList(user);
        RealtimeListCommon.ensureRealtimeListEditPermission(user, realtimeList);
        for (DataPointVO point : realtimeList.getPointList()) {
            if (point.getId() == pointId) {
                realtimeList.getPointList().remove(point);
                break;
            }
        }
        new RealtimeListDao().saveRealtimeList(realtimeList);
    }

    @DwrPermission(user = true)
    public void moveUp(int pointId) {
        User user = Common.getUser();
        RealtimeList realtimeList = getRealtimeList(user);
        RealtimeListCommon.ensureRealtimeListEditPermission(user, realtimeList);
        List<DataPointVO> points = realtimeList.getPointList();

        DataPointVO point;
        for (int i = 0; i < points.size(); i++) {
            point = points.get(i);
            if (point.getId() == pointId) {
                points.set(i, points.get(i - 1));
                points.set(i - 1, point);
                break;
            }
        }

        new RealtimeListDao().saveRealtimeList(realtimeList);
    }

    @DwrPermission(user = true)
    public void moveDown(int pointId) {
        User user = Common.getUser();
        RealtimeList realtimeList = getRealtimeList(user);
        RealtimeListCommon.ensureRealtimeListEditPermission(user, realtimeList);
        List<DataPointVO> points = realtimeList.getPointList();

        DataPointVO point;
        for (int i = 0; i < points.size(); i++) {
            point = points.get(i);
            if (point.getId() == pointId) {
                points.set(i, points.get(i + 1));
                points.set(i + 1, point);
                break;
            }
        }

        new RealtimeListDao().saveRealtimeList(realtimeList);
    }

    /**
     * Convenience method for creating a populated view state.
     */
    private RealtimeListState createRealtimeListState(HttpServletRequest request, DataPointVO pointVO, RuntimeManager rtm,
                                                Map<String, Object> model, User user) {
        // Get the data point status from the data image.
        DataPointRT point = rtm.getDataPoint(pointVO.getId());

        RealtimeListState state = new RealtimeListState();
        state.setId(Integer.toString(pointVO.getId()));

        PointValueTime pointValue = prepareBasePointState(Integer.toString(pointVO.getId()), state, pointVO, point,
                model);
        setEvents(pointVO, user, model);
        if (pointValue != null && pointValue.getValue() instanceof ImageValue) {
            // Text renderers don't help here. Create a thumbnail.
            setImageText(request, state, pointVO, model, pointValue);
        }
        else
            setPrettyText(state, pointVO, model, pointValue);

        if (pointVO.isSettable())
            setChange(pointVO, state, point, request, model, user);

        if (state.getValue() != null)
            setChart(pointVO, state, request, model);
        setMessages(state, request, getModule().getWebPath() + "/web/snippet/realtimeListMessages.jsp", model);

        return state;
    }

    private static void setImageText(HttpServletRequest request, RealtimeListState state, DataPointVO pointVO,
                                     Map<String, Object> model, PointValueTime pointValue) {
        if (!ObjectUtils.equals(pointVO.lastValue(), pointValue)) {
            state.setValue(generateContent(request, "imageValueThumbnail.jsp", model));
            if (pointValue != null)
                state.setTime(Functions.getTime(pointValue));
            pointVO.updateLastValue(pointValue);
        }
    }

    /**
     * Method for creating image charts of the points on the watch list.
     */
    @DwrPermission(user = true)
    public String getImageChartData(int[] pointIds, int fromYear, int fromMonth, int fromDay, int fromHour,
                                    int fromMinute, int fromSecond, boolean fromNone, int toYear, int toMonth, int toDay, int toHour,
                                    int toMinute, int toSecond, boolean toNone, int width, int height) {
        DateTimeZone dtz = Common.getUser().getDateTimeZoneInstance();
        DateTime from = createDateTime(fromYear, fromMonth, fromDay, fromHour, fromMinute, fromSecond, fromNone, dtz);
        DateTime to = createDateTime(toYear, toMonth, toDay, toHour, toMinute, toSecond, toNone, dtz);

        StringBuilder htmlData = new StringBuilder();
        htmlData.append("<img src=\"achart/ft_");
        htmlData.append(System.currentTimeMillis());
        htmlData.append('_');
        htmlData.append(fromNone ? -1 : from.getMillis());
        htmlData.append('_');
        htmlData.append(toNone ? -1 : to.getMillis());

        boolean pointsFound = false;
        // Add the list of points that are numeric.
        List<DataPointVO> realtimeList = getRealtimeList().getPointList();
        for (DataPointVO dp : realtimeList) {
            int dtid = dp.getPointLocator().getDataTypeId();
            if ((dtid == DataTypes.NUMERIC || dtid == DataTypes.BINARY || dtid == DataTypes.MULTISTATE)
                    && ArrayUtils.contains(pointIds, dp.getId())) {
                pointsFound = true;
                htmlData.append('_');
                htmlData.append(dp.getId());
            }
        }

        if (!pointsFound)
            // There are no chartable points, so abort the image creation.
            return translate("watchlist.noChartables");

        htmlData.append(".png?w=");
        htmlData.append(width);
        htmlData.append("&h=");
        htmlData.append(height);
        htmlData.append("\" alt=\"" + translate("common.imageChart") + "\"/>");

        return htmlData.toString();
    }

    private Map<String, Object> getRealtimeListData(User user, RealtimeList realtimeList) {
        Map<String, Object> data = new HashMap<String, Object>();
        if (realtimeList == null)
            return data;

        List<DataPointVO> points = realtimeList.getPointList();
        List<Integer> pointIds = new ArrayList<Integer>(points.size());
        for (DataPointVO point : points) {
            if (Permissions.hasDataPointReadPermission(user, point))
                pointIds.add(point.getId());
        }

        data.put("points", pointIds);
        data.put("users", realtimeList.getRealtimeListUsers());
        data.put("access", realtimeList.getUserAccess(user));

        return data;
    }

    private void prepareRealtimeList(RealtimeList realtimeList, User user) {
        int access = realtimeList.getUserAccess(user);
        User owner = new UserDao().getUser(realtimeList.getUserId());
        for (DataPointVO point : realtimeList.getPointList())
            updateSetPermission(point, access, owner);
    }

    private void updateSetPermission(DataPointVO point, int access, User owner) {
        // Point isn't settable
        if (!point.getPointLocator().isSettable())
            return;

        // Read-only access
        if (access != ShareUser.ACCESS_OWNER && access != ShareUser.ACCESS_SET)
            return;

        // Watch list owner doesn't have set permission
        if (!Permissions.hasDataPointSetPermission(owner, point))
            return;

        // All good.
        point.setSettable(true);
    }

    private static void setPrettyText(RealtimeListState state, DataPointVO pointVO, Map<String, Object> model,
                                      PointValueTime pointValue) {
        String prettyText = Functions.getHtmlText(pointVO, pointValue);
        model.put("text", prettyText);
        if (!ObjectUtils.equals(pointVO.lastValue(), pointValue)) {
            state.setValue(prettyText);
            if (pointValue != null)
                state.setTime(Functions.getTime(pointValue));
            pointVO.updateLastValue(pointValue);
        }
    }

    //
    // Share users
    //
    @DwrPermission(user = true)
    public List<ShareUser> addUpdateSharedUser(int userId, int accessType) {
        RealtimeList realtimeList = getRealtimeList();
        boolean found = false;
        for (ShareUser su : realtimeList.getRealtimeListUsers()) {
            if (su.getUserId() == userId) {
                found = true;
                su.setAccessType(accessType);
                break;
            }
        }

        if (!found) {
            ShareUser su = new ShareUser();
            su.setUserId(userId);
            su.setAccessType(accessType);
            realtimeList.getRealtimeListUsers().add(su);
        }

        new RealtimeListDao().saveRealtimeList(realtimeList);

        return realtimeList.getRealtimeListUsers();
    }

    @DwrPermission(user = true)
    public List<ShareUser> removeSharedUser(int userId) {
        RealtimeList realtimeList = getRealtimeList();

        for (ShareUser su : realtimeList.getRealtimeListUsers()) {
            if (su.getUserId() == userId) {
                realtimeList.getRealtimeListUsers().remove(su);
                break;
            }
        }

        new RealtimeListDao().saveRealtimeList(realtimeList);

        return realtimeList.getRealtimeListUsers();
    }

    private void setRealtimeList(User user, RealtimeList realtimeList) {
        user.setAttribute("realtimeList", realtimeList);
    }

    private static RealtimeList getRealtimeList() {
        return getRealtimeList(Common.getUser());
    }

    private static RealtimeList getRealtimeList(User user) {
        return user.getAttribute("realtimeList", RealtimeList.class);
    }

    @DwrPermission(anonymous = true)
    public void resetRealtimeListState(int pollSessionId) {
        LongPollData data = getLongPollData(pollSessionId, false);

        synchronized (data.getState()) {
            RealtimeListCommon.getRealtimeListStates(data).clear();
            RealtimeList wl = getRealtimeList();
            for (DataPointVO dp : wl.getPointList())
                dp.resetLastValue();
        }
        notifyLongPollImpl(data.getRequest());
    }

    @DwrPermission(user = true)
    public void getChartData(int[] pointIds, int fromYear, int fromMonth, int fromDay, int fromHour, int fromMinute,
                             int fromSecond, boolean fromNone, int toYear, int toMonth, int toDay, int toHour, int toMinute,
                             int toSecond, boolean toNone) {
        User user = Common.getUser();
        DateTimeZone dtz = user.getDateTimeZoneInstance();
        DateTime from = createDateTime(fromYear, fromMonth, fromDay, fromHour, fromMinute, fromSecond, fromNone, dtz);
        DateTime to = createDateTime(toYear, toMonth, toDay, toHour, toMinute, toSecond, toNone, dtz);
        DataExportDefinition def = new DataExportDefinition(pointIds, from, to);
        user.setDataExportDefinition(def);
    }

}
