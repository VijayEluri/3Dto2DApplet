/*
#
# HudWindow.java
#
# copyright (c) 2009-2010, Danny Arends
# last modified Dec, 2010
# first written Dec, 2010
#
#     This program is free software; you can redistribute it and/or
#     modify it under the terms of the GNU General Public License,
#     version 3, as published by the Free Software Foundation.
# 
#     This program is distributed in the hope that it will be useful,
#     but without any warranty; without even the implied warranty of
#     merchantability or fitness for a particular purpose.  See the GNU
#     General Public License, version 3, for more details.
# 
#     A copy of the GNU General Public License, version 3, is available
#     at http://www.r-project.org/Licenses/GPL-3
#
*/

package objects.hud;

import events.ButtonControler;
import events.MyHandler;
import generic.Utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;

import objects.Point2D;
import rendering.Engine;
import rendering.Hud;
import rendering.Scene;

public class HudWindow extends HudButton{
	boolean showTopMenu = true;
	private boolean active = false;
	Point2D fromSlide;
	
	Vector<HudObject> topMenu = new Vector<HudObject>();

	public HudWindow(int x, int y, String name,HudObject p) {
		super(x, y, name,p);
		initTopMenu();
	}
	
	public HudWindow(int x, int y, int sx, int sy, String name,HudObject p) {
		super(x, y, sx, sy, p, name,Color.darkGray);
		initTopMenu();
	}
	
	void initTopMenu(){
		topMenu.add(new HudButton(0,0,22,16,this,"X",Color.lightGray){
			public void runPayload() {
				getParent().setVisible(false);
				Scene.updateScene();
			}
		});
		topMenu.add(new HudButton(0,0,22,16,this,"M",Color.lightGray){
			public void runPayload() {
				getParent().setMinimized(false);
				Scene.updateScene();
			}
		});
		topMenu.add(new HudButton(0,0,22,16,this,"-",Color.lightGray){
			public void runPayload() {
				getParent().setMinimized(true);
				Scene.updateScene();
			}
		});
	}
	
	public Point2D getSize(){
		return super.getSize();
	}
	
	public void setShowTopMenu(boolean s){
		showTopMenu=s;
	}
	
	public void runPayload() {
		if(Engine.verbose) Utils.console("HudWindow " + getName() + "at: " + x + ", " + y + " clicked");
		Hud.deactivateChildren();
		setActive(true);
		if(showTopMenu && !ButtonControler.checkLocation(topMenu, (int)MyHandler.getMouse().x, (int)MyHandler.getMouse().y)){
			if((int)MyHandler.getMouse().y < this.y+20){
				fromSlide = new Point2D(MyHandler.getMouse().x-x,MyHandler.getMouse().y-y);
				MyHandler.registerForSlide(this);
			}
		}
		ButtonControler.checkLocation(children, (int)MyHandler.getMouse().x, (int)MyHandler.getMouse().y);
		Scene.updateScene();
		Scene.reDrawScene();
	}
	
	@Override
	public boolean handleSlide(int mx, int my) {
		for(HudObject o : children){
			o.setLocation((o.x-x)+(mx - fromSlide.x),(o.y-y)+(my - fromSlide.y));
		}		
		this.x=mx - fromSlide.x;
		this.y=my - fromSlide.y;
		return true;
	}

	
	@Override
	public void render(Graphics2D g) {
		if(isVisible()){
			Hud.drawBox(g, (int)x, (int)y, (int)getSize().x, (!isMinimized()) ? (int)getSize().y:20, Color.darkGray);
			Hud.drawBox(g, (int)x, (int)y, (int)getSize().x, (int)20, (isActive())?Color.blue:Color.lightGray);
			Hud.setFont(g, 0);
			Hud.drawString(g, getName(), (int)x+10, (int)y+15);
			Hud.setFont(g, 1);
			HudObject b;
			if(showTopMenu){
				for(int wb=0;wb<topMenu.size();wb++){
					b=topMenu.get(wb);b.setLocation((x+getSize().x)-(15*(wb)+24), y+2);
				}
			}
			if(!isMinimized()){
				for(HudObject o : children){
					o.render(g);
				}
			}else{
				for(HudObject o : topMenu){
					o.render(g);
				}
			}
		}
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

}
