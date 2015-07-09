package uk.co.kasl.topicserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ClientHandler extends Thread{

	Socket clientThread;
	public static List<Socket> clientList = new ArrayList<Socket>();
	public static Vector<String> topics = new Vector<String>();
	
	boolean serveClient = true;
	BufferedReader br = null;
	PrintWriter out = null;
	NodeList nodeList= null;
	Node node = null;
	List<Element> element = null;
	Element child = null;
	Map<String,String> data = null;
	MessageType cmd = null;
	List<User> users = new ArrayList<User>();
	public static List<String> usernames = new ArrayList<String>();
	
	public enum MessageType{
		LOGIN,
		PING,
		ADD_TOPIC,
		SELECT_TOPIC,
		APPEND_TOPIC,
		CLOSE_TOPIC,
		GET_TOPICS,
		AUCTION,
		BID;
		public static MessageType getType(String type){
			MessageType msgType = PING;
			switch(type){
			case "login" : msgType = MessageType.LOGIN; break;
			case "ping" : msgType = MessageType.PING; break;
			case "select" : msgType = MessageType.SELECT_TOPIC; break;
			case "close" : msgType = MessageType.CLOSE_TOPIC; break;
			case "append" : msgType = MessageType.APPEND_TOPIC; break;
			case "auction" : msgType = MessageType.AUCTION; break;
			case "bid" : msgType = MessageType.BID; break;
			case "new" : msgType = MessageType.ADD_TOPIC; break;
			case "get" : msgType = MessageType.GET_TOPICS;
			}
			return msgType;
		}
	}
	
	public ClientHandler(Socket client){
		clientThread = client;
		if(!clientList.contains(client))
			ClientHandler.clientList.add(client);
		System.out.println("No. Of Client : " + clientList.size());
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try{
			br = new BufferedReader(new InputStreamReader(clientThread.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(clientThread.getOutputStream()));
			while(serveClient){
				String clientMsg = br.readLine();
				System.out.println(clientMsg);
				ParseHandleMsg(clientMsg);
			}
		}catch(SocketException e){
			System.out.println("Client has been disconnected.");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	private synchronized void ParseHandleMsg(String clientMsg) {
		// TODO Auto-generated method stub
		if(clientMsg != null){
		try {
			String toSend = "";
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
		    is.setCharacterStream(new StringReader(clientMsg));
			Document doc = builder.parse(is);
			data = new HashMap<>();
			node = doc.getDocumentElement();
			String command = node.getNodeName();
			//System.out.println("");
			cmd = MessageType.getType(command);
			nodeList = doc.getDocumentElement().getChildNodes();
			for(int i = 0; i<nodeList.getLength(); i++){
				node = nodeList.item(i);
				if(node instanceof Element){
					data.put(node.getNodeName(), node.getTextContent());
				}
			}
			switch(cmd){
			case LOGIN: 
				if(!usernames.contains(data.get("username"))){
					usernames.add(data.get("username"));
					users.add(new User(data.get("username"),data.get("password")));
					toSend = "<login><result>success</result></login>";
				}else{
					toSend = "<login><result>failed</result></login>";					
				}
				break;
			case PING: break;
			case SELECT_TOPIC:
				toSend = sendSelectTopic(data.get("username"),data.get("topic"));
				break;
			case APPEND_TOPIC: break;
			case CLOSE_TOPIC: break;
			case AUCTION: break;
			case BID: break;
			case ADD_TOPIC:
				if(!topics.contains(data.get("name"))){
					topics.add(data.get("name"));
					toSend = "<new><result>success</result></new>";
					informClients();
				}else{
					toSend = "<new><result>failed</result></new>";
				}
				break;
			case GET_TOPICS:
				toSend = getTopics(); break;
			}
			if(toSend != ""){
				out.println(toSend);
				out.flush();
				toSend = "";
			}
			/*Iterator it = data.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry pair = (Map.Entry)it.next();
				System.out.println(pair.getKey() + " : " + pair.getValue());
				it.remove();
			}*/
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	private String sendSelectTopic(String username, String topic) {
		// TODO Auto-generated method stub
		String toSend = "<select><username>" + username + "</username><topic>" + topic +"</topic></select>";
		for(int i=0; i<clientList.size(); i++){
			try {
				PrintWriter output = new PrintWriter(new OutputStreamWriter(clientList.get(i).getOutputStream()));
				output.println("<select><username>" + username + "</username><topic>" + topic +"</topic></select>");
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toSend;
	}
	private void informClients() {
		// TODO Auto-generated method stub
		for(int i=0; i<clientList.size(); i++){
			try {
				PrintWriter output = new PrintWriter(new OutputStreamWriter(clientList.get(i).getOutputStream()));
				output.println(getTopics());
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private String getTopics() {
		// TODO Auto-generated method stub		
		String topicsList = "<get>";
		synchronized(topics){
		for(int i=0; i< topics.size(); i++){
			topicsList += "<name" + i + ">" + topics.get(i) + "</name" + i + ">";
		}
		topicsList += "</get>";
		}
		return topicsList;
	}
	
}