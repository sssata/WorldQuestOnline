package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class ClientWorld {

	private ArrayList<Object> objects = new ArrayList<Object>();

	public void add(Object object)
	{
		objects.add(object);
	}

	/**
	 * Draws the world
	 * @param graphics the graphics component to be drawn on
	 * @param playerX the position of the player
	 * @param playerY the position of the player
	 */
	public void draw(Graphics graphics, int playerX, int playerY)
	{
		//Go through each object in the world and draw it relative to the player's position
		try{
			for(Object object : objects)
			{
				int x = object.getX()-playerX + Client.SCREEN_WIDTH/2 - Client.TILE_SIZE/2;
				int y = object.getY()-playerY + Client.SCREEN_HEIGHT/2 - Client.TILE_SIZE/2;

				if(object.getDesc().equals("TILE"))
				{
					Tile tile = (Tile)object;

					//Figure out tyep of tile and place it
					if(tile.getType() == 1)
						graphics.setColor(Color.BLACK);
					else if(tile.getType() == 0)
						graphics.setColor(Color.RED);
					graphics.fillRect(x,y, Client.TILE_SIZE, Client.TILE_SIZE);

				}
				else if(object.getDesc().equals("PLAYER"))
				{
					OtherPlayer player = (OtherPlayer)object;
					graphics.setColor(player.getColour());
					graphics.fillRect(x,y, Client.TILE_SIZE, Client.TILE_SIZE);
				}
			}
		}
		//this might cause some problems in the future
		catch(ConcurrentModificationException E)
		{

		}

	}
	public ArrayList<Object> getObjects() {
		return objects;
	}

	public void clear()
	{
		objects.clear();
	}
	/**
	 * Checks if the world contains a given object
	 * @param object the object to be checked
	 * @return true if the object is found in the world, false if not
	 */
	public boolean contains(Object object)
	{
		for(Object obj : objects)
			if(obj.compareTo(object) == 0)
				return true;
		return false;
	}

	/**
	 * Gets the actual object in the world
	 * @param object the object to be fetched
	 * @return the desired object
	 */
	public Object get(Object object)
	{
		for(Object obj : objects)
			if(obj.compareTo(object) == 0)
				return obj;
		return null;
	}
}