//globally
//declare the min and max variables that you need in parseInfo
float minX, maxX;
float minY, maxY;
int totalCount; // total number of places
int minPopulation, maxPopulation;
int minSurface, maxSurface;
int minAltitude, maxAltitude;
float minDensity, maxDensity;
//declare the variables corresponding to the column ids for x and y
//int x = 1;
//int y = 2;

// and the tables in which the city coordinates will be stored
//float xList[];
//float yList[];
City[] cities;

int minPopulationToDisplay = 10000;
int maxPopulationToDisplay = 2200000;  // max par défaut

// Deux poignées indépendantes
boolean draggingMin = false;
boolean draggingMax = false;

City lastPickedCity = null;


int   sliderX      = 20;
int   sliderY      = 55;
int   sliderW      = 760;
int   sliderH      = 16;
int sliderOffset = 100;

// Pan et zoom
float zoomFactor  = 1.0;    // niveau de zoom courant
float panX        = 0.0;    // décalage horizontal
float panY        = 0.0;    // décalage vertical
boolean panning   = false;  // est-ce qu'on est en train de panner ?
float lastMouseX, lastMouseY;  // position souris au début du pan


City selectedCity = null;   // variable globale


void setup() {
  size(800,700);
  readData();
}

void draw(){
  background(255);
  
  drawSlider();
  
  stroke(200);
  strokeWeight(1);
  line(0, sliderOffset - 5, width, sliderOffset - 5);
  noStroke();
       
  // Applique pan + zoom avant de dessiner les villes
  pushMatrix();                    // sauvegarde la transformation courante
  translate(panX, panY);           // décalage pan
  scale(zoomFactor);               // facteur de zoom
  
  
  for (int i = 0 ; i < totalCount ; i++)
    if (cities[i].population > minPopulationToDisplay && cities[i].population <= maxPopulationToDisplay) {
       cities[i].draw();
    }
    
    popMatrix(); // restaure la transformation d'origine
    
    if (selectedCity != null) {
      drawInfoPanel(selectedCity);   // affiché par-dessus la carte
    }
    
    drawLegend();
  
    // Affiche le niveau de zoom (hors transformation)
    fill(0);
    noStroke();
    textSize(11);
    textAlign(RIGHT, TOP);
    text("Zoom : " + nf(zoomFactor, 1, 1) + "x", width - 10, sliderOffset);
}

void readData() {
  String[] lines = loadStrings("./villes.tsv");
  parseInfo(lines[0]);
  
  //xList = new float[totalCount];
  //yList = new float[totalCount];
    cities = new City[totalCount];
    
  for (int i = 2 ; i < lines.length ; ++i) {
    String[] col = split(lines[i], TAB);
    //xList[i-2] = float (columns[1]);
    //yList[i-2] = float (columns[2]);
    
    int    postalCode  = int(col[0]);
    float  rawX        = float(col[1]);
    float  rawY        = float(col[2]);
    String name        = col[4];
    float  population  = float(col[5]);
    float  surface     = float(col[6]);
    float  altitude    = float(col[7]);
    
   float screenX = mapX(rawX);
   float screenY = mapY(rawY);
    
    cities[i - 2] = new City(postalCode, name, screenX, screenY, population, surface, altitude);
  }
  
  minDensity = cities[0].density;
  maxDensity = cities[0].density;
  for (int i = 1; i < totalCount; i++) {
    if (cities[i].density < minDensity) minDensity = cities[i].density;
    if (cities[i].density > maxDensity) maxDensity = cities[i].density;
  }
  
}

float mapX(float x) {
  return map(x, minX, maxX, 10, width - 10);
}

float mapY(float y) {
  return map(y, minY, maxY, height - 10, sliderOffset);
}

void parseInfo(String line) {
  String infoString = line.substring(2); // remove the #
  String[] infoPieces = split(infoString, ',');
  totalCount = int(infoPieces[0]);
  minX = float(infoPieces[1]);
  maxX = float(infoPieces[2]);
  minY = float(infoPieces[3]);
  maxY = float(infoPieces[4]);
  minPopulation = int(infoPieces[5]);
  maxPopulation = int(infoPieces[6]);
  minSurface = int(infoPieces[7]);
  maxSurface = int(infoPieces[8]);
  minAltitude = int(infoPieces[9]);
  maxAltitude = int(infoPieces[10]);
}

