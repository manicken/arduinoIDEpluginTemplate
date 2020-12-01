

package com.manicken;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JList;
import java.awt.Dimension;


// this gui is created by non bloatware GuiGenie http://guigenie.com/
// written in Java
//For all systems (193 kb): guigenie100.zip
//For windows (276 kb): guigenie100-setup.exe
public class SettingsDialog extends JPanel
{
	//private JLabel lblServerport;
	//public JCheckBox chkAutostart;
    public JCheckBox chkDebugMode;	
    public JList jcomp4;
    //public JTextField txtServerport;

    String[] jcomp4Items = {"Item 1", "Item 2", "Item 3"};

    public SettingsDialog() {
        //construct components
		//lblServerport = new JLabel ("Server Port");
		//chkAutostart = new JCheckBox ("Autostart Server at Arduino IDE start");
        chkDebugMode = new JCheckBox ("Activates some debug output");
        jcomp4 = new JList (jcomp4Items);
        //txtServerport = new JTextField (5);

        //adjust size and set layout
        setPreferredSize (new Dimension (263, 129));
        setLayout (null);

        //add components
		//add (lblServerport);
        //add (chkAutostart);
		//add (txtServerport);
        add (chkDebugMode);
        add (jcomp4);

        //set component bounds (only needed by Absolute Positioning)
        //lblServerport.setBounds (5, 5, 100, 25);
        //txtServerport.setBounds (85, 5, 100, 25);
		//chkAutostart.setBounds (4, 30, 232, 30);
        chkDebugMode.setBounds (4, 65, 232, 30);
        jcomp4.setBounds (45, 110, 225, 385);
    }

}
