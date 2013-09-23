package com.serotonin.m2m2.modbus.dwr;

import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.i18n.Translations;
import com.serotonin.m2m2.web.dwr.beans.TestingUtility;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.NodeScanListener;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.util.ProgressiveTask;
import java.util.LinkedList;
import java.util.List;

public class ModbusNodeScanListener
  implements NodeScanListener, TestingUtility
{
  private final Translations translations;
  private final ModbusMaster modbusMaster;
  private ProgressiveTask task;
  private final List<Integer> nodesFound = new LinkedList();
  private String message = "";

  public ModbusNodeScanListener(Translations translations, ModbusMaster modbusMaster, boolean serial) {
    this.translations = translations;
    this.modbusMaster = modbusMaster;
    try
    {
      modbusMaster.init();
    }
    catch (ModbusInitException e) {
      if (serial) {
        this.message = new TranslatableMessage("MODBUS.scannerSerial.startError", new Object[] { e.getMessage() }).translate(translations);
      }
      else {
        this.message = new TranslatableMessage("MODBUS.scannerIp.startError", new Object[] { e.getMessage() }).translate(translations);
      }
      return;
    }

    this.task = modbusMaster.scanForSlaveNodes(this);
  }

  public List<Integer> getNodesFound() {
    return this.nodesFound;
  }

  public String getMessage() {
    return this.message;
  }

  public boolean isFinished() {
    return this.task == null;
  }

  public synchronized void cancel() {
    if (this.task != null) {
      this.task.cancel();
      try
      {
        wait();
      }
      catch (InterruptedException e)
      {
      }
    }
  }

  private void cleanup() {
    this.modbusMaster.destroy();
    this.task = null;
    notifyAll();
  }

  public void progressUpdate(float progress)
  {
    this.message = new TranslatableMessage("MODBUS.scanner.progress", new Object[] { Integer.toString((int)(progress * 100.0F)) }).translate(this.translations);
  }

  public synchronized void taskCancelled()
  {
    cleanup();
    this.message = this.translations.translate("MODBUS.scanner.cancelled");
  }

  public synchronized void taskCompleted() {
    cleanup();
    this.message = this.translations.translate("MODBUS.scanner.complete");
  }

  public void nodeFound(int nodeNumber) {
    this.nodesFound.add(Integer.valueOf(nodeNumber));
  }
}