void keyPressed() {
  if (key == '+' || key == '=') {
    minPopulationToDisplay = minPopulationToDisplay * 2;
  } else if (key == '-') {
    minPopulationToDisplay = max(1, minPopulationToDisplay / 2);
  } else if (key == '*') {
    maxPopulationToDisplay = min(maxPopulation, maxPopulationToDisplay * 2);
  } else if (key == '/') {
    maxPopulationToDisplay = max(minPopulationToDisplay + 1, maxPopulationToDisplay / 2);
  }
  if (key == 'r' || key == 'R') {
    zoomFactor = 1.0;
    panX       = 0.0;
    panY       = 0.0;
  }
  redraw();
}

void mouseMoved() {
  //println("x = " + mouseX + "  |  y = " + mouseY);
  int worldX = (int) ((mouseX - panX) / zoomFactor);
  int worldY = (int) ((mouseY - panY) / zoomFactor);
  
  City picked = pick(worldX, worldY);

  if (picked != lastPickedCity) {
    if (lastPickedCity != null) {
      lastPickedCity.highlighted = false;
    }
    
    if (picked != null) {
      picked.highlighted = true;
      println("Ville : " + picked.name);
    }
  
    lastPickedCity = picked;
    redraw();
  }
}

City pick(int px, int py) {
  for (int i = totalCount - 1; i >= 0; i--) {

    if (cities[i].population <= minPopulationToDisplay) continue;

    if (cities[i].contains(px, py)) {
      return cities[i];
    }
  }
  return null;
}

void mousePressed() {
  // Clic droit -> début du pan
  if (mouseButton == RIGHT) {
    panning    = true;
    lastMouseX = mouseX;
    lastMouseY = mouseY;
    return;
  }

  // Clic gauche -> slider ou sélection ville
    float logMin   = log(1);
    float logMax   = log(maxPopulation);
    
    float handleMinX = map(log(max(1, minPopulationToDisplay)),
                         logMin, logMax, sliderX, sliderX + sliderW);
    float handleMaxX = map(log(max(1, maxPopulationToDisplay)),
                           logMin, logMax, sliderX, sliderX + sliderW);
    float handleY  = sliderY + sliderH / 2;
  
    //Vérifie si clic sur poignée MIN
    if (dist(mouseX, mouseY, handleMinX, handleY) <= 12) {
      draggingMin = true;
      return;
    }
    
    // Vérifie si clic sur poignée MAX
    if (dist(mouseX, mouseY, handleMaxX, handleY) <= 12) {
      draggingMax = true;
      return;
    }
    
  // Convertit les coordonnées souris en coordonnées monde
  // (inverse du translate + scale appliqué dans draw)
  int worldX = (int) ((mouseX - panX) / zoomFactor);
  int worldY = (int) ((mouseY - panY) / zoomFactor);
    
  City picked = pick(worldX, worldY);

  if (picked != null) {
    picked.selected = !picked.selected;
    selectedCity = picked.selected ? picked : null;
  } else {
    for (int i = 0; i < totalCount; i++) {
      cities[i].selected = false;
      selectedCity = null;
    }
  }
  redraw();
}

void mouseDragged() {
  // Pan
  if (panning) {
    panX += mouseX - lastMouseX;   // déplacement depuis la dernière position
    panY += mouseY - lastMouseY;
    lastMouseX = mouseX;
    lastMouseY = mouseY;
    redraw();
    return;
  }

  // Slider MIN
  float logMin   = log(1);
  float logMax   = log(maxPopulation);
  float clampedX = constrain(mouseX, sliderX, sliderX + sliderW);
  float logVal   = map(clampedX, sliderX, sliderX + sliderW, logMin, logMax);
  int   newVal   = (int) exp(logVal);

  if (draggingMin) {
    // La poignée min ne peut pas dépasser la poignée max
    minPopulationToDisplay = constrain(newVal, 1, maxPopulationToDisplay - 1);
    redraw();
  }

  if (draggingMax) {
    // La poignée max ne peut pas descendre sous la poignée min
    maxPopulationToDisplay = constrain(newVal, minPopulationToDisplay + 1, maxPopulation);
    redraw();
  }
}

