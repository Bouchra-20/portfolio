class City { 
      int postalcode; 
      String name; 
      float x; 
      float y; 
      float population; 
      float density; 
      float surface;
      float altitude; 
      float radius;
      boolean highlighted = false;
      boolean selected    = false; 

      // put a drawing function in here and call from main drawing loop }
      
      public City(int postalcode, String name, float x, float y, float population, float surface, float altitude){
        this.postalcode = postalcode;
        this.name = name;
        this.x = x;
        this.y = y;
        this.population = population;
        this.surface    = surface;
        this.altitude   = altitude; 
        
        if (surface > 0)
          this.density = population / surface;
        else
          this.density = 0;
      }
      
      void draw() {
        float logDensity    = log(density + 1);
        float logMinDensity = log(minDensity + 1);
        float logMaxDensity = log(maxDensity + 1);
      
        float t = map(logDensity, logMinDensity, logMaxDensity, 0, 1);
      
        float r = lerp(50,  255, t);
        float g = lerp(100, 50,  t);
        float b = lerp(220, 50,  t);
      
        float logPop    = log(population + 1);
        float logMinPop = log(minPopulation + 1);
        float logMaxPop = log(maxPopulation + 1);
        radius = map(logPop, logMinPop, logMaxPop, 2, 12);
      
        noStroke();
        fill(r, g, b, 200);
        ellipse(x, y, radius * 1.5, radius * 1.5);
        
        if (selected){
          noFill();
          strokeWeight(3);
          stroke(255, 200, 0, 255);
          ellipse(x, y, (radius + 8) * 2, (radius + 8) * 2);
      
          textSize(12);
          float textW  = textWidth(name);
          float textH  = 16;
          float margin = 4;
          float rectX  = x + radius + 10;
          float rectY  = y - textH / 2 - margin;
      
          if (rectX + textW + margin * 2 > width) {
            rectX = x - radius - 10 - textW - margin * 2;
          }
          textAlign(LEFT, CENTER);
      
          noStroke();
          fill(0, 0, 0, 180);
          rect(rectX - margin, rectY, textW + margin * 2, textH + margin * 2, 4);
      
          fill(255, 200, 0);
          text(name, rectX, y);
        }
        
        if (highlighted) {
          noFill();
          strokeWeight(2);
          stroke(255, 255, 255, 220);
          ellipse(x, y, (radius + 5) * 2, (radius + 5) * 2);
    
          stroke(0, 0, 0, 180);
          strokeWeight(1);
          ellipse(x, y, (radius + 7) * 2, (radius + 7) * 2);
    
          //fill(0);
          //noStroke();
          //textSize(11);
          //textAlign(CENTER, BOTTOM);
          //text(name, x, y - radius - 8);
          textSize(12);
          
          float textW  = textWidth(name);
          float textH  = 16;
          float margin = 4;

          float rectX = x + radius + 6;
          float rectY = y - textH / 2 - margin;
          
          if (rectX + textW + margin * 2 > width) {
            rectX = x - radius - 6 - textW - margin * 2;
          }

          noStroke();
          fill(255, 255, 255, 180);
          rect(rectX - margin,
               rectY,
               textW + margin * 2,
               textH + margin * 2,
               4);

          fill(0);
          textAlign(LEFT, CENTER);
          text(name, rectX, y);
        }
      }
      
      boolean contains(int px, int py) {
        return dist(x, y, px, py) <= radius + 1;
      }
}
