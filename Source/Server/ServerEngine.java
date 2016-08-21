package Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.swing.Timer;

import Imports.ImageReferencePair;
import Imports.Images;
import Server.Creatures.ServerCreature;
import Server.Creatures.ServerPlayer;

/**
 * Runs the actual game
 * @author William Xu & Alex Raita
 *
 */
public class ServerEngine implements Runnable, ActionListener
{

	/**
	 * A list of all the players in the server
	 */
	private static ArrayList<ServerPlayer> listOfPlayers;

	/**
	 * Player to remove
	 */
	private ArrayList<ServerPlayer> toRemove = new ArrayList<ServerPlayer>();

	/**
	 * A list of IDs currently used in the game (index is the ID, true means
	 * used, false means unused). Note that IDs can be freed when the object is
	 * deleted and re-assigned to another object
	 */
	private static boolean[] objectIDs;

	/**
	 * The number of possible ID's for any objects. The number of objects
	 * existing the game (aside from tiles) cannot exceed this limit
	 */
	public static final int NUMBER_OF_IDS = 100000;

	/**
	 * The world the engine works with
	 */
	private ServerWorld world;

	/**
	 * The rate at which the game runs at, in milliseconds
	 */
	public final static int UPDATE_RATE = 15;

	/**
	 * The startTime for checking FPS
	 */
	private long startTime = 0;

	/**
	 * The current FPS of the client
	 */
	private int currentFPS = 60;

	/**
	 * The team of the last player added
	 */
	private int lastTeam = 1;

	/**
	 * A counter updating every repaint and reseting at the expected FPS
	 */
	private int FPScounter = 0;

	/**
	 * The game loop timer
	 */
	private Timer updateTimer;

	/**
	 * The map for the server
	 */
	private ServerGUI gui = null;

	private boolean endGame = false;
	private int losingTeam;

	/**
	 * Constructor for the engine
	 */
	public ServerEngine(String map) throws IOException
	{
		// Start importing the images from the file (place in a loading screen
		// or something later)
		Images.importImages();
		ImageReferencePair.importReferences();

		listOfPlayers = new ArrayList<ServerPlayer>();
		objectIDs = new boolean[NUMBER_OF_IDS];
		world = new ServerWorld(this, map);

	}

	/**
	 * Constructor for the engine
	 */
	public ServerEngine() throws IOException
	{
		// Start importing the images from the file (place in a loading screen
		// or something later)
		Images.importImages();
		ImageReferencePair.importReferences();

		listOfPlayers = new ArrayList<ServerPlayer>();
		objectIDs = new boolean[NUMBER_OF_IDS];
		world = new ServerWorld(this);

	}

	/**
	 * Set the gui
	 * @param gui
	 */
	public void setGui(ServerGUI gui)
	{
		this.gui = gui;
	}

	@Override
	/**
	 * Constantly update the game
	 */
	public void run()
	{
		updateTimer = new Timer(UPDATE_RATE, this);
		startTime = System.currentTimeMillis();
		updateTimer.start();
	}

	/**
	 * Send messages to all the clients updating their player's data
	 */
	public synchronized void updateClients()
	{
		if (!toRemove.isEmpty())
		{
			for (ServerPlayer player : toRemove)
				listOfPlayers.remove(player);
			toRemove.clear();
		}
		try
		{
			for (ServerPlayer player : listOfPlayers)
			{
				player.updateClient();
				if (endGame)
					player.setEndGame(true, losingTeam);
			}
		}
		catch (ConcurrentModificationException e)
		{
			System.out.println("Concurrent modification occured");
			e.printStackTrace();
		}
	}

	public void endGame(int losingTeam)
	{
		endGame = true;
		this.losingTeam = losingTeam;
	}

	/**
	 * Send an instant message to all clients
	 */
	public void broadcast(String message)
	{
		for (ServerPlayer player : listOfPlayers)
		{
			player.sendMessage(message);
		}
		gui.addToChat(message);
	}

	/**
	 * Sends a message to the given team
	 */
	public void broadCastTeam(String message, int team)
	{
		for (ServerPlayer player : listOfPlayers)
		{
			if (player.getTeam() == team)
				player.sendMessage(message);
		}
		gui.addToChat(message);
	}

