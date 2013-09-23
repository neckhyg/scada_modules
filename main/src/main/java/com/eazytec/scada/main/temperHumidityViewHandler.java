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
/*    */ public class temperHumidityViewHandler
/*    */   implements UrlHandler
/*    */ {
/*    */   public View handleRequest(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model)
/*    */     throws Exception
/*    */   {
/* 29 */     GraphicalViewDao viewDao = new GraphicalViewDao();
/* 30 */     User user = Common.getUser(request);
/*    */ 
/* 32 */     List views = viewDao.getViewNames(user.getId());
/* 33 */     model.put("views", views);
/*    */ 
/* 36 */     GraphicalView currentView = null;
/*    */     try
/*    */     {
/* 52 */       currentView = viewDao.getViewByXid("temperHumidity");
/*    */     }
/*    */     catch (NumberFormatException e)
/*    */     {
/*    */     }
/*    */ 
/* 58 */     if ((currentView == null) && (views.size() > 0)) {
/* 59 */       currentView = viewDao.getView(((IntStringPair)views.get(0)).getKey());
/*    */     }
/* 61 */     if (currentView != null) {
/* 62 */       GraphicalViewsCommon.ensureViewPermission(user, currentView);
/*    */ 
/* 66 */       currentView.validateViewComponents(false);
/*    */ 
/* 69 */       model.put("currentView", currentView);
/* 70 */       model.put("owner", Boolean.valueOf(currentView.getUserAccess(user) == 3));
/*    */ 
/* 72 */       GraphicalViewsCommon.setUserView(user, currentView);
/*    */     }
/* 74 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.temperHumidityViewHandler
 * JD-Core Version:    0.6.2
 */