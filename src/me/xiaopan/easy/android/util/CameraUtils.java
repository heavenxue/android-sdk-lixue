/*
 * Copyright 2013 Peng fei Pan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.xiaopan.easy.android.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.view.OrientationEventListener;

/**
 * 相机工具箱
 */
public class CameraUtils {
	/**
	 * 根据当前窗口的显示方向设置相机的显示方向
	 * @param activity 用来获取当前窗口的显示方向
	 * @param cameraId 相机ID，用于区分是前置摄像头还是后置摄像头，在API级别xiaoyu9d系统下此参数无用
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static int getOptimalDisplayOrientationByWindowDisplayRotation(Activity activity, int cameraId) {      
		int degrees = WindowUtils.getDisplayRotation(activity);      
		if(Build.VERSION.SDK_INT >= 9){
			Camera.CameraInfo info = new Camera.CameraInfo();      
			Camera.getCameraInfo(cameraId, info);      
			int result;
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {          
				result = (info.orientation + degrees) % 360;          
				result = (360 - result) % 360;    
			} else {
				result = (info.orientation - degrees + 360) % 360;      
			}      
			return result;  
		}else{
			return 0; 
		}
	}
	
	/**
	 * 根据当前窗口的显示方向设置相机的显示方向
	 * @param activity 用来获取当前窗口的显示方向
	 * @param cameraId 相机ID，用于区分是前置摄像头还是后置摄像头
	 * @param camera
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void setDisplayOrientationByWindowDisplayRotation(Activity activity, int cameraId, Camera camera) {      
		int degrees = WindowUtils.getDisplayRotation(activity);      
		int result = degrees;
		if(Build.VERSION.SDK_INT >= 9){
			Camera.CameraInfo info = new Camera.CameraInfo();      
			Camera.getCameraInfo(cameraId, info);      
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {          
				result = (info.orientation + degrees) % 360;          
				result = (360 - result) % 360;    
			} else {
				result = (info.orientation - degrees + 360) % 360;      
			}      
		}
		camera.setDisplayOrientation(result);  
	}
	
	/**
	 * @param orientation OrientationEventListener类中onOrientationChanged()方法的参数
	 * @param cameraId
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static int getOptimalParametersOrientationByWindowDisplayRotation(int orientation, int cameraId) {
		if (orientation != OrientationEventListener.ORIENTATION_UNKNOWN){
			//计算方向
			int rotation = 0;
			if(Build.VERSION.SDK_INT >= 9){
				Camera.CameraInfo info = new Camera.CameraInfo();
				Camera.getCameraInfo(cameraId, info);
				orientation = (orientation + 45) / 90 * 90;
				if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
					rotation = (info.orientation - orientation + 360) % 360;
				} else {
					rotation = (info.orientation + orientation) % 360;
				}
			}
			return rotation;
		}else{
			return -1;
		}
	}
	
	/**
	 *  OrientationEventListener类中onOrientationChanged()方法的参数
	 * @param orientation
	 * @param cameraId
	 * @param camera
	 */
	public static void setParametersOrientationByWindowDisplayRotation(int orientation, int cameraId, Camera camera) {
		int rotation = getOptimalParametersOrientationByWindowDisplayRotation(orientation, cameraId);
		if(rotation >= 0){
			Camera.Parameters parameters = camera.getParameters();
			parameters.setRotation(rotation);
			camera.setParameters(parameters);
		}
	}
	
	/**
	 * 判断给定的相机是否支持给定的闪光模式
	 * @param camera 给定的相机
	 * @param flashMode 给定的闪光模式
	 * @return
	 */
	public static boolean isSupportFlashMode(Camera camera, String flashMode){
		return camera != null?camera.getParameters().getSupportedFlashModes().contains(flashMode):false;
	}
	
	/**
	 * 计算取景框的位置，可通过此Rect在裁剪出取景框中的内容
	 * @param context 上下文 用来判断是横屏还是竖屏
	 * @param surfaceViewWidth SurfaceView的宽度
	 * @param surfaceViewHeight SurfaceView的高度
	 * @param rectInSurfaceView 取景框视图在SurfaceView上的Rect
	 * @param size 输出图片的分辨率，可通过Camera.getParameters().getPictureSize()获得
	 * @return
	 */
	public static Rect computeFinderFrameRect(Context context, int surfaceViewWidth, int surfaceViewHeight, Rect rectInSurfaceView, Camera.Size size){
		Rect finalRect = new Rect(rectInSurfaceView);
		if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//如果是横屏
			finalRect.left = finalRect.left * size.width / surfaceViewWidth;
			finalRect.right = finalRect.right * size.width / surfaceViewWidth;
			finalRect.top = finalRect.top * size.height / surfaceViewHeight;
			finalRect.bottom = finalRect.bottom * size.height / surfaceViewHeight;
		} else {
			finalRect.left = finalRect.left * size.height / surfaceViewWidth;
			finalRect.right = finalRect.right * size.height / surfaceViewWidth;
			finalRect.top = finalRect.top * size.width / surfaceViewHeight;
			finalRect.bottom = finalRect.bottom * size.width / surfaceViewHeight;
		}
		return finalRect;
	}
	
	/**
	 * 将YUV格式的图片的源数据从横屏模式转为竖屏模式，注意：将源图片的宽高互换一下就是新图片的宽高
	 * @param sourceData YUV格式的图片的源数据
	 * @param width 源图片的宽
	 * @param height 源图片的高
	 * @return 
	 */
	public static final byte[] yuvLandscapeToPortrait(byte[] sourceData, int width, int height){
		byte[] rotatedData = new byte[sourceData.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++)
				rotatedData[x * height + height - y - 1] = sourceData[x + y * width];
		}
		return rotatedData;
	}
}