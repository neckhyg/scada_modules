/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ public class SewageCompany
/*    */ {
/*  4 */   int id = -1;
/*    */   String address;
/*    */   String name;
/*    */   String telephone;
/*    */   int dataPointId;
/*    */   String dataPointName;
/*    */ 
/*    */   public int getId()
/*    */   {
/* 12 */     return this.id;
/*    */   }
/*    */ 
/*    */   public void setId(int id) {
/* 16 */     this.id = id;
/*    */   }
/*    */ 
/*    */   public String getAddress() {
/* 20 */     return this.address;
/*    */   }
/*    */ 
/*    */   public void setAddress(String address) {
/* 24 */     this.address = address;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 28 */     return this.name;
/*    */   }
/*    */ 
/*    */   public void setName(String name) {
/* 32 */     this.name = name;
/*    */   }
/*    */ 
/*    */   public String getTelephone() {
/* 36 */     return this.telephone;
/*    */   }
/*    */ 
/*    */   public void setTelephone(String telephone) {
/* 40 */     this.telephone = telephone;
/*    */   }
/*    */ 
/*    */   public int getDataPointId() {
/* 44 */     return this.dataPointId;
/*    */   }
/*    */ 
/*    */   public void setDataPointId(int dataPointId) {
/* 48 */     this.dataPointId = dataPointId;
/*    */   }
/*    */ 
/*    */   public String getDataPointName() {
/* 52 */     return this.dataPointName;
/*    */   }
/*    */ 
/*    */   public void setDataPointName(String dataPointName) {
/* 56 */     this.dataPointName = dataPointName;
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.SewageCompany
 * JD-Core Version:    0.6.2
 */