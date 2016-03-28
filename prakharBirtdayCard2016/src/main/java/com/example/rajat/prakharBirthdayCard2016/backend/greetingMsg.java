package com.example.rajat.prakharBirthdayCard2016.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.cmd.Query;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by hscuser on 18/03/16.
 */
@Entity
public class GreetingMsg {

enum DeviceType {
    ANDROID,
    iOS
};
    @Id
    Long uniqueId;

    @Index
    Long id;
    String msgDetail;
//    String phoneNumber;
//    DeviceType deviceType; // Android/iOS/Web
//    Date date;

    private GreetingMsg() {}

    public GreetingMsg(String msgDetail) {

        this.id = this.generateNewMessageId();
        System.out.println("Generated Msg Id = " + this.id);
        this.msgDetail = msgDetail;
//        this.phoneNumber = "";
//        this.deviceType = DeviceType.ANDROID;
//        this.date = new Date();
    }

    Long generateNewMessageId() {
        long currentMaxMsgId = 0;

        // get current max msg ID
        Query<GreetingMsg> q = ofy().load().type(GreetingMsg.class).order("-id").limit(1);

        for(GreetingMsg g: q) {
            System.out.println("Greeting" + g.toString());
            currentMaxMsgId = g.getMsgId();
            System.out.println("MsgId = " + currentMaxMsgId);
        }

        return (currentMaxMsgId + 1);
    }

    public Long getMsgId() {
        return id;
    }

    public void setMsgId(Long msgId) {
        this.id = msgId;
    }

    public String getMsgDetail() {
        return msgDetail;
    }

    public void setMsgDetail(String msgDetail) {
        this.msgDetail = msgDetail;
    }

}
