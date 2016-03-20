/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.example.rajat.prakharBirthdayCard2016.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.cmd.Query;

import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.googlecode.objectify.ObjectifyService.register;

/** An endpoint class we are exposing */
@Api(
  name = "myApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.prakharBirthdayCard2016.rajat.example.com",
    ownerName = "backend.prakharBirthdayCard2016.rajat.example.com",
    packagePath=""
  )
)
public class MyEndpoint {

    static {
        register(GreetingMsg.class);

//        register(MyEndpoint.class);
    }


    /** A simple endpoint method that takes a name and says Hi back */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Hi, " + name);

        return response;
    }

    @ApiMethod(name="sayGreeting")
    public MyBean sayGreeting(@Named("name") String name, @Named("period") String period) {
        MyBean response = new MyBean();
        response.setData("Good " + period + " " + name);

        return response;
    }

    @ApiMethod(name="sendGreeting")
    public GreetingMsg sendGreeting(@Named("GreetingMsg") String greetingMsgReceived) {
        final GreetingMsg greetingMsg = new GreetingMsg(greetingMsgReceived);

        ofy().save().entity(greetingMsg).now();
        return greetingMsg;
    }

    @ApiMethod(name="receiveGreetings")
    public MyBean receiveGreetings(@Named("id") Long id) {
        Query<GreetingMsg> q = ofy().load().type(GreetingMsg.class).filter("id >", id);
        MyBean responseMsg = new MyBean();
        responseMsg.setData("{ \"messages\": [");

        int notFirst = 1;

        for(GreetingMsg g: q) {
            System.out.println("Greeting" + g.toString());
            System.out.println("MsgId = " + g.getMsgId() + " " + g.getMsgDetail());

            if (notFirst == 1) {
                notFirst = 0;
//                responseMsg.appendData("\n");
            } else {
                responseMsg.appendData(",");
            }
            responseMsg.appendData("{ \"id\": \"" + g.getMsgId() + "\", \"msg\": \"" + g.getMsgDetail() + "\" }");
        }

        responseMsg.appendData("] }");

        return responseMsg;
    }

}
