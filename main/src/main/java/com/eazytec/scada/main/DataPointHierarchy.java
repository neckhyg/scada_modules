/*    */ package com.eazytec.scada.main;
/*    */ 
/*    */ public class DataPointHierarchy
/*    */ {
/*    */   private int id;
/*    */   private int parentId;
/*    */   private String name;
/*    */ 
/*    */   public int getId()
/*    */   {
/* 10 */     return this.id;
/*    */   }
/*    */ 
/*    */   public void setId(int id) {
/* 14 */     this.id = id;
/*    */   }
/*    */ 
/*    */   public int getParentId() {
/* 18 */     return this.parentId;
/*    */   }
/*    */ 
/*    */   public void setParentId(int parentId) {
/* 22 */     this.parentId = parentId;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 26 */     return this.name;
/*    */   }
/*    */ 
/*    */   public void setName(String name) {
/* 30 */     this.name = name;
/*    */   }
/*    */ }

/* Location:           C:\Documents and Settings\Administrator\桌面\modules\main\lib\main-1.0.0.jar
 * Qualified Name:     com.eazytec.scada.main.DataPointHierarchy
 * JD-Core Version:    0.6.2
 */