	/**
	 * Remove a player from the array list
	 * @param remove
	 */
	public synchronized void removePlayer(ServerPlayer remove)
	{
		toRemove.add(remove);
		lastTeam--;
		listOfPlayers.remove(remove);
		world.remove(remove);
		broadcast("R " + remove.getID());
		broadcast("RO " + remove.getName().split(" ").length + " "
				+ remove.getTeam() + remove.getName());
	}

	/**
	 * Add a new player to the game
	 * @param newPlayer the new player
	 */
	public void addPlayer(ServerPlayer newPlayer)
	{
		listOfPlayers.add(newPlayer);
		world.add(newPlayer);
	}

	/**
	 * Use and reserve the next available ID in the list of booleans
	 * @return the id
	 */
	public static int useNextID()
	{
		for (int id = 0; id < objectIDs.length; id++)
		{
			if (!objectIDs[id])
			{
				objectIDs[id] = true;
				return id;
			}
		}
		return -1;
	}

	@Override
	/**
	 * Update the game after every game tick (15 milliseconds)
	 */
	public void actionPerformed(ActionEvent e)
	{
		// Update the FPS counter on the server gui
		if (FPScounter >= (1000.0 / UPDATE_RATE + 0.5))
		{
			FPScounter = 0;
			currentFPS = (int) ((1000.0
					/ (System.currentTimeMillis() - startTime)
					* (1000.0 / UPDATE_RATE) + 0.5));
			startTime = System.currentTimeMillis();
		}
		FPScounter++;

		// Remove disconnected players
		ArrayList<ServerPlayer> listOfRemovedPlayers = new ArrayList<ServerPlayer>();
		for (ServerPlayer player : listOfPlayers)
		{
			if (player.isDisconnected())
			{
				listOfRemovedPlayers.add(player);
			}
		}
		for (ServerPlayer player : listOfRemovedPlayers)
		{
			listOfPlayers.remove(player);
		}

		// Move all the objects around and update them
		world.update();

		// Update all the clients with the new player data
		updateClients();

		// Update the gui
		if (gui != null)
		{
			gui.update();
		}

		// Parameters to fix the lag spike
		if (!lagSpike && world.getWorldCounter() > 1000 && getCurrentFPS() < 50)
		{
			lagSpike = true;
			System.out.println("~LAGSPIKE~");
			
			// Clear the object grid
			for (int row = 0; row < world.getObjectGrid().length; row++)
			{
				for (int column = 0; column < world.getObjectGrid()[0].length; column++)
				{
					world.getObjectGrid()[row][column] = new ArrayList<ServerObject>();
				}
			}
		}
		
		

	}

	boolean lagSpike = false;

	/**
	 * Get the emptiest team
	 * @return
	 */
	public int getNextTeam()
	{
		int noOfBlue = 0;
		int noOfRed = 0;
		for (ServerPlayer player : listOfPlayers)
		{
			if (player.getTeam() == ServerCreature.RED_TEAM)
			{
				noOfRed++;
			}
			else
			{
				noOfBlue++;
			}
		}

		if (noOfBlue == noOfRed)
		{
			if (Math.random() < 0.5)
			{
				return ServerCreature.BLUE_TEAM;
			}
			return ServerCreature.RED_TEAM;
		}
		else if (noOfBlue > noOfRed)
		{
			return ServerCreature.RED_TEAM;
		}
		return ServerCreature.BLUE_TEAM;
	}

	// ///////////////////////
	// GETTERS AND SETTERS //
	// ///////////////////////
	public int getCurrentFPS()
	{
		return currentFPS;
	}

	public void setCurrentFPS(int currentFPS)
	{
		this.currentFPS = currentFPS;
	}

	public int nextTeam()
	{
		return ++lastTeam;
	}

	public ServerWorld getWorld()
	{
		return world;
	}

	public static ArrayList<ServerPlayer> getListOfPlayers()
	{
		return listOfPlayers;
	}

	public static void setListOfPlayers(ArrayList<ServerPlayer> newListOfPlayers)
	{
		listOfPlayers = newListOfPlayers;
	}
}
