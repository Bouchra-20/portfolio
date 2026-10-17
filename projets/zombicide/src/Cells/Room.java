package Cells;

import java.util.*;

public class Room extends Cell {
	/**
	 * Attributs of the pieces Class
	 */


	// protected Map<Integer, Equipment> equips;
	// protected String roomName;
	// protected int num;
	/**
	 * constructor of Pieces
	 * 
	 * @param x Coordinate X of the room
	 * @param y Coordinate Y of the room
	 */
	public Room(int x, int y) {
		super(x, y);
		// int doornumbr, Equipment equips, String roomName, int num
		// this.equips=new HashMap<>();
		// this.roomName=rommName;
		// this.doors = new HashMap<>();
		// this.num=num;

	}

	/**
	 * method that makes doors in Room and gives each a door a number
	 * 
	 * @param height the height of the building
	 * @param width the width of the building
	 */
	public void createDoors(int height, int width) {
		if ((super.x == 0 && super.y == 0) || (super.x == height - 1 && super.y == width - 1)) {
			for (int i = 0; i < 2; i++) {
				super.doors.add(new Door(i+1,this));
			}
		} else if (super.x == 0 || super.y == 0 || super.x == height - 1 || super.y == width - 1) {
			for (int i = 0; i < 3; i++) {
				super.doors.add(new Door(i+1,this));
			}
		} else {
			for (int i = 0; i < 4; i++) {
				super.doors.add(new Door(i+1,this));
			}
		}
	}

	// public Equipment pickEquipment(){
	// return null;//
	// }


}
