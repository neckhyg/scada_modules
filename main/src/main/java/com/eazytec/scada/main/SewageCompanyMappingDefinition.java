/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.m2m2.module.UriMappingDefinition;
/*    */ import com.serotonin.m2m2.module.UriMappingDefinition.Permission;
/*    */ import com.serotonin.m2m2.web.mvc.UrlHandler;
/*    */ 
/*    */ public class SewageCompanyMappingDefinition extends UriMappingDefinition
/*    */ {
/*    */   public UriMappingDefinition.Permission getPermission()
/*    */   {
/*  8 */     return null;
/*    */   }
/*    */ 
/*    */   public String getPath() {
/* 12 */     return "/sewage_comp_edit.shtm";
/*    */   }
/*    */ 
/*    */   public UrlHandler getHandler() {
/* 16 */     return new SewageCompanyHandler();
/*    */   }
/*    */ 
/*    */   public String getJspPath() {
/* 20 */     return "web/sewage_comp_edit.jsp";
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.SewageCompanyMappingDefinition
 * JD-Core Version:    0.6.2
 */