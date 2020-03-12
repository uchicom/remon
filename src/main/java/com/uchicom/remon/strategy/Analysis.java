// (c) 2019 uchicom
package com.uchicom.remon.strategy;

import com.uchicom.remon.util.ImagePanel;

public interface Analysis {

	public void refrectImage(int x, int y, int option, byte[] imageBytes);
	
	public void setImagePanel(ImagePanel panel);
}
