import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.video.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class spaceMountain extends PApplet {



Movie music;

int mode = 0;

int tileX = 450 * 2;
int tileY = tileX / 3;
int tileNum = 10;
int tail = 4;
Tile[][] tiles = new Tile[8][tileNum]; 
PVector tileVel = new PVector(0, 0, 20);

PShape logo;

// starts
Star[] stars = new Star[1000];

BackgroundStar[] backgroundStars = new BackgroundStar[1000];
float backgroundDepth = 1000;

PVector[] vels = new PVector[20];
//PVector tVel;
PVector cVel = new PVector();

int index = 0;

long now = 0;
float period = 3000;

// how fast are we flying
float factor = 1.5f;
// A target to "lerp" to
float target = 1;

// How far away are stars
float depth = 5000;

// Flags
boolean hyperdrive = false;
boolean rotate = true;

// Some rotation
float rotX, rotY, rotZ;

PVector move;
PVector pmove;

public void setup() {
  size(1600, 1600, P3D);
  for (int i=0; i<stars.length; i++) {
    stars[i] = new Star();
  }

  for (int i=0; i<backgroundStars.length; i++) {
    backgroundStars[i] = new BackgroundStar();
  }

  generateVels();
  getTiles();
  logo = loadShape("spacemountain.svg");
  logo.scale(1.5f * width / 800);
  //logo.disableStyle();

  music = new Movie(this, "Space Mountain Music.mp4");
  music.loop();
}

public void draw() {
  background(0);
  factor = lerp(factor, target, 0.1f);
 // rotY = map(mouseY, 0, height, -PI/2, PI/2);
 // rotX = map(mouseX, 0, width, PI/2, -PI/2);
  move = new PVector(width / 2 - mouseX, height / 2 - mouseY, depth / 10);


  // println("index: "+index);
  // println("cVel: "+cVel);
  
  pushMatrix();
  translate(width/2, height/2);
  rotateX(rotY);
  rotateY(rotX);
  rotateZ(rotZ);
  
  for (Star s : stars){
    s.update();
    s.display();
  }

  if(mode == 0) {
    target = 1;
  }

  // Draw all stars, they move, not us
  //if(mode != 1){  
    // for (Star s : stars) {
    //   //s.update();
    //   s.display();
    // }
    for (BackgroundStar s : backgroundStars) {
      s.display();
    }
  //}

  if(mode == 3) {
    hyperdrive = true;
    target = 20;
    cVel.lerp(new PVector(0, 0, 5), 0.001f);
  }

  if(mode == 4)
  {  
    if(abs(vels[index].x - cVel.x) < 0.1f && abs(vels[index].y - cVel.y) < 0.1f && abs(vels[index].z - cVel.z) < 0.1f) {
      index++;
      index = index%vels.length;
    } else {
      cVel.lerp(vels[index], 0.03f);
    }

    if (rotate) {
    //rotZ += 0.01;
    float trotZ = 0;

    if(pmove.x == cVel.x && pmove.y == cVel.y && pmove.z == cVel.z) {
      println("equal");
      if(index%3==1) trotZ -= 0.02f;
      else trotZ += 0.01f;
    }
    else trotZ = PVector.angleBetween(cVel, new PVector(0, 0, 1));


      rotZ = lerp(rotZ, trotZ, 0.1f);
      
  //    rotZ = PVector.angleBetween(move, new PVector(0, 0, 1));
      rotX = PI/2 - PVector.angleBetween(cVel, new PVector(1, 0, 0));
      rotY = PI/2 - PVector.angleBetween(cVel, new PVector(0, 1, 0));
    }

    if(millis() - now > period) {
      target = random(40, 60);
      now = millis();
      period = random(3000, 10000);
    }
  }

  if(mode == 5) {
    //hyperdrive = true;
    target = 1;
    cVel.lerp(new PVector(0, 0, 5), 0.1f);
    rotZ = lerp(rotZ, 0, 0.1f);
    rotX = lerp(rotX, 0, 0.1f);
    rotY = lerp(rotY, 0, 0.1f);
  }


  popMatrix();
  
  fill(255);
  //text("space to engage hyperdrive\nr to enable spin\nmouse drag to rotate view",10,30);
  //pmove = move.get();
  pmove = cVel.get();

  if(mode == 1) tiles();

  // draw the car
  car();

  if(mode == 0) logo();// draw logo
  
}

public void keyPressed() {
  if (key == ' ') {
    mode++;
    mode = mode%6;

    //if(mode == 3) hyperdrive = true;
    // hyperdrive = !hyperdrive;
    // if (hyperdrive) {
    //   target = 50;
    // } 
    // else {
    //   target = 1;
    // }
  } 
  // else if (key == 'r') {
  //   rotate = !rotate;
  // }
}

// void mouseDragged() {
//   rotX += (mouseX - pmouseX) * 0.01;
//   rotY -= (mouseY - pmouseY) * 0.01;
// }

public void generateVels() {
  for (int i = 0; i<vels.length; i++){
    if(i % 2 == 0)
    vels[i] = new PVector(random(-2, 2), random(-2, 2), random(5));
    else {
      vels[i] = new PVector(0, 0, random(5));
    }
  }
}

public void logo() {
  pushMatrix();
  shapeMode(CENTER);
  translate(width / 2, height * 0.65f, -tileX);
  shape(logo, 0, 0);
  popMatrix();
}

public void car() {
  fill(50);
  noStroke();
  ellipseMode(CENTER);
  pushMatrix();
  translate(0, 0, tileX);
  ellipse(width / 2, height* 0.75f, width /4, width / 4);
  popMatrix();
}

public void tiles() {
  noStroke();
    for(int j = 0; j < tiles.length; j++) {
      for(int i = 0; i < tiles[j].length; i++) {
        tiles[j][i].update();
      //tiles[j][i].render();
      }
    
      for (int i = 0; i < tail; i++) {
        tiles[j][tileNum - 1-(i + PApplet.parseInt(frameCount / 6))%tileNum].render();
      }
    }
}

public void getTiles() {

  //float angle = 0;
  for(int j = 0; j < tiles.length; j++) {
    float angle = j * PI / 4;
    for(int i = 0; i < tiles[j].length; i++) {
      //tiles[i] = new Tile(0, height / 2, -tileX * i * 2, i);
      tiles[j][i] = new Tile(width / 2 - cos(angle)*width/2, height / 2 - sin(angle)*width/2, -tileX * i * 2 + (j%2) * tileX, j, i, angle);
    }
  }
}

public void movieEvent(Movie m) {
  m.read();
}

class BackgroundStar {
	PVector pos;
  // Size
  float r;

  BackgroundStar() {
    // Magic a random 3D vector is a point on a sphere of radius 1
    pos = PVector.random3D();
    // Expand by size of sphere
    pos.mult(backgroundDepth);
    r = 2;
  }

  // Just a sad little point
  public void display() {
    stroke(255, 127);
    strokeWeight(r);
    point(pos.x, pos.y, pos.z);
  }
}
// NOC Cosmos
// https://github.com/shiffman/The-Nature-of-Code-Cosmos-Edition

// Simple simulation of flying star

class Star {
  PVector pos;
  PVector originalVel;
  PVector vel;

  Star() {
    pos = new PVector(random(-5*width, 5*width), random(-5*height, 5*height), random(-depth, depth));
    // Save its starting velocity
    originalVel = new PVector(0, 0, random(1, 5));
    // originalVel = new PVector(0, 0, 0);
    vel = originalVel.get();
  }

  public void display() {
    stroke(200, 200, 255, 127);
    strokeWeight(2);
    point(pos.x, pos.y, pos.z);
    // We draw a trail based on its velocity
    beginShape();
    for (int i = 0; i < 5; i++) {
      vertex(pos.x-vel.x*i, pos.y-vel.y*i, pos.z-vel.z*i);
    }
    endShape();
  }
  
  // Move!
  public void update() {
    // Eh, we could do better
    // Accessing global speed "factor"
    
    if(!hyperdrive) {
      vel.z = originalVel.z*factor; 
    }
    else {
      vel = cVel.get();
      vel.setMag(originalVel.mag()*factor);
    }

    pos.add(vel);
    
    
    // Recycle the star when it passes far away
    if (pos.z > depth)
      pos.z = -depth;
    if (pos.x > 5*width || pos.x < -5*width)
      pos.x = -pos.x;
    if (pos.y > 5*height || pos.y < -5*height)
      pos.y = -pos.y;
  }
}

class Tile {
  PVector pos = new PVector();
  int index;
  int aIndex;
  float angle;
  
  Tile(float x, float y, float z, int j, int i, float a) {
    pos = new PVector(x, y, z);
    aIndex = j;
    index = i;
    angle = a;
    
  }
  
  public void update() {
    pos.add(tileVel);
    // if(pos.z > tileX) {
    //   pos.z = tiles[aIndex][(index + tileNum - 1)%tileNum].pos.z - 2 * tileX;
    // }
  }
  
  public void render() {
    pushMatrix();
    translate(pos.x, pos.y - tileY/2, pos.z);
    rotateY(PI / 2);
    rotateX(-angle);
    fill(100, 100, 255);
    
    rect(0, 0, tileX, tileY);
    popMatrix();
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "spaceMountain" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
