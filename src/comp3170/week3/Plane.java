package comp3170.week3;

public class Plane {
	private Scene scene;
	
	public Plane() {
		scene = new Scene();
	}
	
	public void draw() {
		scene.draw();
	}
	
	public void update(float deltaTime) {
		scene.update(deltaTime);
	}
}