void mouseReleased() {
  panning     = false;
  draggingMin = false;
  draggingMax = false;
}


void drawSlider() {
  float logMin    = log(1);
  float logMax    = log(maxPopulation);
  
  // Position X des deux poignées
  float handleMinX = map(log(max(1, minPopulationToDisplay)),
                         logMin, logMax, sliderX, sliderX + sliderW);
  float handleMaxX = map(log(max(1, maxPopulationToDisplay)),
                         logMin, logMax, sliderX, sliderX + sliderW);
  float handleY    = sliderY + sliderH / 2;
  
  
    //Calcul de l'histogramme
  int   nBins  = 80;                      // nombre de colonnes
  int[] bins   = new int[nBins];          // comptage par tranche
  float binW   = sliderW / float(nBins);  // largeur d'une tranche en pixels

  for (int i = 0; i < totalCount; i++) {
    float logPop = log(max(1, cities[i].population));
    // Index du bin correspondant à cette population
    int idx = (int) map(logPop, logMin, logMax, 0, nBins - 0.001);
    idx = constrain(idx, 0, nBins - 1);
    bins[idx]++;
  }

  // Nombre max de villes dans un bin (pour normaliser la hauteur)
  int maxBin = 0;
  for (int i = 0; i < nBins; i++) {
    if (bins[i] > maxBin) maxBin = bins[i];
  }

  // Dessin de l'histogramme (derrière tout le reste)
  int histoH = 40;   // hauteur max des barres au-dessus du slider

  for (int i = 0; i < nBins; i++) {
    float barX = sliderX + i * binW;
    float barH = map(bins[i], 0, maxBin, 0, histoH);  // hauteur normalisée

    // Position X du centre de ce bin -> est-il dans l'intervalle sélectionné ?
    float binCenterX = barX + binW / 2;
    boolean inRange  = (binCenterX >= handleMinX && binCenterX <= handleMaxX);

    // Couleur : bleu si dans l'intervalle, gris sinon
    noStroke();
    if (inRange) {
      fill(66, 133, 244, 180);   // bleu semi-transparent
    } else {
      fill(180, 180, 180, 180);  // gris semi-transparent
    }

    // La barre pousse vers le haut depuis le bord supérieur du slider
    rect(barX, sliderY - barH, binW - 1, barH);
  }

  // Fond gris (zone exclue)
  noStroke();
  fill(220);
  rect(sliderX, sliderY, sliderW, sliderH, 8);

  // Zone bleue (intervalle sélectionné)
  fill(66, 133, 244, 150);
  rect(handleMinX, sliderY, handleMaxX - handleMinX, sliderH, 0);

  // Poignée MIN
  fill(255);
  stroke(66, 133, 244);
  strokeWeight(2);
  ellipse(handleMinX, handleY, 20, 20);

  // Poignée MAX
  fill(255);
  stroke(66, 133, 244);
  strokeWeight(2);
  ellipse(handleMaxX, handleY, 20, 20);


  fill(0);
  noStroke();
  textSize(11);
  textAlign(LEFT, TOP);
  text("Population : " + minPopulationToDisplay
       + " → " + maxPopulationToDisplay,
       sliderX, sliderY + sliderH + 4);
}


void mouseWheel(MouseEvent event) {
  // event.getCount() = +1 (molette vers le bas) ou -1 (vers le haut)
  float delta = event.getCount();

  // Facteur multiplicatif : zoom in ou out de 10% à chaque cran
  float zoomStep = (delta > 0) ? 0.9 : 1.1;

  // Zoom centré sur la position de la souris
  // On ajuste panX/panY pour que le point sous le curseur
  // reste au même endroit après le zoom
  panX = mouseX + (panX - mouseX) * zoomStep;
  panY = mouseY + (panY - mouseY) * zoomStep;

  zoomFactor *= zoomStep;

  // Limites de zoom : entre 0.5x et 10x
  zoomFactor = constrain(zoomFactor, 0.5, 10.0);

  redraw();
}


