/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.db.pair.IntStringPair;
/*    */ import com.serotonin.m2m2.Common;
/*    */ import com.serotonin.m2m2.gviews.GraphicalView;
/*    */ import com.serotonin.m2m2.gviews.GraphicalViewDao;
/*    */ import com.serotonin.m2m2.gviews.GraphicalViewsCommon;
/*    */ import com.serotonin.m2m2.vo.User;
/*    */ import com.serotonin.m2m2.web.mvc.UrlHandler;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import org.springframework.web.servlet.View;
/*    */ 
/*    */ public class upsMonitorViewHandler
/*    */   implements UrlHandler
/*    */ {
/*    */   public View handleRequest(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model)
/*    */     throws Exception
/*    */   {
/* 28 */     GraphicalViewDao viewDao = new GraphicalViewDao();
/* 29 */     User user = Common.getUser(request);
/*    */ 
/* 31 */     List views = viewDao.getViewNames(user.getId());
/* 32 */     model.put("views", views);
/*    */ 
/* 35 */     GraphicalView currentView = null;
/*    */     try
/*    */     {
/* 38 */       currentView = viewDao.getViewByXid("upsMonitor");
/*    */     }
/*    */     catch (NumberFormatException e)
/*    */     {
/*    */     }
/*    */ 
/* 44 */     if ((currentView == null) && (views.size() > 0)) {
/* 45 */       currentView = viewDao.getView(((IntStringPair)views.get(0)).getKey());
/*    */     }
/* 47 */     if (currentView != null) {
/* 48 */       GraphicalViewsCommon.ensureViewPermission(user, currentView);
/*    */ 
/* 52 */       currentView.validateViewComponents(false);
/*    */ 
/* 55 */       model.put("currentView", currentView);
/* 56 */       model.put("owner", Boolean.valueOf(currentView.getUserAccess(user) == 3));
/*    */ 
/* 58 */       GraphicalViewsCommon.setUserView(user, currentView);
/*    */     }
/* 60 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.upsMonitorViewHandler
 * JD-Core Version:    0.6.2
 */