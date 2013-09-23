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
/*    */ public class leakGraphicalViewHandler
/*    */   implements UrlHandler
/*    */ {
/*    */   public View handleRequest(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model)
/*    */     throws Exception
/*    */   {
/* 30 */     GraphicalViewDao viewDao = new GraphicalViewDao();
/* 31 */     User user = Common.getUser(request);
/*    */ 
/* 33 */     List views = viewDao.getViewNames(user.getId());
/* 34 */     model.put("views", views);
/*    */ 
/* 37 */     GraphicalView currentView = null;
/*    */     try
/*    */     {
/* 53 */       currentView = viewDao.getViewByXid("leak");
/*    */     }
/*    */     catch (NumberFormatException e)
/*    */     {
/*    */     }
/*    */ 
/* 59 */     if ((currentView == null) && (views.size() > 0)) {
/* 60 */       currentView = viewDao.getView(((IntStringPair)views.get(0)).getKey());
/*    */     }
/* 62 */     if (currentView != null) {
/* 63 */       GraphicalViewsCommon.ensureViewPermission(user, currentView);
/*    */ 
/* 67 */       currentView.validateViewComponents(false);
/*    */ 
/* 70 */       model.put("currentView", currentView);
/* 71 */       model.put("owner", Boolean.valueOf(currentView.getUserAccess(user) == 3));
/*    */ 
/* 73 */       GraphicalViewsCommon.setUserView(user, currentView);
/*    */     }
/* 75 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.leakGraphicalViewHandler
 * JD-Core Version:    0.6.2
 */