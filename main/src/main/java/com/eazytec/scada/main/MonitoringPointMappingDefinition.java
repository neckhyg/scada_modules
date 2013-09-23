/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.m2m2.gviews.GraphicalViewHandler;
/*    */ import com.serotonin.m2m2.module.UriMappingDefinition;
/*    */ import com.serotonin.m2m2.module.UriMappingDefinition.Permission;
/*    */ import com.serotonin.m2m2.web.mvc.UrlHandler;
/*    */ 
/*    */ public class MonitoringPointMappingDefinition extends UriMappingDefinition
/*    */ {
/*    */   public UriMappingDefinition.Permission getPermission()
/*    */   {
/* 10 */     return UriMappingDefinition.Permission.USER;
/*    */   }
/*    */ 
/*    */   public String getPath()
/*    */   {
/* 15 */     return "/monitoring_point.shtm";
/*    */   }
/*    */ 
/*    */   public UrlHandler getHandler()
/*    */   {
/* 21 */     return new GraphicalViewHandler();
/*    */   }
/*    */ 
/*    */   public String getJspPath()
/*    */   {
/* 26 */     return "web/monitoring_point.jsp";
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.MonitoringPointMappingDefinition
 * JD-Core Version:    0.6.2
 */