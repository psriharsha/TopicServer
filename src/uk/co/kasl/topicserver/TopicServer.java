package uk.co.kasl.topicserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TopicServer {

	ServerSocket serverSocket;
	boolean serverStatus = true;
	
	public TopicServer(){
		try {
			serverSocket = new ServerSocket(11111);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Server cannot be initiated");
		}
		System.out.println("Server is listening now...");
		while(serverStatus){
			try {
				Socket clientSocket = serverSocket.accept();
				ClientHandler client = new ClientHandler(clientSocket);
				client.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try{
			serverSocket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		new TopicServer();
	}
	
}