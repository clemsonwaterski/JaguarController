package org.ros.android.jaguar;

/**
 * Created by dale on 7/7/15.
 */
import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

     public class Listener extends AbstractNodeMain {

           @Override
   public GraphName getDefaultNodeName() {
         return GraphName.of("jaguar4x4_2014/listener");
       }

           @Override
   public void onStart(ConnectedNode connectedNode) {
         final Log log = connectedNode.getLog();
         Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("/drrobot_motor_cmd", "std_msgs/String");
         subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
               @Override
               public void onNewMessage(std_msgs.String message) {
                     log.info("I heard: \"" + message.getData() + "\"");
                   }
             });
       }
 }