void drawLegend() {
  int legX = width - 160;   // position en bas à droite
  int legY = height - 180;
  int legW = 145;
  int legH = 170;

  // Fond semi-transparent
  noStroke();
  fill(255, 255, 255, 210);
  rect(legX, legY, legW, legH, 8);
  stroke(200);
  strokeWeight(1);
  noFill();
  rect(legX, legY, legW, legH, 8);

  // Titre
  fill(0);
  textSize(11);
  textAlign(LEFT, TOP);
  text("Légende", legX + 10, legY + 8);

  // Dégradé couleur -> densité
  textSize(10);
  text("Densité (hab/km²)", legX + 10, legY + 28);

  // Barre de dégradé bleu -> rouge
  int gradW = 120;
  int gradH = 12;
  int gradX = legX + 12;
  int gradY = legY + 42;

  for (int i = 0; i < gradW; i++) {
    float t = i / float(gradW);
    float r = lerp(50,  255, t);
    float g = lerp(100, 50,  t);
    float b = lerp(220, 50,  t);
    stroke(r, g, b);
    line(gradX + i, gradY, gradX + i, gradY + gradH);
  }

  // Labels min/max densité
  noStroke();
  fill(0);
  textAlign(LEFT, TOP);
  text("faible", gradX, gradY + gradH + 2);
  textAlign(RIGHT, TOP);
  text("élevée", gradX + gradW, gradY + gradH + 2);

  // Taille -> population
  textAlign(LEFT, TOP);
  fill(0);
  text("Population", legX + 10, legY + 88);

  // Trois cercles de tailles différentes
  int[] popExamples  = {1000, 50000, 500000};
  String[] popLabels = {"1k", "50k", "500k"};
  int circleY = legY + 120;
  int[] circleX = {legX + 25, legX + 75, legX + 125};

  for (int i = 0; i < 3; i++) {
    float logPop    = log(popExamples[i] + 1);
    float logMinPop = log(minPopulation + 1);
    float logMaxPop = log(maxPopulation + 1);
    float r = map(logPop, logMinPop, logMaxPop, 2, 12);

    noStroke();
    fill(150, 150, 200, 200);
    ellipse(circleX[i], circleY, r * 2, r * 2);

    fill(0);
    textSize(9);
    textAlign(CENTER, TOP);
    text(popLabels[i], circleX[i], circleY + r + 2);
  }
}

void drawInfoPanel(City c) {
  int panelW = 200;
  int panelH = 140;
  int panelX = 10;
  int panelY = sliderOffset + 10;

  // Fond blanc avec bordure colorée
  noStroke();
  fill(255, 255, 255, 230);
  rect(panelX, panelY, panelW, panelH, 8);
  stroke(66, 133, 244);
  strokeWeight(2);
  noFill();
  rect(panelX, panelY, panelW, panelH, 8);

  // Contenu
  fill(0);
  noStroke();
  textAlign(LEFT, TOP);

  // Nom de la ville en titre
  textSize(13);
  text(c.name, panelX + 10, panelY + 10);

  // Ligne de séparation
  stroke(200);
  strokeWeight(1);
  line(panelX + 8, panelY + 28, panelX + panelW - 8, panelY + 28);
  noStroke();

  // Attributs
  textSize(11);
  fill(80);
  int lineH = 20;
  int textX = panelX + 10;
  int startY = panelY + 35;

  text("Population : " + (int)c.population,         textX, startY);
  text("Densité    : " + nf(c.density, 0, 1) + " hab/km²",
                                                     textX, startY + lineH);
  text("Surface    : " + nf(c.surface, 0, 1) + " km²",
                                                     textX, startY + lineH * 2);
  text("Altitude   : " + (int)c.altitude + " m",    textX, startY + lineH * 3);
  text("Code postal: " + c.postalcode,               textX, startY + lineH * 4);
}
