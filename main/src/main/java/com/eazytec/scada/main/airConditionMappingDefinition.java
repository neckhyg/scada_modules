/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.m2m2.module.UriMappingDefinition;
/*    */ import com.serotonin.m2m2.module.UriMappingDefinition.Permission;
/*    */ import com.serotonin.m2m2.web.mvc.UrlHandler;
/*    */ 
/*    */ public class airConditionMappingDefinition extends UriMappingDefinition
/*    */ {
/*    */   public UriMappingDefinition.Permission getPermission()
/*    */   {
/* 16 */     return UriMappingDefinition.Permission.USER;
/*    */   }
/*    */ 
/*    */   public String getPath()
/*    */   {
/* 21 */     return "/airCondition.shtm";
/*    */   }
/*    */ 
/*    */   public UrlHandler getHandler()
/*    */   {
/* 26 */     return new airConditionViewHandler();
/*    */   }
/*    */ 
/*    */   public String getJspPath()
/*    */   {
/* 31 */     return "web/airCondition.jsp";
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.airConditionMappingDefinition
 * JD-Core Version:    0.6.2
 */