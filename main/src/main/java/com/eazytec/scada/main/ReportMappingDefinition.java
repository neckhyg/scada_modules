/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.m2m2.module.UriMappingDefinition;
/*    */ import com.serotonin.m2m2.module.UriMappingDefinition.Permission;
/*    */ import com.serotonin.m2m2.web.mvc.UrlHandler;
/*    */ 
/*    */ public class ReportMappingDefinition extends UriMappingDefinition
/*    */ {
/*    */   public UriMappingDefinition.Permission getPermission()
/*    */   {
/*  9 */     return UriMappingDefinition.Permission.USER;
/*    */   }
/*    */ 
/*    */   public String getPath()
/*    */   {
/* 14 */     return "/report.shtm";
/*    */   }
/*    */ 
/*    */   public UrlHandler getHandler()
/*    */   {
/* 19 */     return null;
/*    */   }
/*    */ 
/*    */   public String getJspPath()
/*    */   {
/* 24 */     return "web/report.jsp";
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.ReportMappingDefinition
 * JD-Core Version:    0.6.2
 */