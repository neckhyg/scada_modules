/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.m2m2.module.DwrDefinition;
/*    */ import com.serotonin.m2m2.web.dwr.ModuleDwr;
/*    */ 
/*    */ public class SewageRecordDwrDefinition extends DwrDefinition
/*    */ {
/*    */   public Class<? extends ModuleDwr> getDwrClass()
/*    */   {
/* 10 */     return SewageRecordDwr.class;
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.SewageRecordDwrDefinition
 * JD-Core Version:    0.6.2
 */