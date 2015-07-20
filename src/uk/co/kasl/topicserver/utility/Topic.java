package uk.co.kasl.topicserver.utility;

import java.util.Vector;

public class Topic {
	
	public String topicName;
	public String message;
	public Message msg;
	Vector<Message> msgs = new Vector<Message>();
	
	public Topic(String name){
		topicName = name;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	
	public void addMessage(String user,String thisMessage){
		msg = new Message(user, thisMessage);
		msgs.add(msg);
	}
	
	public Vector<Message> getTopicMessage(){
		Vector<Message> allMessages = new Vector<Message>();
		for(int i=0; i<msgs.size(); i++){
			allMessages.add(msgs.get(i));
		}
		return allMessages;
	}
	
}