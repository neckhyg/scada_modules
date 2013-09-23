package com.serotonin.m2m2.opc;

import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JICurrency;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIUnsignedByte;
import org.jinterop.dcom.core.JIUnsignedInteger;
import org.jinterop.dcom.core.JIUnsignedShort;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.dcom.list.ClassDetails;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.browser.Access;
import org.openscada.opc.lib.da.browser.FlatBrowser;
import org.openscada.opc.lib.list.Categories;
import org.openscada.opc.lib.list.Category;
import org.openscada.opc.lib.list.ServerList;

public class OPCUtils
{
  public static final short QUALITY_BAD = 0;
  public static final short QUALITY_UNCERTAIN = 64;
  public static final short QUALITY_NA = 128;
  public static final short QUALITY_GOOD = 192;
  public static final int BINARY = 1;
  public static final int MULTISTATE = 2;
  public static final int NUMERIC = 3;
  public static final int ALPHANUMERIC = 4;
  public static final int IMAGE = 5;
  public static final int Unknown_Value_Type = 6;
  public static final int BAD = 7;

  public static ArrayList<String> listOPCServers(String user, String password, String host, String domain)
    throws Exception
  {
    ArrayList listNameOPCServers = new ArrayList();

    ServerList serverList = new ServerList(host, user, password, domain);

    Collection<ClassDetails> detailsList = serverList.listServersWithDetails(new Category[] { Categories.OPCDAServer20 }, new Category[] { Categories.OPCDAServer10 });

    for (ClassDetails details : detailsList) {
      listNameOPCServers.add(details.getProgId());
    }

    return listNameOPCServers;
  }

  public static String getClsId(String user, String password, String host, String domain, String progId)
    throws IllegalArgumentException, UnknownHostException, JIException
  {
    ServerList serverListOPC = new ServerList(host, user, password, domain);

    Collection<ClassDetails> detailsList = serverListOPC.listServersWithDetails(new Category[] { Categories.OPCDAServer20 }, new Category[] { Categories.OPCDAServer10 });

    for (ClassDetails classDetails : detailsList) {
      if (progId.equals(classDetails.getProgId())) {
        return classDetails.getClsId();
      }
    }

    return null;
  }

  public static ArrayList<OPCItem> browseOPCTags(String user, String password, String host, String domain, String servername)
    throws Exception
  {
    Server server = null;
    try
    {
      server = createServer(user, password, host, domain, servername);

      String name = String.valueOf(Math.random() * 1000.0D);
      Group group = server.addGroup(name);

      Map mapNameOPCItem = new HashMap();

      ArrayList nameTotalOPCItems = getItems(server);

      ArrayList nameSetOPCItems = getSettableItems(server);

      for (int i = 0; i < nameTotalOPCItems.size(); i++) {
        mapNameOPCItem.put(nameTotalOPCItems.get(i), Boolean.valueOf(false));
      }

      for (int i = 0; i < nameSetOPCItems.size(); i++) {
        mapNameOPCItem.put(nameSetOPCItems.get(i), Boolean.valueOf(true));
      }

      ArrayList listOPCItems = addItems(group, nameTotalOPCItems);

      ArrayList listItemState = readOPCItems(listOPCItems);

      ArrayList listJIVariant = new ArrayList();

      for (int i = 0; i < listItemState.size(); i++) {
        listJIVariant.add(((ItemState)listItemState.get(i)).getValue());
      }

      ArrayList listDataTypeServer = verifyDataType(listJIVariant);

      ArrayList scadaTypes = covertTypeToScada(listDataTypeServer);

      ArrayList listScadaOPCItems = new ArrayList();

      for (int i = 0; i < listOPCItems.size(); i++) {
        OPCItem opcItem = new OPCItem(((Item)listOPCItems.get(i)).getId(), ((Integer)scadaTypes.get(i)).intValue(), ((Boolean)mapNameOPCItem.get(((Item)listOPCItems.get(i)).getId())).booleanValue());

        listScadaOPCItems.add(opcItem);
      }

      server.disconnect();
      server.dispose();

      return listScadaOPCItems;
    }
    catch (Exception e) {
      if (server != null) {
        server.disconnect();
        server.dispose();
      }
        throw e;
    }

  }

