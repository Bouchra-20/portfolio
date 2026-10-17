package Board;
import Cells.*;
import Equipments.*;
import Players.*;
import java.util.*;

public class Board {

	/**height of the board**/
	protected int height;
	/**width of the board**/
	protected int width;
	/**the board**/
	protected Cell[][] cells;
	/**Sewers**/
	protected Sewer sewer1;
	protected Sewer sewer2;
	protected Sewer sewer3;
	protected Sewer sewer4;
	List<Sewer> sewers;
	
    private static final Random random = new Random(); // Single instance of Random for use throughout

	
	/**Constructor of Board
	 * @param height of the board
	 * @param width of the board
	 */
	public Board(int width, int height) {
		this.cells = new Cell[width][height];
		this.height = height;
		this.width = width;
		this.sewers = new ArrayList<>();
	}

	/**get the height
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**get the width
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	
	/**get the board
	 * @return the board
	 */
	public Cell[][] getBoard() {
		return this.cells;
	}

	
	// Method to generate the streets and the initial grid layout
    public void generate() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
        // Determine the main streets' positions
        int streetVer = getRandomInRange(2, height - 3);
        int streetHor = getRandomInRange(2, width - 3);


        // Fill the vertical street cells with Street objects
        for (int i = 0; i < width; i++) {
            cells[i][streetVer] = new Street(i, streetVer);
        }
        // Fill the horizontal street cells with Street objects
        for (int j = 0; j < height; j++) {
            cells[streetHor][j] = new Street(streetHor, j);
        }


        //Recursively generate additional streets in each quadrant (4 times) to get the street correctly.
        generateStreets(0, 0, streetHor - 1, streetVer - 1); // // Top-left area
        generateStreets(0, streetVer + 1, streetHor - 1, height - 1); // Top-right area
        generateStreets(streetHor + 1, 0, width - 1, streetVer - 1); // Bottom-left area
        generateStreets(streetHor + 1, streetVer + 1, width - 1, height - 1); // Bottom-right area


