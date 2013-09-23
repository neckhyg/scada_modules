package com.serotonin.m2m2.persistent.common;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.io.StreamUtils;
import com.serotonin.json.*;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.i18n.TranslatableMessageParseException;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.queue.ByteQueue;
import java.io.*;
import java.nio.charset.Charset;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Packet
{
    private static final Log LOG = LogFactory.getLog(Packet.class);
    public static final int PUBLISHER_SOCKET_TIMEOUT = 30000;
    public static final int DATA_SOURCE_SOCKET_TIMEOUT = 5000;
    public static final int TEST_PACKET_SEND_DELAY = 60000;
    private static final int PAYLOAD_READ_TIMEOUT = 60000;
    private static final byte PAYLOAD_READ_BUFFER[] = new byte[1024];
    private static final Charset CHARSET = Charset.forName("UTF-8");
    public static final byte EMPTY[] = new byte[0];
    public static final byte TRUE[] = {0};
    public static final byte FALSE[] = { 1 };
    private final PacketType type;
    private final ByteQueue payload;

    public static Packet readPacketNoBlock(InputStream in, int version)
        throws IOException, PersistentAbortException, PersistentProtocolException
    {
        if(in.available() > 0)
            return readPacket(in, version);
        else
            return null;
    }

    public static Packet readPacket(InputStream in, int version)
        throws IOException, PersistentAbortException, PersistentProtocolException
    {
        Packet packet = null;
        PacketType packetType;
        int length;
        if(version == 1)
        {
            length = StreamUtils.read4ByteSigned(in);
            packetType = PacketType.getPacketType(StreamUtils.readByte(in));
        } else
        if(version >= 2)
        {
            int i;
            do
            {
                i = in.read();
                if(i == -1)
                    throw new IOException("EOS");
                if(i != 218)
                {
                    if(LOG.isDebugEnabled())
                        LOG.debug((new StringBuilder()).append("Invalid packet detected. First byte: ").append(i).toString());
                    continue;
                }
                i = in.read();
                if(i == -1)
                    throw new IOException("EOS");
                if(i >> 4 == 13)
                    break;
                if(LOG.isDebugEnabled())
                    LOG.debug((new StringBuilder()).append("Invalid packet detected. Second byte: ").append(i).toString());
            } while(true);
            packetType = PacketType.getPacketType((byte)(i & 0xf));
            length = read4ByteSigned(in);
        } else
        {
            throw new PersistentProtocolException((new StringBuilder()).append("Unknown version ").append(version).toString());
        }
        packet = new Packet(packetType);
        if(length > 0)
            readPayload(in, packet, length);
        if(packet.type == PacketType.ABORT)
        {
            String message = packet.popString();
            try
            {
                throw new PersistentAbortException(TranslatableMessage.deserialize(message));
            }
            catch(TranslatableMessageParseException e)
            {
                throw new PersistentAbortException(new TranslatableMessage("common.default", new Object[] {
                    message
                }));
            }
        } else
        {
            return packet;
        }
    }

    private static void readPayload(InputStream in, Packet packet, int length)
        throws IOException, PayloadReadTimeoutException
    {
        long timeout = System.currentTimeMillis() + 60000L;
        int readCount;
        for(; length > 0; length -= readCount)
        {
            if(System.currentTimeMillis() > timeout)
                throw new PayloadReadTimeoutException(packet.getType(), length, packet.payload.popAll());
            int readLen = length;
            if(readLen > PAYLOAD_READ_BUFFER.length)
                readLen = PAYLOAD_READ_BUFFER.length;
            readCount = in.read(PAYLOAD_READ_BUFFER, 0, readLen);
            packet.payload.push(PAYLOAD_READ_BUFFER, 0, readCount);
        }

    }

    public static void writePacket(OutputStream out, int version, Packet packet)
        throws IOException
    {
        writeHeader(out, version, packet.type, packet.payload.size());
        packet.payload.write(out);
    }

    public static void writePacket(OutputStream out, int version, PacketType type, byte payload[])
        throws IOException
    {
        writeHeader(out, version, type, payload.length);
        out.write(payload);
    }

    public static void writePacket(OutputStream out, int version, PacketType type, ByteQueue payload)
        throws IOException
    {
        writeHeader(out, version, type, payload.size());
        payload.write(out);
    }

    private static void writeHeader(OutputStream out, int version, PacketType type, int length)
        throws IOException
    {
        if(version == 1)
        {
            StreamUtils.write4ByteSigned(out, length);
            StreamUtils.writeByte(out, type.getId());
        } else
        if(version >= 2)
        {
            out.write(218);
            out.write(0xd0 | type.getId());
            write4ByteSigned(out, length);
        }
    }

    public static void pushBoolean(ByteQueue queue, boolean b)
    {
        queue.push(b ? TRUE : FALSE);
    }

    public static void pushString(ByteQueue queue, String s)
    {
        if(s == null)
        {
            queue.pushU2B(65535);
        } else
        {
            byte b[] = s.getBytes(CHARSET);
            if(b.length >= 65534)
            {
                queue.pushU2B(65534);
                queue.pushU3B(b.length);
            } else
            {
                queue.pushU2B(b.length);
            }
            queue.push(b);
        }
    }

    public static void pushInt(ByteQueue queue, int i)
    {
        queue.pushS4B(i);
    }

    public static void pushLong(ByteQueue queue, long l)
    {
        queue.pushU4B(l >> 32);
        queue.pushU4B(l);
    }

    public static void pushBytes(ByteQueue queue, byte a[])
    {
        queue.pushU2B(a.length);
        queue.push(a);
    }

    public static void pushDouble(ByteQueue queue, double d)
    {
        pushLong(queue, Double.doubleToLongBits(d));
    }

    private static void write4ByteSigned(OutputStream out, int i)
        throws IOException
    {
        out.write((byte)(i >> 24 & 0xff));
        out.write((byte)(i >> 16 & 0xff));
        out.write((byte)(i >> 8 & 0xff));
        out.write((byte)(i & 0xff));
    }

    private static int read4ByteSigned(InputStream in)
        throws IOException
    {
        return in.read() << 24 | in.read() << 16 | in.read() << 8 | in.read();
    }

    public static void pushJson(ByteQueue queue, Object o)
    {
        StringWriter out = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(Common.JSON_CONTEXT, out);
        try
        {
            jsonWriter.writeObject(o);
        }
        catch(JsonException e)
        {
            throw new ShouldNeverHappenException(e);
        }
        catch(IOException e)
        {
            throw new ShouldNeverHappenException(e);
        }
        pushString(queue, out.toString());
    }

    protected Packet(PacketType type)
    {
        this.type = type;
        payload = new ByteQueue();
    }

    public Packet(PacketType type, ByteQueue payload)
    {
        this.type = type;
        this.payload = payload;
    }

    public PacketType getType()
    {
        return type;
    }

    public ByteQueue getPayload()
    {
        return payload;
    }

    public boolean popBoolean()
    {
        return payload.pop() == 1;
    }

    public String popString()
    {
        int len = payload.popU2B();
        if(len == 65535)
            return null;
        if(len == 65534)
            len = payload.popU3B();
        return payload.popString(len, CHARSET);
    }

    public int popInt()
    {
        return payload.popS4B();
    }

    public long popLong()
    {
        return payload.popU4B() << 32 | payload.popU4B();
    }

    public double popDouble()
    {
        return Double.longBitsToDouble(popLong());
    }

    public byte[] popBytes()
    {
        byte a[] = new byte[payload.popU2B()];
        payload.pop(a);
        return a;
    }

    public Object popJson(Class clazz)
        throws JsonException
    {
        String s = popString();
        try
        {
            return (new JsonReader(Common.JSON_CONTEXT, s)).read(clazz);
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void pushDataPointInfo(ByteQueue queue, DataPointInfo info)
    {
        pushJson(queue, info);
    }

    public DataPointInfo popDataPointInfo()
        throws DoAbortException
    {
        try
        {
            return (DataPointInfo)popJson(DataPointInfo.class);
        }
        catch(JsonException e)
        {
            LOG.error("Json read error", e);
            throw new DoAbortException(new TranslatableMessage("event.persistent.pointDeserialization", new Object[] {
                e.getMessage()
            }));
        }
    }

    public static void pushDataPointVO(ByteQueue queue, int version, DataPointVO dpvo, byte serializedDataPoint[])
    {
        if(version >= 4)
            pushBytes(queue, serializedDataPoint);
        else
            queue.push(serializedDataPoint);
        if(version >= 4)
        {
            pushString(queue, dpvo.getName());
            pushString(queue, dpvo.getDeviceName());
            pushInt(queue, dpvo.getIntervalLoggingPeriodType());
            pushInt(queue, dpvo.getIntervalLoggingPeriod());
            pushInt(queue, dpvo.getIntervalLoggingType());
            pushDouble(queue, dpvo.getTolerance());
            pushInt(queue, dpvo.getPurgeType());
            pushInt(queue, dpvo.getPurgePeriod());
            pushInt(queue, dpvo.getDefaultCacheSize());
            pushBoolean(queue, dpvo.isDiscardExtremeValues());
            pushInt(queue, dpvo.getEngineeringUnits());
        }
    }

    public DataPointVO popDataPointVO(int version)
        throws DoAbortException
    {
        DataPointVO newDpvo;
        try
        {
            if(version >= 4)
                newDpvo = (DataPointVO)SerializationHelper.readObjectFromArray(popBytes());
            else
                newDpvo = (DataPointVO)SerializationHelper.readObjectFromArray(payload.popAll());
        }
        catch(Exception e)
        {
            LOG.error("Point deserialization error", e);
            throw new DoAbortException(new TranslatableMessage("event.persistent.pointDeserialization", new Object[] {
                e.getMessage()
            }));
        }
        if(version >= 4)
        {
            newDpvo.setName(popString());
            newDpvo.setDeviceName(popString());
            newDpvo.setIntervalLoggingPeriodType(popInt());
            newDpvo.setIntervalLoggingPeriod(popInt());
            newDpvo.setIntervalLoggingType(popInt());
            newDpvo.setTolerance(popDouble());
            newDpvo.setPurgeType(popInt());
            newDpvo.setPurgePeriod(popInt());
            newDpvo.setDefaultCacheSize(popInt());
            newDpvo.setDiscardExtremeValues(popBoolean());
            newDpvo.setEngineeringUnits(popInt());
        }
        return newDpvo;
    }
}
