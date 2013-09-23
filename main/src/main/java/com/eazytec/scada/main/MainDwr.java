/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.m2m2.web.dwr.ModuleDwr;
/*    */ import com.serotonin.m2m2.web.dwr.util.DwrPermission;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class MainDwr extends ModuleDwr
/*    */ {
/*    */   @DwrPermission(user=true)
/*    */   public Map<String, Object> init()
/*    */   {
/* 12 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.MainDwr
 * JD-Core Version:    0.6.2
 */