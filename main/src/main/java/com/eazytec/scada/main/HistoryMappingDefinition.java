/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.m2m2.module.UriMappingDefinition;
/*    */ import com.serotonin.m2m2.module.UriMappingDefinition.Permission;
/*    */ import com.serotonin.m2m2.web.mvc.UrlHandler;
/*    */ 
/*    */ public class HistoryMappingDefinition extends UriMappingDefinition
/*    */ {
/*    */   public UriMappingDefinition.Permission getPermission()
/*    */   {
/*  8 */     return UriMappingDefinition.Permission.USER;
/*    */   }
/*    */ 
/*    */   public String getPath()
/*    */   {
/* 13 */     return "/history.shtm";
/*    */   }
/*    */ 
/*    */   public UrlHandler getHandler() {
/* 17 */     return new HistoryHandler();
/*    */   }
/*    */ 
/*    */   public String getJspPath() {
/* 21 */     return "web/history.jsp";
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.HistoryMappingDefinition
 * JD-Core Version:    0.6.2
 */