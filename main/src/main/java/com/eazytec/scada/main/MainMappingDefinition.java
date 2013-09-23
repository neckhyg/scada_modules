/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.m2m2.module.UrlMappingDefinition;
/*    */ import com.serotonin.m2m2.module.UrlMappingDefinition.Permission;
/*    */ import com.serotonin.m2m2.web.mvc.UrlHandler;
/*    */ 
/*    */ public class MainMappingDefinition extends UrlMappingDefinition
/*    */ {
/*    */   public String getUrlPath()
/*    */   {
/*  9 */     return "/main.shtm";
/*    */   }
/*    */ 
/*    */   public UrlHandler getHandler()
/*    */   {
/* 14 */     return new MainHandler();
/*    */   }
/*    */ 
/*    */   public String getJspPath()
/*    */   {
/* 19 */     return "web/main.jsp";
/*    */   }
/*    */ 
/*    */   public String getMenuKey()
/*    */   {
/* 24 */     return "header.main";
/*    */   }
/*    */ 
/*    */   public String getMenuImage()
/*    */   {
/* 29 */     return "web/eazytec.png";
/*    */   }
/*    */ 
/*    */   public UrlMappingDefinition.Permission getPermission()
/*    */   {
/* 34 */     return UrlMappingDefinition.Permission.USER;
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.MainMappingDefinition
 * JD-Core Version:    0.6.2
 */