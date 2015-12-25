package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Imports.Images;

/**
 * Creates a new world and accepts new client connections
 * 
 * @author William Xu & Alex Raita
 * 
 */
public class Server implements Runnable
{
	private ServerSocket socket;
	private ServerEngine engine;
	private int port;

	public Server()
	{
		port = 5000;

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
	public void run()
	{
		// Construct the new world
		System.out.println("Creating world...");
		try
		{
			engine = new ServerEngine();
		}
		catch (IOException e1)
		{
			System.out.println("Error with Creating World and/or Engine");
		}
		Thread newEngine = new Thread(engine);
		newEngine.start();

		// Accept players into the server
		System.out.println("Waiting for clients to connect");

		while (true)
		{
			try
			{
				Socket newClient = socket.accept();
				ServerPlayer newPlayer = new ServerPlayer(newClient, engine,
						ServerPlayer.PLAYER_X, ServerPlayer.PLAYER_Y, -1, -1,ServerWorld.GRAVITY,
						engine.useNextID(), "PLAYER_RIGHT"
								+ Images.IMAGE_FORMAT);
				engine.addPlayer(newPlayer);
				Thread playerThread = new Thread(newPlayer);
				playerThread.start();

				System.out.println("A new client has connected");
			}
			catch (IOException e)
			{
				System.out.println("Error connecting to client");
				e.printStackTrace();
			}
		}
	}
	
	public ServerEngine getEngine()
	{
		return engine;
	}

}
