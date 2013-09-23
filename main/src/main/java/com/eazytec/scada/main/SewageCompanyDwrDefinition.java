/*   */ package com.eazytec.scada.main;
/*   */ 
/*   */ import com.serotonin.m2m2.module.DwrDefinition;
/*   */ import com.serotonin.m2m2.web.dwr.ModuleDwr;
/*   */ 
/*   */ public class SewageCompanyDwrDefinition extends DwrDefinition
/*   */ {
/*   */   public Class<? extends ModuleDwr> getDwrClass()
/*   */   {
/* 8 */     return SewageCompanyDwr.class;
/*   */   }
/*   */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.SewageCompanyDwrDefinition
 * JD-Core Version:    0.6.2
 */