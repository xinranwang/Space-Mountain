import processing.video.*;

Movie music;

int mode = 0;

int tileX = 450;
int tileY = tileX / 3;
int tileNum = 10;
int tail = 4;
Tile[][] tiles = new Tile[8][tileNum]; 
PVector tileVel = new PVector(0, 0, 20);

PShape logo;

// starts
Star[] stars = new Star[1000];

BackgroundStar[] backgroundStars = new BackgroundStar[1000];
float backgroundDepth = 5000;

PVector[] vels = new PVector[20];
//PVector tVel;
PVector cVel = new PVector();

int index = 0;

long now = 0;
float period = 3000;

// how fast are we flying
float factor = 1.5;
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

void setup() {
  size(800, 800, P3D);
  for (int i=0; i<stars.length; i++) {
    stars[i] = new Star();
  }

  for (int i=0; i<backgroundStars.length; i++) {
    backgroundStars[i] = new BackgroundStar();
  }

  generateVels();
  getTiles();
  logo = loadShape("spacemountain.svg");
  logo.scale(1.5 * width / 800);
  //logo.disableStyle();

  music = new Movie(this, "Space Mountain Music.mp4");
  music.loop();
}

void draw() {
  background(0);
  factor = lerp(factor, target, 0.1);
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
    cVel.lerp(new PVector(0, 0, 5), 0.001);
  }

  if(mode == 4)
  {  
    if(abs(vels[index].x - cVel.x) < 0.1 && abs(vels[index].y - cVel.y) < 0.1 && abs(vels[index].z - cVel.z) < 0.1) {
      index++;
      index = index%vels.length;
    } else {
      cVel.lerp(vels[index], 0.03);
    }

    if (rotate) {
    //rotZ += 0.01;
    float trotZ = 0;

    if(pmove.x == cVel.x && pmove.y == cVel.y && pmove.z == cVel.z) {
      println("equal");
      if(index%3==1) trotZ -= 0.02;
      else trotZ += 0.01;
    }
    else trotZ = PVector.angleBetween(cVel, new PVector(0, 0, 1));


      rotZ = lerp(rotZ, trotZ, 0.1);
      
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
    cVel.lerp(new PVector(0, 0, 5), 0.1);
    rotZ = lerp(rotZ, 0, 0.1);
    rotX = lerp(rotX, 0, 0.1);
    rotY = lerp(rotY, 0, 0.1);
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

void keyPressed() {
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

void generateVels() {
  for (int i = 0; i<vels.length; i++){
    if(i % 2 == 0)
    vels[i] = new PVector(random(-2, 2), random(-2, 2), random(5));
    else {
      vels[i] = new PVector(0, 0, random(5));
    }
  }
}

void logo() {
  pushMatrix();
  shapeMode(CENTER);
  translate(width / 2, height * 0.65, -tileX);
  shape(logo, 0, 0);
  popMatrix();
}

void car() {
  fill(50);
  noStroke();
  ellipseMode(CENTER);
  pushMatrix();
  translate(0, 0, tileX);
  ellipse(width / 2, height* 0.75, width /4, width / 4);
  popMatrix();
}

void tiles() {
  noStroke();
    for(int j = 0; j < tiles.length; j++) {
      for(int i = 0; i < tiles[j].length; i++) {
        tiles[j][i].update();
      //tiles[j][i].render();
      }
    
      for (int i = 0; i < tail; i++) {
        tiles[j][tileNum - 1-(i + int(frameCount / 6))%tileNum].render();
      }
    }
}

void getTiles() {

  //float angle = 0;
  for(int j = 0; j < tiles.length; j++) {
    float angle = j * PI / 4;
    for(int i = 0; i < tiles[j].length; i++) {
      //tiles[i] = new Tile(0, height / 2, -tileX * i * 2, i);
      tiles[j][i] = new Tile(width / 2 - cos(angle)*width/2, height / 2 - sin(angle)*width/2, -tileX * i * 2 + (j%2) * tileX, j, i, angle);
    }
  }
}

void movieEvent(Movie m) {
  m.read();
}