  public static String getExceptionMessage(Exception e, Server server)
  {
    String message;
    if (((e instanceof JIException)) && (server != null)) {
      message = String.format("%08X: %s", new Object[] { Integer.valueOf(((JIException)e).getErrorCode()), server.getErrorMessage(((JIException)e).getErrorCode()) });
    }
    else {
      message = e.getMessage();
    }
    return message;
  }

  public OPCItem validateTag(String tag, String user, String password, String host, String domain, String servername)
  {
    OPCItem opcItem = new OPCItem(tag, 0, true);
    try
    {
      Server server = createServer(user, password, host, domain, servername);

      String name = String.valueOf(Math.random() * 1000.0D);
      Group group = server.addGroup(name);

      Item it = group.addItem(tag);

      ItemState is = it.read(true);

      JIVariant ji = is.getValue();

      String opcServerType = verifyDataType(ji);

      int scadaType = covertTypeToScada(opcServerType).intValue();

      opcItem.setDataType(scadaType);
      opcItem.setValidate(true);

      return opcItem;
    } catch (Exception e) {
      e.printStackTrace();
      opcItem.setValidate(false);
      opcItem.setDataType(6);
    }return opcItem;
  }

  public static ArrayList<Item> addItems(Group group, ArrayList<String> nameOPCItems)
    throws JIException, AddFailedException
  {
    ArrayList listOPCItems = new ArrayList();

    for (int i = 0; i < nameOPCItems.size(); i++) {
      try {
        listOPCItems.add(group.addItem((String)nameOPCItems.get(i)));
      }
      catch (Exception e) {
        System.out.println("ERROR: " + e.toString());
      }
    }

    return listOPCItems;
  }

  public static ArrayList<ItemState> readOPCItems(ArrayList<Item> listOPCItems)
    throws JIException
  {
    ArrayList listItemState = new ArrayList();

    for (int i = 0; i < listOPCItems.size(); i++) {
      try {
        listItemState.add(((Item)listOPCItems.get(i)).read(true));
      } catch (Exception e) {
        ItemState is = new ItemState();
        JIVariant ji = new JIVariant("u64/?");
        is.setValue(ji);
        listItemState.add(is);
      }
    }

    return listItemState;
  }

  public static ArrayList<String> getItems(Server server)
    throws IllegalArgumentException, UnknownHostException, JIException
  {
    ArrayList nameOPCItems = new ArrayList();

    FlatBrowser flatBrowser = server.getFlatBrowser();

    if (flatBrowser != null) {
      for (String item : flatBrowser.browse("")) {
        nameOPCItems.add(item);
      }
    }

    return nameOPCItems;
  }

  public static ArrayList<String> getSettableItems(Server server)
    throws IllegalArgumentException, UnknownHostException, JIException
  {
    ArrayList writtableItems = (ArrayList)server.getFlatBrowser().browse(EnumSet.of(Access.WRITE));

    return writtableItems;
  }

  public static ArrayList<String> verifyDataType(ArrayList<JIVariant> listJIVariant)
    throws JIException
  {
    ArrayList typeDataOPCItem = new ArrayList();

    for (int i = 0; i < listJIVariant.size(); i++) {
      Object obj = ((JIVariant)listJIVariant.get(i)).getObject();

      if (obj.getClass().equals(Double.class))
        typeDataOPCItem.add("Double");
      else if (obj.getClass().equals(Float.class))
        typeDataOPCItem.add("Float");
      else if (obj.getClass().equals(Byte.class))
        typeDataOPCItem.add("Byte");
      else if (obj.getClass().equals(Character.class))
        typeDataOPCItem.add("Character");
      else if (obj.getClass().equals(Integer.class))
        typeDataOPCItem.add("Integer");
      else if (obj.getClass().equals(Long.class))
        typeDataOPCItem.add("Long");
      else if (obj.getClass().equals(Boolean.class))
        typeDataOPCItem.add("Boolean");
      else if (obj.getClass().equals(JIUnsignedByte.class))
        typeDataOPCItem.add("JIUnsignedByte");
      else if (obj.getClass().equals(Short.class))
        typeDataOPCItem.add("Short");
      else if (obj.getClass().equals(JIUnsignedShort.class))
        typeDataOPCItem.add("JIUnsignedShort");
      else if (obj.getClass().equals(JIUnsignedInteger.class))
        typeDataOPCItem.add("JIUnsignedInteger");
      else if (obj.getClass().equals(JIString.class))
        typeDataOPCItem.add("JIString");
      else if (obj.getClass().equals(JICurrency.class))
        typeDataOPCItem.add("JICurrency");
      else if (obj.getClass().equals(Date.class))
        typeDataOPCItem.add("Date");
      else {
        typeDataOPCItem.add("Unknown value type");
      }
    }

    return typeDataOPCItem;
  }

