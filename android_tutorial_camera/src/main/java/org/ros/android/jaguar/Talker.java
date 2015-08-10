package org.ros.android.jaguar;

/*
00002  * Copyright (C) 2011 Google Inc.
00003  *
00004  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
00005  * use this file except in compliance with the License. You may obtain a copy of
00006  * the License at
00007  *
00008  * http://www.apache.org/licenses/LICENSE-2.0
00009  *
00010  * Unless required by applicable law or agreed to in writing, software
00011  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
00012  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
00013  * License for the specific language governing permissions and limitations under
00014  * the License.
00015  */

 import org.ros.concurrent.CancellableLoop;
 import org.ros.namespace.GraphName;
 import org.ros.node.AbstractNodeMain;
 import org.ros.node.ConnectedNode;
 import org.ros.node.topic.Publisher;

 public class Talker extends AbstractNodeMain {

     private Publisher<std_msgs.String> talkerPublisher;

       @Override
        public GraphName getDefaultNodeName() {
             return GraphName.of("jaguar4x4_2014/talker");
           }

       @Override
        public void onStart(final ConnectedNode connectedNode) {
             final Publisher<std_msgs.String> publisher =
                         connectedNode.newPublisher("/drrobot_motor_cmd", "std_msgs/String");
             // This CancellableLoop will be canceled automatically when the node shuts
             // down.
           talkerPublisher = publisher;
             connectedNode.executeCancellableLoop(new CancellableLoop() {
                   private int sequenceNumber;

                           @Override
                   protected void setup() {
                         sequenceNumber = 0;
                       }

                           @Override
                   protected void loop() throws InterruptedException {
                               /*
                         std_msgs.String initStr = publisher.newMessage();
                         initStr.setData("MMW !MG");
                         publisher.publish(initStr);
                         Thread.sleep(1000);
                         std_msgs.String str = publisher.newMessage();
                         str.setData("MMW !M 100 100");
                         publisher.publish(str);
                         sequenceNumber++;
                         Thread.sleep(1000);
                         */
                       }
                 });
           }
        public void publishMessage(String message){
            std_msgs.String str = talkerPublisher.newMessage();
            str.setData(message);
            talkerPublisher.publish(str);
        }

}