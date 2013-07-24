/*
 * Boltzmann 3D, a kinetic theory demonstrator
 * Copyright (C) 2013 Dr. Randall B. Shirts
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.byu.chem.boltzmann.view.simulationsettings.components;

import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

import edu.byu.chem.boltzmann.controller.ErrorHandler;
import edu.byu.chem.boltzmann.utils.Units;

// Moved out from Utils.java, APS 27 May 2008

//ActivationEnergyPanel displays the activation energy curve.
public class ActivationEnergyPanel extends JPanel
{
	private static final long serialVersionUID = 0x1L;
	
	private final int topMargin=20, bottomMargin=20;
	private final int leftMargin=20, rightMargin=20;
	private final int arrowSize=3;
	public double redToBlue, blueToRed;

        private String redString = "Red", blueString = "Blue";
        private Color redColor = Color.RED, blueColor = Color.BLUE;

	public ActivationEnergyPanel(double rToB, double bToR)
	{                
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(200, 200));
		setMinimumSize(new Dimension(200, 200));
		setFont(new Font("Arial", Font.BOLD, 12));
		setToolTipText("Activation Energy Graph: Indicates the energy required for a red-to-blue reaction, and vice-versa.");
		redToBlue=rToB;
		blueToRed=bToR;
	}
	public void paintComponent(Graphics g)
	{
		try
		{
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			int w=getSize().width-leftMargin-rightMargin, h=getSize().height-topMargin-bottomMargin;
			int w2=w/2; //, h2=h/2;
			int [] y=new int[w2], y2=new int[w2], x=new int[w2], x2=new int[w2];
			int R, B;
			int yoff=0;
			if(redToBlue<blueToRed)
			{
				R=(int)((redToBlue/blueToRed)*h);
				B=h;
			}
			else
			{
				B=(int)((blueToRed/redToBlue)*h);
				R=h;
				yoff=(int)(B-R);
			}
			for(int i=0;i<w2;i++)
			{
				double j=i/(double)w;
				double j2=j*j;
				double j3=j2*j;
				x[i]=i+leftMargin;
				x2[i]=i+w2+leftMargin;
				y[i]=h-(int)((-16*R*j3)+(12*R*j2)+(B-R))+topMargin+yoff;
				y2[i]=h-(int)((16*B*j3)-(12*B*j2)+B)+topMargin+yoff;
			}
			//Draw the curve
			g.drawPolyline(x, y, w2);
			g.drawPolyline(x2, y2, w2);
			//Draw the Ea for Red->Blue
			drawDoubleArrow(leftMargin, y[0], topMargin, g);
			g.drawString("Ea=" + redToBlue, leftMargin+5, (y[0]+topMargin)/2);
			//Draw the Ea for Blue->Red
			drawDoubleArrow(getSize().width-rightMargin, y2[w2-1], topMargin, g);
			FontMetrics fm=g.getFontMetrics();
			String blueEa="Ea=" + blueToRed;
			g.drawString(blueEa,getSize().width-rightMargin-fm.stringWidth(blueEa)-5, (y2[w2-1]+topMargin)/2);
			//Draw the delta E
			drawDoubleArrow(w2+leftMargin, y[0], y2[w2-1], g);
			g.drawString("\u2206E", w2+leftMargin-20, (y[0]+y2[w2-1])/2);
			//Draw the labels
			g.setColor(redColor);
			g.drawString(redString, 5, y[0]+15);
			g.setColor(blueColor);
			g.drawString(blueString, w, y2[w2-1]+15);
		}
		catch(Exception e)
		{
			throw new RuntimeException(null,e);
		}

	}

        public void setActivationEnergies(double redToBlueEnergy, double blueToRedEnergy) {
            redToBlue = redToBlueEnergy;
            blueToRed = blueToRedEnergy;
            repaint();
        }

        public void setParticleNames(String redName, String blueName) {
            this.redString = redName;
            this.blueString = blueName;
        }

        public void setParticleColors(Color redColor, Color blueColor) {
            this.redColor = redColor;
            this.blueColor = blueColor;
        }

	//Draws a vertical double ended arrow
	public void drawDoubleArrow(int x, int y1, int y2, Graphics g)
	{
		g.drawLine(x,y1, x,y2);
		int arrowY=arrowSize;
		if(y1>y2)
			arrowY=-arrowY;
		g.drawLine(x, y1, x+arrowSize, y1+arrowY);
		g.drawLine(x, y1, x-arrowSize, y1+arrowY);
		g.drawLine(x, y2, x+arrowSize, y2-arrowY);
		g.drawLine(x, y2, x-arrowSize, y2-arrowY);

	}
}