  public static String verifyDataType(JIVariant jIVariant)
    throws JIException
  {
    String typeDataOPCItem = "";

    Object obj = jIVariant.getObject();

    if (obj.getClass().equals(Double.class))
      typeDataOPCItem = "Double";
    else if (obj.getClass().equals(Float.class))
      typeDataOPCItem = "Float";
    else if (obj.getClass().equals(Byte.class))
      typeDataOPCItem = "Byte";
    else if (obj.getClass().equals(Character.class))
      typeDataOPCItem = "Character";
    else if (obj.getClass().equals(Integer.class))
      typeDataOPCItem = "Integer";
    else if (obj.getClass().equals(Long.class))
      typeDataOPCItem = "Long";
    else if (obj.getClass().equals(Boolean.class))
      typeDataOPCItem = "Boolean";
    else if (obj.getClass().equals(JIUnsignedByte.class))
      typeDataOPCItem = "JIUnsignedByte";
    else if (obj.getClass().equals(Short.class))
      typeDataOPCItem = "Short";
    else if (obj.getClass().equals(JIUnsignedShort.class))
      typeDataOPCItem = "JIUnsignedShort";
    else if (obj.getClass().equals(JIUnsignedInteger.class))
      typeDataOPCItem = "JIUnsignedInteger";
    else if (obj.getClass().equals(JIString.class))
      typeDataOPCItem = "JIString";
    else if (obj.getClass().equals(JICurrency.class))
      typeDataOPCItem = "JICurrency";
    else if (obj.getClass().equals(Date.class))
      typeDataOPCItem = "Date";
    else {
      typeDataOPCItem = "Unknown value type";
    }

    return typeDataOPCItem;
  }

  public static String qualityToString(short quality)
  {
    switch (quality) {
    case 0:
      return "BAD_QUALITY";
    case 64:
      return "UNCERTAIN_QUALITY";
    case 128:
      return "NA_QUALITY";
    }

    return "BAD_QUALITY";
  }

  public static String getValueOPC(JIVariant jIVariant) throws JIException
  {
    String valueOPCItem = "";

    Object obj = jIVariant.getObject();

    String type = verifyDataType(jIVariant);

    if (type.equals("Double")) {
      valueOPCItem = String.valueOf(((Double)obj).doubleValue());
    } else if (type.equals("Float")) {
      valueOPCItem = String.valueOf(((Float)obj).floatValue());
    } else if (type.equals("Byte")) {
      valueOPCItem = String.valueOf(((Byte)obj).byteValue());
    } else if (type.equals("Character")) {
      Character c = Character.valueOf(((Character)obj).charValue());
      valueOPCItem = String.valueOf(c.toString().getBytes()[0]);
    } else if (type.equals("Integer")) {
      valueOPCItem = String.valueOf(((Integer)obj).intValue());
    } else if (type.equals("Long")) {
      valueOPCItem = String.valueOf(((Long)obj).longValue());
    } else if (type.equals("Boolean")) {
      valueOPCItem = String.valueOf(((Boolean)obj).booleanValue());
    } else if (type.equals("JIUnsignedByte")) {
      valueOPCItem = String.valueOf(((JIUnsignedByte)obj).getValue());
    }
    else if (type.equals("Short")) {
      valueOPCItem = String.valueOf(((Short)obj).shortValue());
    } else if (type.equals("JIUnsignedShort")) {
      valueOPCItem = String.valueOf(((JIUnsignedShort)obj).getValue());
    }
    else if (type.equals("JIUnsignedInteger")) {
      valueOPCItem = String.valueOf(((JIUnsignedInteger)obj).getValue());
    }
    else if (type.equals("JIString")) {
      valueOPCItem = jIVariant.getObjectAsString2();
    } else if (type.equals("JICurrency")) {
      valueOPCItem = String.valueOf(((JICurrency)obj).getUnits());
    }
    else if (type.equals("Date")) {
      Date date = new Date(((Date)obj).getTime());
      valueOPCItem = date.toString();
    } else {
      valueOPCItem = "Unknown value type";
    }

    return valueOPCItem;
  }

