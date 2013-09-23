/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.m2m2.module.UriMappingDefinition;
/*    */ import com.serotonin.m2m2.module.UriMappingDefinition.Permission;
/*    */ import com.serotonin.m2m2.watchlist.WatchListHandler;
/*    */ import com.serotonin.m2m2.web.mvc.UrlHandler;
/*    */ 
/*    */ public class MonitoringDataMappingDefinition extends UriMappingDefinition
/*    */ {
/*    */   public UriMappingDefinition.Permission getPermission()
/*    */   {
/* 10 */     return UriMappingDefinition.Permission.USER;
/*    */   }
/*    */ 
/*    */   public String getPath()
/*    */   {
/* 15 */     return "/monitoring_data.shtm";
/*    */   }
/*    */ 
/*    */   public UrlHandler getHandler()
/*    */   {
/* 20 */     return new WatchListHandler();
/*    */   }
/*    */ 
/*    */   public String getJspPath()
/*    */   {
/* 25 */     return "web/monitoring_data.jsp";
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.MonitoringDataMappingDefinition
 * JD-Core Version:    0.6.2
 */