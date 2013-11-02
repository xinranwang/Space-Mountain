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
  
  void update() {
    pos.add(tileVel);
    // if(pos.z > tileX) {
    //   pos.z = tiles[aIndex][(index + tileNum - 1)%tileNum].pos.z - 2 * tileX;
    // }
  }
  
  void render() {
    pushMatrix();
    translate(pos.x, pos.y - tileY/2, pos.z);
    rotateY(PI / 2);
    rotateX(-angle);
    fill(100, 100, 255);
    
    rect(0, 0, tileX, tileY);
    popMatrix();
  }
}