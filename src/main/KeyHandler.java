package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{
	
	public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;
	public boolean upMenu, downMenu, leftMenu, rightMenu, enterMenu;
	private boolean upConsumed, downConsumed, leftConsumed, rightConsumed, enterConsumed;

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_W) {
			upPressed = true;
			if(!upConsumed) {
				upMenu = true;
				upConsumed = true;
			}
		}
		if(code == KeyEvent.VK_A) {
			leftPressed = true;
			if(!leftConsumed) {
				leftMenu = true;
				leftConsumed = true;
			}
		}
		if(code == KeyEvent.VK_S) {
			downPressed = true;
			if(!downConsumed) {
				downMenu = true;
				downConsumed = true;
			}
		}
		if(code == KeyEvent.VK_D) {
			rightPressed = true;
			if(!rightConsumed) {
				rightMenu = true;
				rightConsumed = true;
			}
		}
		if(code == KeyEvent.VK_ENTER) {
			enterPressed = true;
			if(!enterConsumed) {
				enterMenu = true;
				enterConsumed = true;
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_W) {
			upPressed = false;
			upConsumed = false;
		}
		if(code == KeyEvent.VK_A) {
			leftPressed = false;
			leftConsumed = false;
		}
		if(code == KeyEvent.VK_S) {
			downPressed = false;
			downConsumed = false;
		}
		if(code == KeyEvent.VK_D) {
			rightPressed = false;
			rightConsumed = false;
		}
		if(code == KeyEvent.VK_ENTER) {
			enterPressed = false;
			enterConsumed = false;
		}
	}

	public void endFrame() {
		upMenu = false;
		downMenu = false;
		leftMenu = false;
		rightMenu = false;
		enterMenu = false;
	}

}
