package com.serotonin.m2m2.pop3;

import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.DataSourceUtils;
import com.serotonin.m2m2.rt.dataSource.NoMatchException;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;

import javax.mail.*;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class Pop3DataSourceRT extends PollingDataSource {
    public static final int INBOX_EXCEPTION_EVENT = 1;
    public static final int MESSAGE_READ_EXCEPTION_EVENT = 2;
    public static final int PARSE_EXCEPTION_EVENT = 3;
    private final Pop3DataSourceVO vo;

    public Pop3DataSourceRT(Pop3DataSourceVO vo) {
        super(vo);
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
        this.vo = vo;
    }

    public void removeDataPoint(DataPointRT dataPoint) {
        returnToNormal(3, System.currentTimeMillis());
    }

    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
    }

    protected void doPoll(long time) {
        Folder folder = null;
        Store store = null;
        try {
            boolean messagesRead = false;
            TranslatableMessage messageReadError = null;
            TranslatableMessage parseError = null;

            Properties props = System.getProperties();
            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("pop3");

            store.connect(this.vo.getPop3Server(), this.vo.getUsername(), this.vo.getPassword());

            folder = store.getDefaultFolder();
            if (folder == null) {
                throw new Exception("No default folder");
            }
            folder = folder.getFolder("INBOX");
            if (folder == null)
                throw new Exception("No POP3 inbox");
            folder.open(2);

            Message[] messages = folder.getMessages();
            for (Message message : messages) {
                messagesRead = true;
                Pop3Email pop3Email = null;
                try {
                    pop3Email = readMessage(message);

                    message.setFlag(Flags.Flag.DELETED, true);
                } catch (Exception e) {
                    if (messageReadError == null)
                        messageReadError = new TranslatableMessage("common.default", new Object[]{e.getMessage()});
                } finally {
                    message.setFlag(Flags.Flag.DELETED, true);
                }
                try {
                    processMessage(pop3Email, time);
                } catch (TranslatableException e) {
                    if (parseError == null) {
                        parseError = e.getTranslatableMessage();
                    }
                }
            }
            returnToNormal(1, time);

            if (messagesRead) {
                if (messageReadError != null)
                    raiseEvent(2, time, false, messageReadError);
                else {
                    returnToNormal(2, time);
                }
                if (parseError != null)
                    raiseEvent(3, time, false, parseError);
                else
                    returnToNormal(3, time);
            }
        } catch (Exception e) {
            raiseEvent(1, time, false, new TranslatableMessage("common.default", new Object[]{e.getMessage()}));
        } finally {
            try {
                if (folder != null)
                    folder.close(true);
            } catch (MessagingException e) {
            }
            try {
                if (store != null)
                    store.close();
            } catch (MessagingException e) {
            }
        }
    }

    private Pop3Email readMessage(Message message) throws MessagingException, IOException {
        Pop3Email email = new Pop3Email();

        StringBuffer body = new StringBuffer();
        Object content = message.getContent();
        if ((content instanceof String)) {
            body.append((String) content);
        } else if ((content instanceof Multipart)) {
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bp = multipart.getBodyPart(i);
                String contentType = bp.getContentType();
                if (contentType.startsWith("text/")) {
                    body.append(bp.getContent());
                }
            }
        }

        email.setBody(body.toString());
        email.setSubject(message.getSubject());

        Date date = message.getSentDate();
        if (date == null)
            date = message.getReceivedDate();
        email.setDate(date);

        return email;
    }

    private void processMessage(Pop3Email pop3Email, long time) throws TranslatableException {
        for (DataPointRT dp : this.dataPoints) {
            Pop3PointLocatorRT locator = (Pop3PointLocatorRT) dp.getPointLocator();
            String content;
            if (locator.isFindInSubject())
                content = pop3Email.getSubject();
            else
                content = pop3Email.getBody();
            DataValue value;
            try {
                value = DataSourceUtils.getValue(locator.getValuePattern(), content, locator.getDataTypeId(), locator.getBinary0Value(), dp.getVO().getTextRenderer(), locator.getValueFormat(), dp.getVO().getName());
            } catch (NoMatchException e) {
                throw e;
            }
            if (locator.isIgnoreIfMissing())
                continue;
            long valueTime;
            if (locator.isUseReceivedTime()) {
                if (pop3Email.getDate() != null)
                    valueTime = pop3Email.getDate().getTime();
                else
                    valueTime = System.currentTimeMillis();
            } else {
                valueTime = DataSourceUtils.getValueTime(time, locator.getTimePattern(), pop3Email.getBody(), locator.getTimeFormat(), dp.getVO().getName());
            }

            dp.updatePointValue(new PointValueTime(value, valueTime));
        }
    }
}