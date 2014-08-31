package com.nexters.mindpaper.programs;

import static android.opengl.GLES20.glUseProgram;
import android.content.Context;

import com.nexters.mindpaper.util.ShaderHelper;
import com.nexters.mindpaper.util.TextResourceReader;

abstract class ShaderProgram {
	// Uniform constants
	protected static final String U_MATRIX = "u_Matrix";
	protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

	// Attribute constants
	protected static final String A_POSITION = "a_Position";
	protected static final String A_COLOR = "a_Color";
	protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
	

	protected static final String U_POSITION = "u_position";

	// Shader program
	protected final int program;

	protected ShaderProgram(Context context, int vertexShaderResourceId,
			int fragmentShaderResourceId) {
		// Compile the shaders and link the program.
		program = ShaderHelper.buildProgram(TextResourceReader
				.readTextFileFromResource(context, vertexShaderResourceId),
				TextResourceReader.readTextFileFromResource(context,
						fragmentShaderResourceId));
	}

	public void useProgram() {
		// Set the current OpenGL shader program to this program.
		glUseProgram(0);
		glUseProgram(program);
	}
}