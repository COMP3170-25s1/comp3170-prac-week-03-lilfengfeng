package comp3170.week3;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import static comp3170.Math.TAU;

public class Scene {

	final private String VERTEX_SHADER = "vertex.glsl";
	final private String FRAGMENT_SHADER = "fragment.glsl";

	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	private Vector3f[] colours;
	private int colourBuffer;

	private Shader shader;
	
	private Matrix4f modelMatrix = new Matrix4f();
	private Matrix4f transMatrix = new Matrix4f();
	private Matrix4f rotMatrix = new Matrix4f();
	private Matrix4f scalMatrix = new Matrix4f();
	
	final private Vector3f OFFSET = new Vector3f(0.25f,0.0f,0.0f);
	final private float MOVEMENT_SPEED = 3.0f;
	final private float SCALE = 0.1f;
	final private float ROTATION_RATE = TAU/12;


	public Scene() {

		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// @formatter:off
			//          (0,1)
			//           /|\
			//          / | \
			//         /  |  \
			//        / (0,0) \
			//       /   / \   \
			//      /  /     \  \
			//     / /         \ \		
			//    //             \\
			//(-1,-1)           (1,-1)
			//
	 		
		vertices = new Vector4f[] {
			new Vector4f( 0, 0, 0, 1),
			new Vector4f( 0, 1, 0, 1),
			new Vector4f(-1,-1, 0, 1),
			new Vector4f( 1,-1, 0, 1),
		};
			
			// @formatter:on
		vertexBuffer = GLBuffers.createBuffer(vertices);

		// @formatter:off
		colours = new Vector3f[] {
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,0),	// RED
			new Vector3f(0,0,1),	// BLUE
		};
			// @formatter:on

		colourBuffer = GLBuffers.createBuffer(colours);

		// @formatter:off
		indices = new int[] {  
			0, 1, 2, // left triangle
			0, 1, 3, // right triangle
			};
			// @formatter:on

		indexBuffer = GLBuffers.createIndexBuffer(indices);
		
		//Workshop Practise
//		float offsetX = 0.25f;
//		float offsetY = 0.0f;
//		translationMatrix(offsetX, offsetY, modelMatrix);
		
//		//Rotate 90 degree
//		float newAngle = -TAU / 4;// 90 Degree
//		rotationMatrix(newAngle, modelMatrix); 
		
		//Scale and transform at bottom-left corner.
//		float newOffsetX = 0.7f;
//		float newOffsetY = -0.7f;
//		translationMatrix(newOffsetX, newOffsetY, modelMatrix);
//	
//		float newScaleX = 0.3f;
//		float newScaleY = 0.3f;
//		scaleMatrix(newScaleX,newScaleY, modelMatrix);
		
//		//Rotate 45 degree and pointing Top-left corner		
//		float newTLCOffsetX = -0.79f;
//		float newTLCOffsetY = 0.79f;
//		translationMatrix(newTLCOffsetX, newTLCOffsetY, transMatrix);
//		
//		float newTLCAngle = TAU / 8;// 45 Degree
//		rotationMatrix(newTLCAngle, rotMatrix); 
//		
//		float newTLCScaleX = 0.3f;
//		float newTLCScaleY = 0.3f;
//		scaleMatrix(newTLCScaleX,newTLCScaleY, scalMatrix);
//		
//		modelMatrix.mul(transMatrix).mul(rotMatrix).mul(scalMatrix);
		
		//Add animaton
		modelMatrix.translate(OFFSET).scale(SCALE);
	}
	
	public void update(float deltaTime) {
		
		float movement = MOVEMENT_SPEED * deltaTime;
		float rotation = ROTATION_RATE * deltaTime;
		modelMatrix.translate(0.0f,movement, 0.0f).rotateZ(rotation);
	}
	
	public void draw() {
		
		shader.enable();
		// set the attributes
		shader.setAttribute("a_position", vertexBuffer);
		shader.setAttribute("a_colour", colourBuffer);
		
		//set the uniform
		shader.setUniform("u_modelMatrix", modelMatrix);

		// draw using index buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

	}

	/**
	 * Set the destination matrix to a translation matrix. Note the destination
	 * matrix must already be allocated.
	 * 
	 * @param tx   Offset in the x direction
	 * @param ty   Offset in the y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f translationMatrix(float tx, float ty, Matrix4f dest) {
		// clear the matrix to the identity matrix
		dest.identity();

		//     [ 1 0 0 tx ]
		// T = [ 0 1 0 ty ]
	    //     [ 0 0 0 0  ]
		//     [ 0 0 0 1  ]

		// Perform operations on only the x and y values of the T vec. 
		// Leaves the z value alone, as we are only doing 2D transformations.
		
		dest.m30(tx);
		dest.m31(ty);

		return dest;
	}

	/**
	 * Set the destination matrix to a rotation matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param angle Angle of rotation (in radians)
	 * @param dest  Destination matrix to write into
	 * @return
	 */

	public static Matrix4f rotationMatrix(float angle, Matrix4f dest) {

		// TODO: Your code here
		
		//     [ cos -sin 0 0 ]
		// R = [ sin  cos 0 0 ]
		//     [  0    0  0 0 ]
		//     [  0    0  0 1 ]
		
		dest.m00((float)Math.cos(angle));
		dest.m01((float)Math.sin(angle));
		dest.m10((float)Math.sin(-angle));
		dest.m11((float)Math.cos(angle));
		
		return dest;
	}

	/**
	 * Set the destination matrix to a scale matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param sx   Scale factor in x direction
	 * @param sy   Scale factor in y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f scaleMatrix(float sx, float sy, Matrix4f dest) {

		// TODO: Your code here
		
		//     [ sx 0  0  0 ]
		// S = [ 0  sy 0  0 ]
		//     [ 0  0  0  0 ]
		//     [ 0  0  0  1 ]
		
		dest.m00(sx);
		dest.m11(sy);
		return dest;
	}

}
