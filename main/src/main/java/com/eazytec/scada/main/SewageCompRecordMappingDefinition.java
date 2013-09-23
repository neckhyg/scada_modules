/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.m2m2.module.UriMappingDefinition;
/*    */ import com.serotonin.m2m2.module.UriMappingDefinition.Permission;
/*    */ import com.serotonin.m2m2.web.mvc.UrlHandler;
/*    */ 
/*    */ public class SewageCompRecordMappingDefinition extends UriMappingDefinition
/*    */ {
/*    */   public UriMappingDefinition.Permission getPermission()
/*    */   {
/*  9 */     return UriMappingDefinition.Permission.USER;
/*    */   }
/*    */ 
/*    */   public String getPath()
/*    */   {
/* 14 */     return "/sewage_comp_record.shtm";
/*    */   }
/*    */ 
/*    */   public UrlHandler getHandler()
/*    */   {
/* 19 */     return new SewageCompRecordHandler();
/*    */   }
/*    */ 
/*    */   public String getJspPath()
/*    */   {
/* 24 */     return "web/sewage_comp_record.jsp";
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.SewageCompRecordMappingDefinition
 * JD-Core Version:    0.6.2
 */