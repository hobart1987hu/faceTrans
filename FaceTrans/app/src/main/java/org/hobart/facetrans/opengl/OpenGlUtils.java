package org.hobart.facetrans.opengl;

import android.content.Context;



public class OpenGlUtils {

	/**
	   * Convert x to openGL
	   * 
	   * @param x
	   *            Screen x offset top left
	   * @return Screen x offset top left in OpenGL
	   */
	  public static float toGLX(float x,float ratio,float screenWidth) {
	      return -1.0f * ratio + toGLWidth(x,ratio,screenWidth);
	  }
	 
	  /**
	   * Convert y to openGL y
	   * 
	   * @param y
	   *            Screen y offset top left
	   * @return Screen y offset top left in OpenGL
	   */
	  public static float toGLY(float y,float screenHeight) {
	      return 1.0f - toGLHeight(y,screenHeight);
	  }
	 
	  /**
	   * Convert width to openGL width
	   * 
	   * @param width
	   * @return Width in openGL
	   */
	  public static float toGLWidth(float width,float ratio,float screenWidth) {
	      return 2.0f * (width / screenWidth) * ratio;
	  }
	 
	  /**
	   * Convert height to openGL height
	   * 
	   * @param height
	   * @return Height in openGL
	   */
	  public static float toGLHeight(float height,float screenHeight) {
	      return 2.0f * (height / screenHeight);
	  }
	 
	  /**
	   * Convert x to screen x
	   * 
	   * @param glX
	   *            openGL x
	   * @return screen x
	   */
	  public static float toScreenX(float glX,float ratio,float screenWidth) {
	      return toScreenWidth(glX - (-1 * ratio),ratio,screenWidth);
	  }
	 
	  /**
	   * Convert y to screent y
	   * 
	   * @param glY
	   *            openGL y
	   * @return screen y
	   */
	  public static float toScreenY(float glY,float screenHeight) {
	      return toScreenHeight(1.0f - glY,screenHeight);
	  }
	 
	  /**
	   * Convert glWidth to screen width
	   * 
	   * @param glWidth
	   * @return Width in screen
	   */
	  public static float toScreenWidth(float glWidth,float ratio,float screenWidth) {
	      return (glWidth * screenWidth) / (2.0f * ratio);
	  }
	 
	  /**
	   * Convert height to screen height
	   * 
	   * @param glHeight
	   * @return Height in screen
	   */
	  public static float toScreenHeight(float glHeight,float screenHeight) {
	      return (glHeight * screenHeight) / 2.0f;
	  }
	  
	  /**
	   * 获取view宽高比w/h
	   * @return
	   */
	  public static float VIEW_W_H = 1;
	
}