  public static ArrayList<Integer> covertTypeToScada(ArrayList<String> typeDataOPCItem)
  {
    ArrayList typeDataScada = new ArrayList();

    for (int i = 0; i < typeDataOPCItem.size(); i++) {
      if ((((String)typeDataOPCItem.get(i)).equals("Double")) || (((String)typeDataOPCItem.get(i)).equals("Float")) || (((String)typeDataOPCItem.get(i)).equals("Integer")) || (((String)typeDataOPCItem.get(i)).equals("Long")) || (((String)typeDataOPCItem.get(i)).equals("JIUnsignedByte")) || (((String)typeDataOPCItem.get(i)).equals("Byte")) || (((String)typeDataOPCItem.get(i)).equals("JIUnsignedShort")) || (((String)typeDataOPCItem.get(i)).equals("JIUnsignedInteger")) || (((String)typeDataOPCItem.get(i)).equals("JICurrency")) || (((String)typeDataOPCItem.get(i)).equals("Short")))
      {
        typeDataScada.add(Integer.valueOf(3));
      } else if (((String)typeDataOPCItem.get(i)).equals("Boolean"))
        typeDataScada.add(Integer.valueOf(1));
      else if ((((String)typeDataOPCItem.get(i)).equals("Date")) || (((String)typeDataOPCItem.get(i)).equals("Character")) || (((String)typeDataOPCItem.get(i)).equals("JIString")))
      {
        typeDataScada.add(Integer.valueOf(4));
      } else if (((String)typeDataOPCItem.get(i)).equals("Unknown value type"))
        typeDataScada.add(Integer.valueOf(6));
      else {
        typeDataScada.add(Integer.valueOf(7));
      }
    }

    return typeDataScada;
  }

  public Integer covertTypeToScada(String typeDataOPCItem)
  {
    int typeDataScada = 0;

    if ((typeDataOPCItem.equals("Double")) || (typeDataOPCItem.equals("Float")) || (typeDataOPCItem.equals("Integer")) || (typeDataOPCItem.equals("Long")) || (typeDataOPCItem.equals("JIUnsignedByte")) || (typeDataOPCItem.equals("Byte")) || (typeDataOPCItem.equals("JIUnsignedShort")) || (typeDataOPCItem.equals("JIUnsignedInteger")) || (typeDataOPCItem.equals("JICurrency")) || (typeDataOPCItem.equals("Short")))
    {
      typeDataScada = 3;
    } else if (typeDataOPCItem.equals("Boolean"))
      typeDataScada = 1;
    else if ((typeDataOPCItem.equals("Date")) || (typeDataOPCItem.equals("Character")) || (typeDataOPCItem.equals("JIString")))
    {
      typeDataScada = 4;
    } else if (typeDataOPCItem.equals("Unknown value type"))
      typeDataScada = 6;
    else {
      typeDataScada = 7;
    }

    return Integer.valueOf(typeDataScada);
  }

  public static Server createServer(String user, String password, String host, String domain, String servername)
    throws IllegalArgumentException, UnknownHostException, JIException, AlreadyConnectedException
  {
    Server serverOPC = null;

    String clsid = getClsId(user, password, host, domain, servername);

    ConnectionInformation ci = new ConnectionInformation();
    ci.setHost(host);
    ci.setDomain(domain);
    ci.setUser(user);
    ci.setPassword(password);
    ci.setClsid(clsid);

    serverOPC = new Server(ci, Executors.newSingleThreadScheduledExecutor());
    serverOPC.connect();

    return serverOPC;
  }
}