        // Set up sewers at the ends of two main streets
        ((Street)cells[0][streetVer]).addSewer(); // vertical street top
        this.sewer1 = ((Street)cells[0][streetVer]).getSewer();
        this.sewer1.setCell(cells[0][streetVer]);
        sewers.add(sewer1);
        ((Street)cells[width - 1][streetVer]).addSewer(); //Vertical lower end of street
        this.sewer2 = ((Street)cells[width - 1][streetVer]).getSewer();
        this.sewer2.setCell(cells[width - 1][streetVer]);
        sewers.add(sewer2);
        ((Street)cells[streetHor][0]).addSewer(); //Left end of horizontal street
        this.sewer3 = ((Street)cells[streetHor][0]).getSewer();
        this.sewer3.setCell(cells[streetHor][0]);
        sewers.add(sewer3);
        ((Street)cells[streetHor][height - 1]).addSewer();  // Right end of horizontal street
        this.sewer4 = ((Street)cells[streetHor][height - 1]).getSewer();
        this.sewer4.setCell((cells[streetHor][height - 1]));
        sewers.add(sewer4);
    }


    // Recursive method to generate additional streets
    public void generateStreets(int startX, int startY, int endX, int endY) {
        // Base case: if the area is too small, return without adding streets
        if ((endX - startX + 1 < 5) || (endY - startY + 1 < 5)) {
            return;
        } else {

            // Randomly decide to generate horizontal or vertical streets
            boolean isHorizontal = new Random().nextBoolean();

            if (isHorizontal) {
                // Generate a Horizontal street and recursively generate more 
                int yPosition = getRandomInRange(startY + 2, endY - 2);
                for (int i = startX; i <= endX; i++) {
                    cells[i][yPosition] = new Street(i, yPosition);
                }

                generateStreets(startX, startY, endX, yPosition - 1);
                generateStreets(startX, yPosition + 1, endX, endY);
            } else {
                // Generate a vertical street and recursively generate more 
                int xPosition = getRandomInRange(startX + 2, endX - 2);
                for (int i = startY; i <= endY; i++) {
                    cells[xPosition][i] = new Street(xPosition, i);
                }

                generateStreets(startX, startY, xPosition - 1, endY);
                generateStreets(xPosition + 1, startY, endX, endY);
            }
        }
    }
    /** fill the board with Buildings
	 * @param y horizontal coordinates to set
	 * @param x vertical coordinates to set
	 */
    public void fillWithBuildings(int x, int y) {
    	if(y >= height ||  x >= width){
			return ;
		}else{
            int origX = x;
            while(y < height){
                while(x < width && !(cells[x][y] instanceof Street)){
                    Room room = new Room(x, y);
                    //room.createDoors(width, height);
                    cells[x][y] = room ;
                    x++;
                }
                x = origX;
                y++ ;
            }
            fillWithBuildings(origX+1,0);
        	fillWithBuildings(0,y);
		}
    }
    
    // fill the board with the Pharmacie Room or Hotel
    public void placeSpecialRooms() {
        Random random = new Random();

        // Place the Pharmacie
        boolean placedPharmacie = false;
        while (!placedPharmacie) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            // Ensure the selected cell is neither a Street nor already occupied by special rooms
            if (!(cells[x][y] instanceof Street) && !(cells[x][y] instanceof Pharmacy) && !(cells[x][y] instanceof Hotel)) {
                cells[x][y] = new Pharmacy(x, y); // Place the Pharmacie at the selected position
                placedPharmacie = true;
            }
        }

        // Place the Hotel
        boolean placedHotel = false;
        while (!placedHotel) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            // Ensure the selected cell is neither a Street nor already occupied by special rooms
            if (!(cells[x][y] instanceof Street) && !(cells[x][y] instanceof Pharmacy) && !(cells[x][y] instanceof Hotel)) {
                cells[x][y] = new Hotel(x, y); // Place the Hotel at the selected position
                placedHotel = true;
            }
        }
    }
    
    
    public List<Sewer> getSewers(){
        return this.sewers;
    }
    
    public void addDoor(){
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = cells[j][i];
                if(!(cell instanceof Street)){
                    if(j+1< this.width){
                        Door door = new Door();
                        cells[j][i].doors().add(door);
                        if(cells[j+1][i].doors().size()<4){
                            cells[j+1][i].doors().add(door);
                        }
                        if(cells[j+1][i] instanceof Street){
                            Door d = this.getDoor(j, i, "right");
                            d.toOpen();
                        }
                    }
                    if(i+1< this.height){
                        Door door = new Door();
                        cells[j][i].doors().add(door);
                        if(cells[j][i+1].doors().size()<4){
                            cells[j][i+1].doors().add(door);
                        }
                        if(cells[j][i+1] instanceof Street){
                            Door d = this.getDoor(j, i, "down");
                            d.toOpen();
                        }
                    }
                    if(j-1>=0 && cells[j-1][i] instanceof Street){
                        Door door = new Door();
                        cells[j][i].doors().add(door);
                        if(cells[j-1][i].doors().size()<2){
                            cells[j-1][i].doors().add(door);
                        }
                        for(Door d : cells[j-1][i].doors()){
                            d.toOpen();
                        }
                    }
                    if(i-1>=0 && cells[j][i-1] instanceof Street){
                        Door door = new Door();
                        cells[j][i].doors().add(door);
                        if(cells[j][i-1].doors().size()<2){
                            cells[j][i-1].doors().add(door);
                        }
                        for(Door d : cells[j][i-1].doors()){
                            d.toOpen();
                        }
                    }
                }
                
            }
        }
    }
    

    // Generates a Random Int number from a defined range   
    private static int getRandomInRange(int min, int max) {
        //add the condition to let it positive
        if (min >= max) {
            return min;
        }

        return random.nextInt((max - min) + 1) + min;
    }
    
    public Cell findCarrefour() {
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                Cell cellule = this.cells[i][j];
                if (cellule instanceof Street) {
                    int countRuesVoisines = 0;
                    if (i-1 >= 0 && this.cells[i - 1][j] instanceof Street) {
                        countRuesVoisines++; // Voisin au nord
                    }
                    if (i + 1 < this.width && this.cells[i + 1][j] instanceof Street) {
                        countRuesVoisines++; // Voisin au sud
                    }
                    if (j-1 > 0 && this.cells[i][j - 1] instanceof Street) {
                        countRuesVoisines++; // Voisin à l'ouest
                    }
                    if (j + 1 < this.height && this.cells[i][j + 1] instanceof Street) {
                        countRuesVoisines++; // Voisin à l'est
                    }

                    // Si la cellule a au moins deux voisins rues, c'est un carrefour
                    if (countRuesVoisines == 4) {
                    	return cellule;
                    }
                }
            }
        }

        return null;
    }
    public boolean isCarrefour(int x, int y){
        Cell cell = this.cells[x][y];
        if (cell instanceof Street) {
            int countRuesVoisines = 0;
            if (x-1 >= 0 && this.cells[x - 1][y] instanceof Street) {
                countRuesVoisines++; // Voisin au nord
            }
            if (x + 1 < this.width && this.cells[x + 1][y] instanceof Street) {
                countRuesVoisines++; // Voisin au sud
            }
            if (y-1 > 0 && this.cells[x][y - 1] instanceof Street) {
                countRuesVoisines++; // Voisin à l'ouest
            }
            if (y + 1 < this.height && this.cells[x][y + 1] instanceof Street) {
                countRuesVoisines++; // Voisin à l'est
            }
            // Si la cellule a au moins deux voisins rues, c'est un carrefour
            if (countRuesVoisines == 4) {
                return true;
            }
        }
        return false;
    }
    
    // Displays The Board 
    /*public void printBoard() {
        for (int i = 0; i < this.cells.length; i++) {
            for (int j = 0; j < this.cells[i].length; j++) {
                if (this.cells[i][j] instanceof Pharmacy) {
                	System.out.print("\u001B[32m|P|\u001B[0m ");
                } else if (this.cells[i][j] instanceof Hotel) {
                	System.out.print("\u001B[33m|H|\u001B[0m ");
                } else if (this.cells[i][j] instanceof Street) {
                    Street street = (Street) this.cells[i][j];
                    if (street.hasSewer()) {
                    	System.out.print("\u001B[34m[@]\u001B[0m ");
                    } else if(this.cells[i][j] instanceof Street && this.cells[i][j].hasZombie()){
                        System.out.print("\u001B[34m[Z]\u001B[0m ");
                    }else if(this.cells[i][j] instanceof Street && this.cells[i][j].hasPlayer()){
                        System.out.print("\u001B[34m[J]\u001B[0m ");
                    }else {
                    	System.out.print("\u001B[36m[ ]\u001B[0m ");
                    }
                } else if (this.cells[i][j] instanceof Room && this.cells[i][j].hasPlayer()) {
                    System.out.print("|J| ");
                }else if(this.cells[i][j] instanceof Room && this.cells[i][j].hasZombie()){
                    System.out.print("|Z| ");
                }else if(this.cells[i][j] instanceof Room && !this.cells[i][j].hasZombie()&& !this.cells[i][j].hasPlayer()){
                    System.out.print("| | ");
                } else {
                    System.out.print("   ");
                }
            }
            System.out.print("\n");
        }
    }*/
    
    
    public void printBoard() {
        /*for (int i = 0; i < this.height; i++) {
            for (int k = 0; k < this.width; k++) {
                if(this.cells[k][i] instanceof Street){
                    System.out.print("      ");
                }else if(i-1<0){
                    System.out.print("|⎺⎺⎺⎺|");
                }else{
                    System.out.print("|⎺  ⎺|");
                }
            }
            System.out.print("\n");
            for (int j = 0; j < this.width; j++) {
                if (this.cells[j][i] instanceof Pharmacy ) {
                    if(j-1< 0){
                        System.out.print("|✙    ");
                    }else if (j+1>= this.width) {
                	    System.out.print(" ✙   |");
                    }else{
                        System.out.print(" ✙    ");
                    }
                } else if (this.cells[j][i] instanceof Hotel) {
                    if(j-1< 0){
                        System.out.print("|🄷    ");
                    }else if (j+1>= this.width) {
                	    System.out.print(" 🄷   |");
                    }else{
                        System.out.print("  🄷   ");
                    }
                }else if (this.cells[j][i] instanceof Room){
                    if (this.cells[j][i].hasPlayer()) {
                        if(!this.cells[j][i].hasZombie()){
                            if(j-1< 0){
                                System.out.print("|🚶   ");
                            }else if (j+1>= this.width) {
                	            System.out.print(" 🚶  |");
                            }else{
                                System.out.print(" 🚶   ");
                            }
                        }else{
                            if(j-1< 0){
                	            System.out.print("|🧟 🚶");
                            }else if(j+1>= this.width){
                	            System.out.print(" 🧟🚶|");
                            }else{
                                System.out.print(" 🧟🚶 ");
                            }
                        }
                	}else{
                        if(this.cells[j][i].hasZombie()){
                            if(j-1< 0){
                	            System.out.print("|🧟   ");
                            }else if(j+1>= this.width){
                	            System.out.print(" 🧟  |");
                            }else{
                                System.out.print(" 🧟   ");
                            }
                        }else{
                            if(j-1< 0){
                                System.out.print("|     ");
                            }else if(j+1>= this.width){
                                System.out.print("     |");
                            }else{
                                System.out.print("      ");
                            }
                        }
                    }
                } else if (this.cells[j][i] instanceof Street) {
                    Street street = (Street) this.cells[j][i];
                    if (street.hasSewer()) {
                        if(this.cells[j][i].hasPlayer()){
                            System.out.print("  Ⓩ 🚶");
                        }else{
                            System.out.print("  Ⓩ   ");
                        }
                    }else if(this.isCarrefour(j,i)){
                        if(!this.cells[j][i].hasZombie()){
                            if(this.cells[j][i].hasPlayer()){
                                System.out.print("  🅙   ");
                            }else{
                                System.out.print("  ⚪   ");
                            }
                        }else if(!this.cells[j][i].hasPlayer()){
                            System.out.print("  🅩   ");
                        }
                    } else if(this.cells[j][i].hasPlayer() ){
                        if(this.cells[j][i].hasZombie()){
                            System.out.print(" 🚶🧟 ");
                        }else{
                            System.out.print(" 🚶   ");
                        }
                    }else if(!this.cells[j][i].hasPlayer() ){
                        if(this.cells[j][i].hasZombie()){
                            System.out.print("🧟    ");
                        }else{
                            System.out.print("      ");
                        }
                    }
                }else {
                	System.out.print("      ");
                }
            }
            
            System.out.print("\n");
            for (int c = 0; c < this.width; c++) {
                if(this.isCarrefour(c,i) || this.cells[c][i] instanceof Street){
                    System.out.print("      ");
                }else if(i+1 < this.height){
                    System.out.print("|_  _|");
                }else{
                    System.out.print("|____|");
                }
            	
            }
            
            System.out.print("\n");
        }
        System.out.print("\n");*/
    	this.printBoard2(0,0,this.width,this.height);
    }
    
    public void printBoard2(int x, int y, int endx, int endy) {
        for (int i = y; i < endy; i++) {
            for (int k = x; k < endx; k++) {
                if(this.cells[k][i] instanceof Street){
                	if(k-1>=0 && this.cells[k-1][i] instanceof Street){
                        System.out.print("        ");
                    }else{
                        System.out.print("       ");
                    }
                    //System.out.print("        ");
                }else if(i-1<0){
                    System.out.print("|⎺⎺⎺⎺⎺⎺|");
                }else if(i-1 >= 0){
                    if (this.cells[k][i] instanceof Room && this.cells[k][i-1] instanceof Street){
                        int p = 0;
                        Door d;
                        while (p<this.cells[k][i].doors().size() && !this.cells[k][i-1].doors().contains(this.cells[k][i].doors().get(p))){
                            p++;
                        }
                        if(p<this.cells[k][i].doors().size()){
                            d = this.cells[k][i].doors().get(p);
                            if(d.isOpen()){
                                System.out.print("|⎺⎺  ⎺⎺|");
                            }else{
                                System.out.print("|⎺⎺🚪⎺⎺|");
                            }
                        }
                        
                    }else{
                        System.out.print("|⎺⎺  ⎺⎺|");
                    }
                }
                   //System.out.print("|_  _|");
            }
            System.out.print("\n");
            for (int j = x; j < endx; j++) {
                if (this.cells[j][i] instanceof Pharmacy ) {
                    if(j-1 > 0 && this.cells[j-1][i] instanceof Street){
                        //System.out.print("🚪");
                    	System.out.print(" ");
                    }
                    if(j-1< 0){
                        System.out.print("|✙     ");
                            int k =0;
                            while (k<this.cells[j][i].doors().size() && !this.cells[j+1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                k++;
                            }
                            if(k<this.cells[j][i].doors().size()){
                                Door d = this.cells[j][i].doors().get(k);
                                if(d.isOpen()){
                                    System.out.print("  ");
                                }else{
                                    System.out.print("🚪");
                                }
                            }
                            
                    }else if (j+1>= this.width) {
                        /*int k =0;
                            while (k<this.cells[j][i].doors().size() && !this.cells[j-1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                k++;
                            }
                            if(k<this.cells[j][i].doors().size()){
                                Door d = this.cells[j][i].doors().get(k);
                                if(d.isOpen()){
                                    System.out.print("   ");
                                }else{
                                    System.out.print("🚪 ");
                                }
                            }*/
                	    System.out.print("   ✙  |");
                    }else{
                        /*int k =0;
                            while (k<this.cells[j][i].doors().size() && !this.cells[j-1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                k++;
                            }
                            if(k<this.cells[j][i].doors().size()){
                                Door d = this.cells[j][i].doors().get(k);
                                if(d.isOpen()){
                                    System.out.print("   ");
                                }else{
                                    System.out.print("🚪 ");
                                }
                            }*/
                        System.out.print("  ✙   ");
                        int k =0;
                            while (k<this.cells[j][i].doors().size() && !this.cells[j+1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                k++;
                            }
                            if(k<this.cells[j][i].doors().size()){
                                Door d = this.cells[j][i].doors().get(k);
                                if(d.isOpen()){
                                    System.out.print("  ");
                                }else{
                                    System.out.print("🚪");
                                }
                            }
                    }
                } else if (this.cells[j][i] instanceof Hotel) {
                    if(j-1 > 0 && this.cells[j-1][i] instanceof Street){
                        //System.out.print("🚪");
                    	System.out.print(" ");
                    }
                    if(j-1< 0){
                        System.out.print("|🄷     ");
                        int k =0;
                            while (k<this.cells[j][i].doors().size() && !this.cells[j+1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                k++;
                            }
                            if(k<this.cells[j][i].doors().size()){
                                Door d = this.cells[j][i].doors().get(k);
                                if(d.isOpen()){
                                    System.out.print("  ");
                                }else{
                                    System.out.print("🚪");
                                }
                            }
                    }else if (j+1>= this.width) {
                        /*int k =0;
                            while (k<this.cells[j][i].doors().size() && !this.cells[j-1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                k++;
                            }
                            if(k<this.cells[j][i].doors().size()){
                                Door d = this.cells[j][i].doors().get(k);
                                if(d.isOpen()){
                                    System.out.print("   ");
                                }else{
                                    System.out.print("🚪 ");
                                }
                            }*/
                	    System.out.print("🄷     |");
                    }else{
                        /*int k =0;
                            while (k<this.cells[j][i].doors().size() && !this.cells[j-1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                k++;
                            }
                            if(k<this.cells[j][i].doors().size()){
                                Door d = this.cells[j][i].doors().get(k);
                                if(d.isOpen()){
                                    System.out.print("   ");
                                }else{
                                    System.out.print("🚪 ");
                                }
                            }*/
                        System.out.print("   🄷  ");
                        int k =0;
                            while (k<this.cells[j][i].doors().size() && !this.cells[j+1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                k++;
                            }
                            if(k<this.cells[j][i].doors().size()){
                                Door d = this.cells[j][i].doors().get(k);
                                if(d.isOpen()){
                                    System.out.print("  ");
                                }else{
                                    System.out.print("🚪");
                                }
                            }
                    }
                }else if (this.cells[j][i] instanceof Room){
                    if(j-1 > 0 && this.cells[j-1][i] instanceof Street){
                        //System.out.print("🚪");
                    	System.out.print(" ");
                    }
                    if (this.cells[j][i].hasPlayer()) {
                        if(!this.cells[j][i].hasZombie()){
                            if(j-1< 0){
                                System.out.print("|🚶    ");
                                /*System.out.print("|");
                                for(int z=0;z<this.cells[j][i].players().size();z++){
                                    System.out.print(this.cells[j][i].players().get(z).symbole());
                                }
                                System.out.print("     ");*/
                                int k =0;
                                while (k<this.cells[j][i].doors().size() && !this.cells[j+1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                    k++;
                                }
                                if(k<this.cells[j][i].doors().size()){
                                    Door d = this.cells[j][i].doors().get(k);
                                    if(d.isOpen()){
                                        System.out.print("  ");
                                    }else{
                                        System.out.print("🚪");
                                    }
                                }
                            }else if (j+1>= this.width) {
                                /*int k =0;
                                while (k<this.cells[j][i].doors().size() && !this.cells[j-1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                    k++;
                                }
                                if(k<this.cells[j][i].doors().size()){
                                    Door d = this.cells[j][i].doors().get(k);
                                    if(d.isOpen()){
                                        System.out.print("   ");
                                    }else{
                                        System.out.print("🚪 ");
                                    }
                                }*/
                	            System.out.print(" 🚶   |");
                                /*System.out.print("  ");
                                for(int z=0;z<this.cells[j][i].players().size();z++){
                                    System.out.print(this.cells[j][i].players().get(z).symbole());
                                }
                                System.out.print("  |");*/
                            }else{
                                /*int k =0;
                                while (k<this.cells[j][i].doors().size() && !this.cells[j-1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                    k++;
                                }
                                if(k<this.cells[j][i].doors().size()){
                                    Door d = this.cells[j][i].doors().get(k);
                                    if(d.isOpen()){
                                        System.out.print("   ");
                                    }else{
                                        System.out.print("🚪 ");
                                    }
                                }*/
                                System.out.print("  🚶  ");
                                /*System.out.print(" ");
                                for(int z=0;z<this.cells[j][i].players().size();z++){
                                    System.out.print(this.cells[j][i].players().get(z).symbole());
                                }
                                System.out.print("  ");*/
                                int k =0;
                                while (k<this.cells[j][i].doors().size() && !this.cells[j+1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                    k++;
                                }
                                if(k<this.cells[j][i].doors().size()){
                                    Door d = this.cells[j][i].doors().get(k);
                                    if(d.isOpen()){
                                        System.out.print("  ");
                                    }else{
                                        System.out.print("🚪");
                                    }
                                }
                            }
                        }else{
                            if(j-1< 0){
                	            System.out.print("|🧟🚶 ");
                                /*System.out.print("|");
                                for(int z=0;z<this.cells[j][i].getZombies().size();z++){
                                    System.out.print(this.cells[j][i].getZombies().get(z).symbole());
                                }
                                for(int z=0;z<this.cells[j][i].players().size();z++){
                                    System.out.print(this.cells[j][i].players().get(z).symbole());
                                }
                                System.out.print("  ");*/
                                int k =0;
                                while (k<this.cells[j][i].doors().size() && !this.cells[j+1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                    k++;
                                }
                                if(k<this.cells[j][i].doors().size()){
                                    Door d = this.cells[j][i].doors().get(k);
                                    if(d.isOpen()){
                                        System.out.print("  ");
                                    }else{
                                        System.out.print("🚪");
                                    }
                                }
                            }else if(j+1>= this.width){
                	            System.out.print("  🧟🚶|");
                                /*System.out.print("  ");
                                for(int z=0;z<this.cells[j][i].getZombies().size();z++){
                                    System.out.print(this.cells[j][i].getZombies().get(z).symbole());
                                }
                                for(int z=0;z<this.cells[j][i].players().size();z++){
                                    System.out.print(this.cells[j][i].players().get(z).symbole());
                                }
                                System.out.print("|");*/
                            }else{
                                System.out.print("🧟🚶  ");
                                /*for(int z=0;z<this.cells[j][i].getZombies().size();z++){
                                    System.out.print(this.cells[j][i].getZombies().get(z).symbole());
                                }
                                for(int z=0;z<this.cells[j][i].players().size();z++){
                                    System.out.print(this.cells[j][i].players().get(z).symbole());
                                }
                                System.out.print("   ");*/
                                int k =0;
                                while (k<this.cells[j][i].doors().size() && !this.cells[j+1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                    k++;
                                }
                                if(k<this.cells[j][i].doors().size()){
                                    Door d = this.cells[j][i].doors().get(k);
                                    if(d.isOpen()){
                                        System.out.print("  ");
                                    }else{
                                        System.out.print("🚪");
                                    }
                                }
                            }
                        }
                	}else{
                        if(this.cells[j][i].hasZombie()){
                            if(j-1< 0){
                	            System.out.print("|🧟    ");
                                /*System.out.print("|");
                                for(int z=0;z<this.cells[j][i].getZombies().size();z++){
                                    System.out.print(this.cells[j][i].getZombies().get(z).symbole());
                                }
                                System.out.print("   ");*/
                                int k =0;
                                while (k<this.cells[j][i].doors().size() && !this.cells[j+1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                    k++;
                                }
                                if(k<this.cells[j][i].doors().size()){
                                    Door d = this.cells[j][i].doors().get(k);
                                    if(d.isOpen()){
                                        System.out.print("  ");
                                    }else{
                                        System.out.print("🚪");
                                    }
                                }
                            }else if(j+1>= this.width){
                	            System.out.print("   🧟 |");
                                /*System.out.print("   ");
                                for(int z=0;z<this.cells[j][i].getZombies().size();z++){
                                    System.out.print(this.cells[j][i].getZombies().get(z).symbole());
                                }
                                System.out.print("|");*/
                            }else{
                                System.out.print("  🧟  ");
                                /*System.out.print("   ");
                                for(int z=0;z<this.cells[j][i].getZombies().size();z++){
                                    System.out.print(this.cells[j][i].getZombies().get(z).symbole());
                                }*/
                                int k =0;
                                while (k<this.cells[j][i].doors().size() && !this.cells[j+1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                    k++;
                                }
                                if(k<this.cells[j][i].doors().size()){
                                    Door d = this.cells[j][i].doors().get(k);
                                    if(d.isOpen()){
                                        System.out.print("  ");
                                    }else{
                                        System.out.print("🚪");
                                    }
                                }
                            }
                        }else{
                            if(j-1< 0){
                                System.out.print("|      ");
                                int k =0;
                                while (k<this.cells[j][i].doors().size() && !this.cells[j+1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                    k++;
                                }
                                if(k<this.cells[j][i].doors().size()){
                                    Door d = this.cells[j][i].doors().get(k);
                                    if(d.isOpen()){
                                        System.out.print("  ");
                                    }else{
                                        System.out.print("🚪");
                                    }
                                }
                            }else if(j+1>= this.width){
                                System.out.print("      |");
                            }else{
                                System.out.print("      ");
                                int k =0;
                                while (k<this.cells[j][i].doors().size() && !this.cells[j+1][i].doors().contains(this.cells[j][i].doors().get(k))){
                                    k++;
                                }
                                if(k<this.cells[j][i].doors().size()){
                                    Door d = this.cells[j][i].doors().get(k);
                                    if(d.isOpen()){
                                        System.out.print("  ");
                                    }else{
                                        System.out.print("🚪");
                                    }
                                }
                            }
                        }
                    }
                } else if (this.cells[j][i] instanceof Street) {
                    /*if(this.cells[j-1][i] instanceof Street){
                                    for(int s=j; s>0;s--){
                                        if(this.cells[s][i] instanceof Street){
                                            System.out.print("");
                                            break;
                                        }
                                    }
                                }else{
                                    System.out.print("      ");
                                }*/
                	if(j-1>=0 && this.cells[j-1][i] instanceof Street && j+1< this.width && this.cells[j+1][i] instanceof Room){
                        System.out.print(" ");
                    }/*
                	else if(j+1< this.width && this.cells[j+1][i] instanceof Room){
                        System.out.print(" ");
                    }*/
                    Street street = (Street) this.cells[j][i];
                    if (street.hasSewer()) {
                        if(this.cells[j][i].hasPlayer()){
                            System.out.print("  🅩 🚶");
                        }else{
                            System.out.print("  🅩   ");
                        }
                    }else if(this.isCarrefour(j,i)){
                        if(!this.cells[j][i].hasZombie()){
                            if(this.cells[j][i].hasPlayer()){
                                System.out.print("   🅙   ");
                            }else{
                                System.out.print("   ⚪  ");
                            }
                        }else if(!this.cells[j][i].hasPlayer()){
                            System.out.print("   🅩   ");
                        }else{
                            /*for(int z=0;z<this.cells[j][i].players().size();z++){
                                System.out.print(this.cells[j][i].players().get(z).symbole());
                            }*/
                            System.out.print("🚶⚪🧟");
                            /*System.out.print("⚪");
                            for(int z=0;z<this.cells[j][i].getZombies().size();z++){
                                System.out.print(this.cells[j][i].getZombies().get(z).symbole());
                            }*/
                        }
                    } else if(this.cells[j][i].hasPlayer() ){
                        if(this.cells[j][i].hasZombie()){
                            System.out.print(" 🚶🧟 ");
                            /*for(int z=0;z<this.cells[j][i].players().size();z++){
                                System.out.print(this.cells[j][i].players().get(z).symbole());
                            }
                            for(int z=0;z<this.cells[j][i].getZombies().size();z++){
                                System.out.print(this.cells[j][i].getZombies().get(z).symbole());
                            }*/
                        }else{
                        	System.out.print("  🚶  ");
                            /*System.out.print("   ");
                            for(int z=0;z<this.cells[j][i].players().size();z++){
                                System.out.print(this.cells[j][i].players().get(z).symbole());
                            }*/
                        }
                    }else if(!this.cells[j][i].hasPlayer() ){
                        if(this.cells[j][i].hasZombie()){
                        	System.out.print("  🧟  ");
                            /*System.out.print("   ");
                            for(int z=0;z<this.cells[j][i].getZombies().size();z++){
                                System.out.print(this.cells[j][i].getZombies().get(z).symbole());
                            }*/
                        }else{
                            if(j-1 >= 0 && this.cells[j][i] instanceof Street && this.cells[j-1][i] instanceof Street){
                            	System.out.print("        "); 
                                
                            }else{
                                /*if(j== 0 && this.cells[j][i] instanceof Street && this.cells[j+1][i] instanceof Street){
                                    System.out.print("       ");
                                
                                }else{
                                    System.out.print("      ");
                                }*/
                            	System.out.print("      ");
                            }
                            
                        }
                    }
                }else {
                	System.out.print("      ");
                }
            }
            
            System.out.print("\n");
            for (int c = x; c < endx; c++) {
                if(this.isCarrefour(c,i) || this.cells[c][i] instanceof Street){
                    //System.out.print("        ");
                	if(c-1 >= 0 && this.cells[c][i] instanceof Street && this.cells[c-1][i] instanceof Street){
                        System.out.print("        ");                                
                    }else{
                        System.out.print("       ");
                    }
                }else if(i+1 < this.height){
                    int k = 0;
                    Door d;
                    while (k<this.cells[c][i].doors().size() && !this.cells[c][i+1].doors().contains(this.cells[c][i].doors().get(k))){
                        k++;
                    }
                    if(k<this.cells[c][i].doors().size()){
                        d = this.cells[c][i].doors().get(k);
                        if(d.isOpen()){
                            System.out.print("|__  __|");
                        }else{
                            System.out.print("|__🚪__|");
                        }
                    }
                    //System.out.print("|_  _|");
                }else{
                    System.out.print("|______|");
                }
            	
            }
            
            System.out.print("\n");
        }
        System.out.print("\n");
    }
    
    public void printCell(int x, int y){
        if(this.cells[x][y] instanceof Street){
            if(this.isCarrefour(x,y)){
                System.out.println(" |           ▋           | ");
                System.out.println("─┘                       └─");
            }else if(x+1< this.width && this.cells[x+1][y] instanceof Street){
                System.out.println(" ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ");
            }else if(y-1>= 0 && this.cells[x][y-1] instanceof Street){
                System.out.println("|             ▋            |");
            }else if(y-1<0){
                System.out.println("|⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺|");    
            }
        }else if(y-1<0){
            System.out.println("|⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺|");
        }else{
            int i = 0;
            Door d;
            if(y-1>= 0){
                while (i<this.cells[x][y].doors().size() && !this.cells[x][y-1].doors().contains(this.cells[x][y].doors().get(i))){
                    i++;
                }
                if(i<this.cells[x][y].doors().size()){
                    d = this.cells[x][y].doors().get(i);
                    if(d.isOpen()){
                        System.out.println("|------------  ------------|");
                    }else{
                        System.out.println("|------------🚪------------|");
                    }
                }
                
            }   
        }
        if(this.cells[x][y] instanceof Street){
            if(this.cells[x][y].hasPlayer()){
                System.out.print(" ");
                for(int i=0;i<this.cells[x][y].players().size();i++){
                    System.out.print(this.cells[x][y].players().get(i).symbole());
                }
                System.out.println("");
            }
            Street street = (Street) this.cells[x][y];
            if (street.hasSewer()) {
                System.out.println("            🕳️         ");
                System.out.println("");
            }else if(this.isCarrefour(x,y)){
                System.out.println("            ⚪         ");
            }else if(x+1< this.width && this.cells[x+1][y] instanceof Street){
                System.out.println("   ▀▀▀   ▀▀▀   ▀▀▀   ");
                System.out.println("");
            }
            if(this.cells[x][y].hasZombie()){
                System.out.print(" ");
                for(int i=0;i<this.cells[x][y].getZombies().size();i++){
                    System.out.print(this.cells[x][y].getZombies().get(i).symbole());
                }
                System.out.println("");
            }
        }else if (this.cells[x][y] instanceof Pharmacy ) {
            System.out.println("     ℙ ℍ 𝔸 ℝ 𝕄 𝔸 ℂ 𝕀 𝔼     ");
            if(this.cells[x][y].hasEquipment()){
                System.out.print(" ");
                for(int i=0;i<this.cells[x][y].equipments().size();i++){
                    System.out.print(this.cells[x][y].equipments().get(i).symbole());
                }
                System.out.println("");
            }
            if (this.cells[x][y].hasPlayer()) {
            	System.out.print("   ");
            	for(int i=0;i<this.cells[x][y].players().size();i++){
                    System.out.print(this.cells[x][y].players().get(i).symbole());
                }
            	System.out.println("");
                //Pharmacy pharmacy = (Pharmacy) this.cells[x][y];
                //pharmacy.regenerateMed();
                
            }
            if(x-1>= 0){
                int i =0;
                while (i<this.cells[x][y].doors().size() && !this.cells[x-1][y].doors().contains(this.cells[x][y].doors().get(i))){
                    i++;
                }
                if(i<this.cells[x][y].doors().size()){
                    Door d = this.cells[x][y].doors().get(i);
                    if(d.isOpen()){
                        System.out.print("   ");
                    }else{
                        System.out.print("🚪 ");
                    }
                }
                
            }
            //System.out.print("🚪 ");
            
            if(x+1<this.width){
                int i =0;
                while (i<this.cells[x][y].doors().size() && !this.cells[x+1][y].doors().contains(this.cells[x][y].doors().get(i))){
                    i++;
                }
                if(i<this.cells[x][y].doors().size()){
                    Door d = this.cells[x][y].doors().get(i);
                    if(d.isOpen()){
                        System.out.println("                    ");
                    }else{
                        System.out.println("                        🚪");
                    }
                }
                
            }else{
                System.out.println("");
            }
            if(this.cells[x][y].hasZombie()){
                System.out.print("   ");
                for(int i=0;i<this.cells[x][y].getZombies().size();i++){
                    System.out.print(this.cells[x][y].getZombies().get(i).symbole());
                }
                System.out.println("");
            }else {
            	System.out.println("");
            }
            System.out.println("");
        }else if (this.cells[x][y] instanceof Hotel) {
            System.out.println("         ℍ 𝕆 𝕋 𝔼 𝕝      ");
            if(this.cells[x][y].hasEquipment()){
                System.out.print(" ");
                for(int i=0;i<this.cells[x][y].equipments().size();i++){
                    System.out.print(this.cells[x][y].equipments().get(i).symbole());
                }
                System.out.println("");
            }
            if(x-1>= 0){
                int i =0;
                while (i<this.cells[x][y].doors().size() && !this.cells[x-1][y].doors().contains(this.cells[x][y].doors().get(i))){
                    i++;
                }
                if(i<this.cells[x][y].doors().size()){
                    Door d = this.cells[x][y].doors().get(i);
                    if(d.isOpen()){
                        System.out.print("   ");
                    }else{
                        System.out.print("🚪 ");
                    }
                }
                
            }
            //System.out.print("🚪 ");
            if (this.cells[x][y].hasPlayer()) {
                System.out.print(" "+this.cells[x][y].players().get(0).symbole()+" ");
            }
            if(x+1<this.width){
                int i =0;
                while (i<this.cells[x][y].doors().size() && !this.cells[x+1][y].doors().contains(this.cells[x][y].doors().get(i))){
                    i++;
                }
                if(i<this.cells[x][y].doors().size()){
                    Door d = this.cells[x][y].doors().get(i);
                    if(d.isOpen()){
                        System.out.println("                     ");
                    }else{
                        System.out.println("                   🚪");
                    }
                }
                
            }
            //System.out.println("                 🚪");
            //System.out.println("");
            System.out.println("");
        }else if (this.cells[x][y] instanceof Room){
            if(this.cells[x][y].hasEquipment()){
                //System.out.print("| ");
            	System.out.print("   ");
                for(int i=0;i<this.cells[x][y].equipments().size();i++){
                    System.out.print(this.cells[x][y].equipments().get(i).symbole());
                }
                //System.out.println("| ");
                System.out.println("");
            }else{
                System.out.println("");
            }
            if (this.cells[x][y].hasPlayer()) {
            	System.out.print("  ");
            	for(int i=0;i<this.cells[x][y].players().size();i++){
                    System.out.print(this.cells[x][y].players().get(i).symbole()+" ");
                }
            	System.out.println("");
                
            }
            if(x-1>= 0){
                int i =0;
                while (i<this.cells[x][y].doors().size() && !this.cells[x-1][y].doors().contains(this.cells[x][y].doors().get(i))){
                    i++;
                }
                if(i<this.cells[x][y].doors().size()){
                    Door d = this.cells[x][y].doors().get(i);
                    if(d.isOpen()){
                        System.out.print("   ");
                    }else{
                        System.out.print("🚪 ");
                    }
                }
                
            }
            //System.out.print("🚪 ");
            
            if(x+1<this.width){
                int i =0;
                while (i<this.cells[x][y].doors().size() && !this.cells[x+1][y].doors().contains(this.cells[x][y].doors().get(i))){
                    i++;
                }
                if(i<this.cells[x][y].doors().size()){
                    Door d = this.cells[x][y].doors().get(i);
                    if(d.isOpen()){
                        System.out.println("                     ");
                    }else{
                        System.out.println("                        🚪");
                    }
                }
                
            }
            //System.out.print("            🚪");
            //System.out.println("");
            System.out.println("");
            if(this.cells[x][y].hasZombie()){
            	System.out.print("  ");
                //System.out.print("| ");
                for(int i=0;i<this.cells[x][y].getZombies().size();i++){
                    System.out.print(this.cells[x][y].getZombies().get(i).symbole());
                }
                //System.out.println("                     |");
                System.out.println("");
            }else{
                System.out.println("");
            }
            
        }
        
        if(this.isCarrefour(x,y)){
            System.out.println("─┐                       ┌─");
            System.out.println(" |           ▋           | ");
        }else if(this.cells[x][y] instanceof Street && x+1< this.width && this.cells[x+1][y] instanceof Street){
            System.out.println(" ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ");
        }else if(this.cells[x][y] instanceof Street && y+1< this.height && this.cells[x][y+1] instanceof Street){
            System.out.println("|             ▋            | ");
        }else if(this.cells[x][y] instanceof Street && y+1>= this.height){
            System.out.println("|             ▋            | ");
            System.out.println("");
            System.out.println(" ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺  ");
        }else if(y+1<this.height){
            int i = 0;
            Door d;
            while (i<this.cells[x][y].doors().size() && !this.cells[x][y+1].doors().contains(this.cells[x][y].doors().get(i))){
                i++;
            }
            if(i<this.cells[x][y].doors().size()){
                d = this.cells[x][y].doors().get(i);
                if(d.isOpen()){
                    System.out.println("|____________  ____________|");
                }else{
                    System.out.println("|____________🚪____________|");
                }
            }
            
        }else{
            System.out.println("|__________________________|");
        }
        System.out.println("");
    }

    public void CurrentCell(int x, int y){
        if(this.cells[x][y] instanceof Street){
            Street street = (Street) this.cells[x][y];
            if(this.isCarrefour(x,y)){
                System.out.println(" |          ▋          | ");
                System.out.println("─┘                     └─");
                System.out.println("         🚶              ");
                System.out.println("           ⚪            ");
                System.out.println("");
                System.out.println("─┐                     ┌─");
                System.out.println(" |          ▋          | ");
            }else if (street.hasSewer()) {
                if(y-1<= 0){
                    System.out.println("| ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ |");
                    System.out.println("|            🕳️           |");
                    System.out.println("|    🚶                   |");
                }else if(y+1>= this.height){
                    System.out.println("|    🚶                   |");
                    System.out.println("|            🕳️           |");
                    System.out.println("|                         |");
                    System.out.println(" ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺  ");
                }else if(x+1>= this.width){
                    System.out.println("                          |");
                    System.out.println("             🕳️           |");
                    System.out.println("     🚶                   |");
                }else if(x-1<= 0){
                    System.out.println("|                          ");
                    System.out.println("|            🕳️            "); 
                    System.out.println("|                          ");                       System.out.println("|                     🚶   ");
                }else{
                    System.out.println("                          ");
                    System.out.println("             🕳️           ");
                    System.out.println(" 🚶                       ");
                }
            }else if(x+1<= this.width && this.cells[x+1][y] instanceof Street){
                System.out.println(" ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ");
                System.out.println("         🚶              ");
                System.out.println("   ▀▀▀   ▀▀▀   ▀▀▀   ▀▀▀ ");
                System.out.println("");
                System.out.println(" ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ");
            }else if(x+1>= this.width){
                System.out.println(" ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ |");
                System.out.println("         🚶              |");
                System.out.println("   ▀▀▀   ▀▀▀   ▀▀▀   ▀▀▀ |");
                System.out.println("                         |");
                System.out.println(" ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺  ");
            }else if(x-1<= 0){
                System.out.println("| ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ");
                System.out.println("|         🚶              ");
                System.out.println("|   ▀▀▀   ▀▀▀   ▀▀▀   ▀▀▀ ");
                System.out.println("|                         ");
                System.out.println("| ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ");
            }else if(y-1>= 0 && this.cells[x][y-1] instanceof Street || y+1<this.height && this.cells[x][y+1] instanceof Street){
                System.out.println("|            ▋           |");
                System.out.println("|         🚶             |");
                System.out.println("|            ▋           |");
            }else if(y-1<=0){
                System.out.println("|⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺|"); 
                System.out.println("|            ▋           |");
                System.out.println("|         🚶             |");
                System.out.println("|            ▋           |");
                
            }else if(y+1>= this.height){
                System.out.println("|            ▋           |");
                System.out.println("|       🚶               |");
                System.out.println("|            ▋           |");
                System.out.println("|                        |");
                System.out.println(" ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺ ⎺  ");
            }
        }else if (this.cells[x][y] instanceof Room){
            System.out.println("|⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺⎺|");
            System.out.println("|                          |");
            System.out.println("|         🚶               |");
            System.out.println("|                          |");
            System.out.println("|__________________________|");
        }
        
    }
    
    public void printBoardMap() {
        /*for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = cells[i][j];
                if (cell instanceof Room) {
                    Room room = (Room) cell;
                    System.out.println("Room at: (" + room.getCoordinateX() + ", " + room.getCoordinateY() + ")");
                    Map<Integer, Door> roomDoors = room.getDoors();
                    if (roomDoors.isEmpty()) {
                        System.out.println("    No doors in this room.");
                    } else {
                        roomDoors.forEach((doorNumber, door) -> {
                            System.out.println("    Door #" + doorNumber + " in Room at: (" + room.getCoordinateX() + ", " + room.getCoordinateY() + ")");
                        });
                    }
                }
            }
        }*/
    	
    	/*for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Cell cell = cells[i][j];
                if (cell instanceof Room) {
                    Room room = (Room) cell;
                    System.out.println("Room at: (" + room.getCoordinateX() + ", " + room.getCoordinateY() + ")");
                    List<Door> roomDoors = room.getDoors();
                    if (roomDoors.isEmpty()) {
                        System.out.println("    No doors in this room.");
                    } else {
                        roomDoors.forEach((door) -> {
                            System.out.print("    \u001B[31mD" + door.getDoorNbr() + "\u001B[0m-> ");
                            System.out.print("\u001B[34m"+this.getDirectionDoor(door) + "\u001B[0m ");
                            System.out.print(this.getAdjacentCell(door).toString());
                        });
                        System.out.println("");
                    }
                }
            }
        }*/
    }
    
    public Door getDoor(int x, int y, String direction){
        if (direction.equals("right")){
        	if(x+1<this.width) {
                int i =0;
                while (i<this.cells[x][y].doors().size() && !this.cells[x+1][y].doors().contains(this.cells[x][y].doors().get(i))){
                    i++;
                }
                if(i<this.cells[x][y].doors().size()){
                    return this.cells[x][y].doors().get(i);
                }
        	}else {
        		//System.out.println("No door in this direction");
        	}
        } else if (direction.equals("left")){ 
        	if(x-1>=0) {
                int i =0;
                while (i<this.cells[x][y].doors().size() && !this.cells[x-1][y].doors().contains(this.cells[x][y].doors().get(i))){
                    i++;
                }
                if(i<this.cells[x][y].doors().size()){
                    return this.cells[x][y].doors().get(i);
                }
        	}else {
        		//System.out.println("No door in this direction");
        	}
        }else if ( direction.equals("up")){
        	if(y-1>=0) {
                int i =0;
                while (i<this.cells[x][y].doors().size() && !this.cells[x][y-1].doors().contains(this.cells[x][y].doors().get(i))){
                    i++;
                }
                if(i<this.cells[x][y].doors().size()){
                    return this.cells[x][y].doors().get(i);
                }
        	}else {
        		//System.out.println("No door in this direction");
        	}
        }else {
        	if(y+1<this.height) {
                int i =0;
                while (i<this.cells[x][y].doors().size() && !this.cells[x][y+1].doors().contains(this.cells[x][y].doors().get(i))){
                    i++;
                }
                if(i<this.cells[x][y].doors().size()){
                    return this.cells[x][y].doors().get(i);
                }
        	}else {
        		//System.out.println("No door in this direction");
        	}
        }
        return  null;
    }
    
    public String getDirectionDoor(Door door){
        if(door.getDoorNbr() == 1){
            if(door.getRoom().getCoordinateX() + 1 < this.width){
                return "E";
            }else if(door.getRoom().getCoordinateY() + 1 < this.height){
                return "S";
            }else if(door.getRoom().getCoordinateX() - 1 >= 0){
                return "W";
            }
        }else if(door.getDoorNbr() == 2){
            if(door.getRoom().getCoordinateY() + 1 < this.height){
                return "S";
            }else if(door.getRoom().getCoordinateX() - 1 >= 0){
                return "W";
            }else if(door.getRoom().getCoordinateY() - 1 >= 0){
                return "N";
            }
        }else if(door.getDoorNbr() == 3){
            if(door.getRoom().getCoordinateX() - 1 >= 0){
                return "W";
            }else if(door.getRoom().getCoordinateY() - 1 >= 0){
                return "N";
            }
        }else if(door.getDoorNbr() == 4){
            if(door.getRoom().getCoordinateY() - 1 >= 0){
                return "N";
            }else if(door.getRoom().getCoordinateY() + 1 < this.height){
                return "S";
            }
        }
        return "!";
    }
    public void ZombieMove(){
        
    } 
    public boolean move(String direction,Player player) {
        
        // getting the coordinates of the cell 
        int x = player.getPlayerCell().getCoordinateX();
        int y = player.getPlayerCell().getCoordinateY();
        Cell cell = this.cells[x][y];
        if (direction.equals("right")){
        	if(x+1<this.width) {
        		cell = this.cells[x+1][y];
        		this.cells[x][y].removePlayer(player);
        		cell.setPlayer(player);
            	player.setCell(cell);
        	}else {
        		System.out.println("The player cannot move to this direction");
        		return false;
        	}
        	
        } else if (direction.equals("left")){ 
        	if(x-1>=0) {
        		cell = this.cells[x-1][y];
        		this.cells[x][y].removePlayer(player);
        		cell.setPlayer(player);
            	player.setCell(cell);
        	}else {
        		System.out.println("The player cannot move to this direction");
        		return false;
        	}
        }else if ( direction.equals("up")){
        	if(y-1>=0) {
        		cell = this.cells[x][y-1];
        		this.cells[x][y].removePlayer(player);
        		cell.setPlayer(player);
            	player.setCell(cell);
        	}else {
        		System.out.println("The player cannot move to this direction");
        		return false;
        	}
        }else {
        	if(y+1<this.height) {
        		cell = this.cells[x][y+1];
        		this.cells[x][y].removePlayer(player);
        		cell.setPlayer(player);
            	player.setCell(cell);
        	}else {
        		System.out.println("The player cannot move to this direction");
        		return false;
        	}
        }
		System.out.println("\u001B[31m"+"The player moves "+direction+"\u001B[0m");
		if(cell instanceof Pharmacy) {
			Pharmacy p = (Pharmacy)cell;
			for(Equipment equip : p.equipments()) {
				if(equip instanceof HealVial) {
					return true;
				}
			}
			p.regenerateMed();
		}
		return true;
    }
    /**
     * a method that sets a zombie in a cell of the board
     * @param zombie represents a zombie
     * @param x Coordinate X of the cell
     * @param y Coordinate Y of the cell
     */
    public void setZombie(Zombie zombie,int x,int y){
        if ((this.cells[x][y] instanceof Room) ||(this.cells[x][y] instanceof Street)) {
            this.cells[x][y].setZombie(zombie);    
        }
        
    }
    
    /**
     * calculates the total number of zombies passing through the sewers .
     * @return the total number of zombies passing through the sewers .
     */
    public int totalZombie(){
        int total = 0;
        for(Sewer sewer : this.sewers){
            total += sewer.getZombPasses();
        }
        return total;
    }
    
    /**
     * Sets a player on the board at the specified coordinates. 
     * @param p The player to be set.
     * @param x The x-coordinate of the cell to place the player.
     * @param y The y-coordinate of the cell to place the player.
     */
    public void setPlayer(Player p, int x, int y){
    	this.cells[x][y].setPlayer(p); 
    }


    /*public void printPlayerPositions() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = cells[j][i];
                if (cell.getPlayer() != null) {
                    String role = "";
                    if (cell.getPlayer() instanceof HealerRole) {
                        role += "Healer ";
                    }
                    if (cell.getPlayer() instanceof LuckyRole) {
                        role += "Lucky ";
                    }
                    if (cell.getPlayer() instanceof FighterRole) {
                        role += "Fighter ";
                    }
                    if (cell.getPlayer() instanceof NosyRole) {
                        role += "Nosy ";
                    }
                    // Imprimez les coordonnées de la cellule et le rôle du joueur.
                    System.out.println(role + "Player at: (" + j + ", " + i + ")");
                }
            }
        }
    }*/

    // prints the positions of zombies on the board
    public void printZombiePositions() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = cells[j][i];
                if (cell.hasZombie()) {
                    // Imprimez les coordonnées de la cellule où se trouve le zombie.
                    System.out.println("Zombie at: (" + j + ", " + i + ")");
                }
            }
        }
    }
    /**
     * Searches for noise sources on the board and returns their positions.
     * @return a list of cells representing the noise sources on the board.
     */
    public List<Cell> noiseOrigin(){
        // a list to store the noise sources
        List<Cell> noiseOrigin = new ArrayList<>();
        // searching process by checking each cell of the board 
        for ( int i = 0 ; i < height ; i++){
            for ( int j = 0 ; j< width ; j++){
                Cell cell  = cells[j][i];
                // Checks if the sound level of the cell is 1 or higher meaning that there a noise source in this cell
                if ( cell.getnoiseLevel() >= 1){
                    noiseOrigin.add(cell);
                }
            }
        }
        return noiseOrigin;
    }
    /**
     * Checks if the specified cell is valid whithin the board
     * A cell is considered valid if it's coordinates are whitin the height / width bounderies 
     * @param x the x coordinate of the cell.
     * @param y the y coordinate of the cell.    
     * @return true if the cell is valid, otherwise false.
     */
    public boolean validCell(int x, int y){
        return x>= 0 && x < height && y >= 0 && y < width ;
    }
    
    /**
     * function that verifies if the function has a noiseLevel 
     * @param x The x-coordinate of the cell.
     * @param y the y-coordinate of the cell.
     * @return true if the cell has a noise level higher than zero, otherwise false 
     */
    public boolean alreadyhasNoise(int x, int y){
        return validCell(x, y) && this.cells[x][y].getnoiseLevel() > 0;
    }


     // Propagation of the sound level from the 
     // method that fills the serounding cells with a less intense sound level
     // coming from the original cell 
     
    public void spreadNoise(){
        List<Cell> originCell = noiseOrigin();
        for (Cell cell : originCell ){
            int x = cell.getCoordinateY();
            int y = cell.getCoordinateX();
            int n = cell.getnoiseLevel();
            if ( n == 1 ){
                System.out.println(" le son ne se disperse pas ");

            }else{ 
                int j = 1;
                while ( n >= 1){
                    n = n - 1;
                    int length = 3;
                    for ( int i =0 ; i < length ; i++ ){
                        if (validCell(x-j, y-j+i)){
                            if (!alreadyhasNoise(x-j, y-j+i)) {
                            this.cells[x-j][y-j+i].setnoiseLevel(n);
                            } else {
                                int localnoise = this.cells[x-j][y-j+i].getnoiseLevel();
                                // if the cell has already a sound coming from another source the highest sound will be taken 
                                this.cells[x-j][y-j+i].setnoiseLevel(Math.max(n, localnoise));
                            }
                        }
                        if (validCell(x+j, y-j+i)){
                            if (!alreadyhasNoise(x+j, y-j+i)){
                            this.cells[x+j][y-j+i].setnoiseLevel(n);
                            } else {
                                int localnoise = this.cells[x+i][y+i].getnoiseLevel();
                                this.cells[x+j][y-j+i].setnoiseLevel(Math.max(n, localnoise));
                            }
                        }
                        if (validCell(x-j+i,y-j)){
                            if (!alreadyhasNoise(x-j+i, y-j)){
                            this.cells[x-j+i][y-j].setnoiseLevel(n);
                            }else {
                                int localnoise = this.cells[x-j+i][y-j].getnoiseLevel();
                                this.cells[x-j+i][y-j].setnoiseLevel(Math.max(n, localnoise));
                            }
                        } 
                        if (validCell(x-j+i,y+j)){
                            if (!alreadyhasNoise(x-j+i, y+j)) {
                            this.cells[x-j+i][y+j].setnoiseLevel(n);
                            } else {
                                int localnoise = this.cells[x-j+i][y+j].getnoiseLevel();
                                this.cells[x-j+i][y+j].setnoiseLevel(Math.max(n, localnoise));
                            }                         
                        }
                    }
                    length = length + 2;
                    j+= 1 ;


                }
            }
        }
    }
     /**
     *  method that returns an array that contains the loudest cells of the board 
     * @return highest an array
     */
    public List<Cell> highestNoiseOrigin(){
        List<Cell> noiseOrigin = noiseOrigin();
        List<Cell> highest = new ArrayList<>();
        int maxNoiseLevel = 0;
        for ( Cell cell: noiseOrigin){
            int noiseLevel = cell.getnoiseLevel();
            if ( noiseLevel > maxNoiseLevel){
                maxNoiseLevel = noiseLevel;
            }
        }
        for(Cell cell : noiseOrigin){
            if(cell.getnoiseLevel()==maxNoiseLevel){
                highest.add(cell);
            }
        }
        return highest;


    }
    
    /**
     * method that counts the distance between two cells 
     * @param pos1 the first position
     * @param pos2 the second position 
     * @return the distance between the two Cells 
     */
    public double distance(Cell pos1, Cell pos2){
        int x = pos1.getCoordinateX()- pos2.getCoordinateX();
        int y = pos1.getCoordinateY() - pos2.getCoordinateY();
        return Math.sqrt(x*x + y*y);

    }

    /**
     * function that returns the nearestCell of the highest noiseSource 
     * @param z the zombie 
     * @return nearCell the nearest Highest noiseSource
     */ 
    public Cell nearSource(Zombie z){
        Cell zombiePos = z.getCellPosition();
        List<Cell> highest = highestNoiseOrigin();
        if(highest.isEmpty()){
            return null;
        }
        Cell nearCell = highest.get(0);
        double distanceMinimum= distance(zombiePos, nearCell);
        for(Cell cell : highest){
            double distance = distance( cell, zombiePos);
            if ( distance < distanceMinimum){
                distanceMinimum =distance;
                nearCell = cell ; 
            }
        }
        return nearCell;


    }

    /**
     * get the cell that's neighbouring the current cell
     * @param cell current cell
     * @param direc the direction 
     * @return 
     */
    public Cell getCellBydirection(Cell cell, String direc){
        int x = cell.getCoordinateX();
        int y = cell.getCoordinateY();
        if (direc.equals("up")){
            if (y-1>=0){
                return this.cells[x][y-1];
            }
        } else if(direc.equals("down")){
            if (y+1<height){
                return this.cells[x][y+1];
            }
        } else if (direc.equals("left")){
            if (x-1>=0){
                return this.cells[x-1][y];
            }
        } else if(direc.equals("right")){
            if (x+1<width){
                return this.cells[x+1][y];
            }
        }
        return null;
    }
        
    /**
     * zombie follows in a stupid way the loudest noise and moves by one cell
     * @param z 
     */ 
    public void moveZombietoLoudestCell(Zombie z){
        Cell currentCell =z.getCellPosition() ;
        Cell loudestCell=nearSource(z);
        if(loudestCell != null){
        	int currentX =currentCell.getCoordinateX() ;
            int currentY=currentCell.getCoordinateY();
            int loudestX= loudestCell.getCoordinateX( );
            int loudestY= loudestCell.getCoordinateY(); 
            List<String> dirc=new ArrayList<>();
            String direction ;
            // moving horizentally either left or right 
            if(currentX<loudestX){
                direction="right";
                dirc.add(direction);
            }else if (currentX > loudestX){
                direction = "left";
                dirc.add(direction); }
            // moving vertically either up or down 
            if (currentY<loudestY){
                direction="down";
                dirc.add(direction);
            } 
            else if(currentY>loudestY){
                direction= "up";
                dirc.add(direction);
            } 
            else{
                System.out.println("\u001B[36m"+"The zombie is already at the loudest cell."+ "\u001B[0m ");
                return;}
            // choosing randomally either a vertical or a horizental direction 
            Random random =new Random();
            String randomDirection=dirc.get(random.nextInt(dirc.size() ));
            Cell nextCell =getCellBydirection(currentCell,randomDirection);
            if (nextCell instanceof Street){
                currentCell.removeZombie(z); 
                nextCell.setZombie(z);
                z.setCellPosition(nextCell); 
                System.out.println(z.getType()+" moves "+randomDirection);
            } 
            else{
            	Door d = this.getDoor(nextCell.getCoordinateX(), nextCell.getCoordinateY(), randomDirection);
                if (d.isOpen()){
                    currentCell.removeZombie(z); 
                    nextCell.setZombie(z);
                    z.setCellPosition(nextCell); 
                    System.out.println(z.getType()+" moves into a louder cell");
                } 
                else{
                    System.out.println("The door is closed. "+ z.getType()+ " can't move ");
                }
            }
        }else{
            Random random =new Random();
            String[] dir = {"up","right", "down", "left"};
            String d = "";
            Cell nextCell = null;
            while (nextCell == null){
                int index = random.nextInt(4);
                d = dir[index];
                nextCell = getCellBydirection(currentCell,d);
            }
            if(nextCell instanceof Street){
                currentCell.removeZombie(z); 
                nextCell.setZombie(z);
                z.setCellPosition(nextCell); 
                System.out.println(z.getType()+" moves "+ d);
            }else{
                Door door = this.getDoor(currentCell.getCoordinateX(), currentCell.getCoordinateY(), d);
                if (door.isOpen()){
                    currentCell.removeZombie(z); 
                    nextCell.setZombie(z);
                    z.setCellPosition(nextCell); 
                    System.out.println(z.getType()+" moves "+ d);
                } 
                else{
                    System.out.println("The door is closed. "+z.getType()+" can't move ");
                }
            }
        }
        
        
   
    }

    // method that returns the number of zombies supposed to come out of the sewer
    public int sewerZombie(List<Player> players){
        int cmp =0 ;
        int total =0;
        for( Player player : players){
            cmp += player.getExpertiseLevel();
            total +=1; 
        }
        if(cmp != 0) {
        	int expmoy =(cmp/total);
            int expmoy3 = (expmoy/3);
            int nbzombie = Math.round(expmoy3);
            return nbzombie ;
        }else {
        	return 0;
        }

    }
    // places random equipment on the cells of the board
    public void sitEquipments(){
    	for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
int randomIndex = random.nextInt(4 - 1 + 1) + 1;

            	for(int k=0 ; k<randomIndex; k++) {
            		Equipment equipment = chooseEquip();
                    if(equipment != null){
                        cells[i][j].addEquipment(equipment);
                    }
            	}
            }
        }
    }

    /**
     * chooses a random equipment to place on a cell
     * @return The chosen equipment.
     */
    public Equipment chooseEquip(){
        String[] equipment = {"FirstAidKit","Glasses","HealVial","Key","Map","Weapon"};
        int randomIndex = random.nextInt(6);
        String equip = equipment[randomIndex];
        if(equip.equals("FirstAidKit")){
            return new FirstAidKit();
        }else if(equip.equals("Glasses")){
            return new Glasses(this);
        }else if(equip.equals("HealVial")){
            return new HealVial();
        }else if(equip.equals("Key")){
            return new Key();
        }else if(equip.equals("Map")){
            return new Equipments.Map();
        }else if(equip.equals("Weapon")){
            WeaponType[] weaponTypes = WeaponType.values();
            int randomId = random.nextInt(weaponTypes.length);
        	WeaponType weaponType = weaponTypes[randomId];
        	return new Weapon(weaponType);
        }
        return null;
    }
    

}
