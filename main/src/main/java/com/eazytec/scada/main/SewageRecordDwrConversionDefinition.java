/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ import com.serotonin.m2m2.module.DwrConversionDefinition;
/*    */ 
/*    */ public class SewageRecordDwrConversionDefinition extends DwrConversionDefinition
/*    */ {
/*    */   public void addConversions()
/*    */   {
/* 10 */     addConversion(SewageRecord.class);
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.SewageRecordDwrConversionDefinition
 * JD-Core Version:    0.6.2
 */