package com.eazytec.scada.main;

import com.serotonin.m2m2.view.ShareUser;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.permission.PermissionException;
import com.serotonin.m2m2.web.dwr.longPoll.LongPollData;

import java.util.ArrayList;
import java.util.List;

public class RealtimeListCommon {
    @SuppressWarnings("unchecked")
    public static List<RealtimeListState> getRealtimeListStates(LongPollData data) {
        List<RealtimeListState> realtimeListStates = (List<RealtimeListState>) data.getState().getAttribute("realtimeListStates");
        if (realtimeListStates == null) {
            synchronized (data) {
                realtimeListStates = (List<RealtimeListState>) data.getState().getAttribute("realtimeListStates");
                if (realtimeListStates == null) {
                    realtimeListStates = new ArrayList<RealtimeListState>();
                    data.getState().setAttribute("realtimeListStates", realtimeListStates);
                }
            }
        }
        return realtimeListStates;
    }

    public static void ensureRealtimeListPermission(User user, RealtimeList realtimeList) throws PermissionException {
        if (realtimeList.getUserAccess(user) == ShareUser.ACCESS_NONE)
            throw new PermissionException("User does not have permission to the watch list", user);
    }

    public static void ensureRealtimeListEditPermission(User user, RealtimeList realtimeList) throws PermissionException {
        if (realtimeList.getUserAccess(user) != ShareUser.ACCESS_OWNER)
            throw new PermissionException("User does not have permission to edit the watch list", user);
    }
}
