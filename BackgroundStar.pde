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
  void display() {
    stroke(255, 127);
    strokeWeight(r);
    point(pos.x, pos.y, pos.z);
  }
}