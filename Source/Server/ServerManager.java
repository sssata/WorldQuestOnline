package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Client.ClientFrame;
import Menu.MainMenu;

public class ServerManager implements Runnable{

	private ServerSocket socket;
	private ArrayList<Server> rooms = new ArrayList<Server>();
	private int maxRooms;
	private ClientFrame mainFrame;

	public ServerManager(int port, int maxRooms, ClientFrame mainFrame)
	{
		this.maxRooms = maxRooms;
		this.mainFrame = mainFrame;
		addNewRoom();
		try
		{
			this.socket = new ServerSocket(port);
		}
		catch (IOException e)
		{
			System.out.println("Server cannot be created with given port");
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		outerloop:
			while(true)
			{
				try {
					Socket newClient = socket.accept();
					PrintWriter output = new PrintWriter(newClient.getOutputStream());
					for(Server room : rooms)
					{
						if(!room.isFull())
						{
							if(room.started())
							{
								output.println("CONNECTED");
								System.out.println("CONNECTED GAME STARTED");
								output.flush();
								room.addClient(newClient,output);
							}
							else{
								output.println("CONNECTED");
								System.out.println("CONNECTED TO LOBBY");
								output.flush();
								room.addClient(newClient,output);
							}
							continue outerloop; //Once the client is added wait for a new one
						}
					}

					if(rooms.size() < maxRooms)
					{
						output.println("CONNECTED");
						System.out.println("CONNECTED NEW ROOM");
						output.flush();
						addNewRoom();
						rooms.get(rooms.size()-1).addClient(newClient,output);
					}
					else //No More Space
					{	
						System.out.println("Sent full message to client");
						output.println("FULL");
						output.flush();
						output.close();
						newClient.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

	}

	public void addNewRoom()
	{
		Server newServer = new Server();
		rooms.add(newServer);
		Thread serverThread = new Thread(newServer);
		serverThread.start();

		ServerGUI gui = new ServerGUI(newServer);
		mainFrame.dispose();
		ServerFrame myFrame = new ServerFrame();
		gui.setLocation(0, 0);
		myFrame.add(gui);
		gui.revalidate();
		newServer.setGUI(gui);
	}


}
