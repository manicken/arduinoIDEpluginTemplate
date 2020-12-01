/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2008 Ben Fry and Casey Reas
  Copyright (c) 2020 Jannik Leif Simon Svensson (1984)- Sweden

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.manicken;

import java.io.IOException;
import java.io.File;
import java.io.PrintStream;
import java.io.FileWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;


import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.MenuElement;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.nio.file.Path;

import processing.app.Base;
import processing.app.BaseNoGui;
import processing.app.Editor;
import processing.app.tools.Tool;
import processing.app.Sketch;
import processing.app.PreferencesData;

//import static processing.app.I18n.tr;

import com.manicken.SettingsDialog;
import com.manicken.Reflect;

/**
 * 
 */
public class pluginTemplate implements Tool {
	boolean debugPrint = true;

	Editor editor;// for the plugin
	Sketch sketch; // for the plugin

	JMenu toolsMenu; // for the plugin, uses reflection to get
	
	String thisToolMenuTitle = "pluginTemplate";
	String rootDir;
	
	boolean started = false;
	
	public void init(Editor editor) { // required by tool loader
		this.editor = editor;

		// workaround to make sure that init is run after the Arduino IDE gui has loaded
		// otherwise any System.out(will never be shown at the init phase) 
		editor.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
			  init();
			}
		});
		
	}

	public void run() {// required by tool loader
		// this is not used when using custom menu (see down @initMenu())
		// otherwise for very simple plugins
		// this is run when click the menu item
	}

	public String getMenuTitle() {// required by tool loader
		return thisToolMenuTitle;
	}

	/**
	 * used by the custom menu
	 */
	private void Activate()
	{
		
	}

	/**
	 * used by the custom menu
	 */
	private void Deactivate()
	{

	}

	/**
	 * simple FileExists
	 * @param pathname
	 * @return
	 */
	private boolean FileExists(String pathname)
	{
		return new File(pathname).exists();
	}

	/**
	 * This is the code that runs after the Arduino IDE GUI has been loaded
	 */
	private void init() {
		rootDir = GetArduinoRootDir();

		System.out.println("init " + thisToolMenuTitle);

		// this is just a showoff for different folders
		// they could be comment out or removed when developing real plugin
		if (debugPrint) // enabled in this template by default
		{
			System.out.println("\nArduino install dir:\n  " + rootDir);
			System.out.println("\nGetJarFileDir():\n  " + GetJarFileDir());
			System.out.println("\nBaseNoGui.getToolsFolder():\n  " + BaseNoGui.getToolsFolder());
			System.out.println("\nBaseNoGui.getHardwareFolder():\n  " + BaseNoGui.getHardwareFolder());
			System.out.println("\nBaseNoGui.getSketchbookFolder():\n  " + BaseNoGui.getSketchbookFolder());
			System.out.println("\nBaseNoGui.getSettingsFile(\"preferences.txt\"):\n  " + BaseNoGui.getSettingsFile("preferences.txt"));
		}
		try{
			sketch = this.editor.getSketch();
			System.out.println("\nSketch folder:\n  " + sketch.getFolder());
			
			initMenu();

			started = true;

			

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(thisToolMenuTitle + " could not start!!!");
			return;
		}
	}

	/**
	 * this initializes the submenu for the tool
	 */
	private void initMenu()
	{
		JMenuBar menubar = editor.getJMenuBar();
		int toolsMenuIndex = GetMenuBarItemIndex(menubar, "Tools");
		System.out.println("toolsMenuIndex: " +toolsMenuIndex);

		toolsMenu = (JMenu) Reflect.GetField("toolsMenu", this.editor);

		int thisToolIndex = GetMenuItemIndex(toolsMenu, thisToolMenuTitle);
		JMenu thisToolMenu = new JMenu(thisToolMenuTitle);		
		toolsMenu.insert(thisToolMenu, thisToolIndex+1);
		toolsMenu.remove(thisToolIndex);
		
		JMenuItem newItem = null;

		newItem = new JMenuItem("Activate/SaveCurrent");
		thisToolMenu.add(newItem);
		newItem.addActionListener(event -> Activate());
		
		newItem = new JMenuItem("Deactivate");
		thisToolMenu.add(newItem);
		newItem.addActionListener(event -> Deactivate());

		newItem = new JMenuItem("Select Dialog");
		thisToolMenu.add(newItem);
		newItem.addActionListener(event -> ShowSettingsDialog());
	}

	/**
	 * Just a simple dialog to show plugin settings
	 */
	public void ShowSettingsDialog() { // 
		SettingsDialog sd = new SettingsDialog();
		//cd.txtServerport.setText(Integer.toString(serverPort));
		//cd.chkAutostart.setSelected(autostart);
		
	   int result = JOptionPane.showConfirmDialog(editor, sd, thisToolMenuTitle + " Config" ,JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			
		if (result == JOptionPane.OK_OPTION) {
			//serverPort = Integer.parseInt(cd.txtServerport.getText());
			//autostart = cd.chkAutostart.isSelected();
			debugPrint = sd.chkDebugMode.isSelected();
		} else { System.out.println("Cancelled"); }
	}

	/**
	 * Workaround
	 * To get the current "Initial" plugin menu index
	 * So that it can be replaced by
	 * a custom menu item with submenus
	 * @param menu
	 * @param name
	 * @return
	 */
	public int GetMenuItemIndex(JMenu menu, String name) {
		//System.out.println("try get menu: " + name);
		for ( int i = 0; i < menu.getItemCount(); i++) {
			//System.out.println("try get menu item @ " + i);
			JMenuItem item = menu.getItem(i);
			if (item == null) continue; // happens on seperators
			if (item.getText() == name)
				return i;
		}
		return -1;
	}

	/**
	 * Experimental way of getting tools menu, not working at the moment
	 * @param menuBar
	 * @param name
	 * @return
	 */
	public int GetMenuBarItemIndex(JMenuBar menuBar, String name) {
		//System.out.println("try get menu: " + name);
		MenuElement[] items = menuBar.getSubElements();
		for ( int i = 0; i < items.length; i++) {
			//System.out.println("try get menu item @ " + i);
			JMenu menu = (JMenu)items[i];
			if (menu == null) continue; // happens on seperators
			if (menu.getText() == name)
				return i;
		}
		return -1;
	}

	/**
	 * Gets the Arduino Install Folder
	 * @return
	 */
	public String GetArduinoRootDir() {
		try {
			File file = BaseNoGui.getToolsFolder();
			return file.getParentFile().getAbsolutePath();
		} catch (Exception e) { e.printStackTrace(); return ""; }
	}

	/**
	 * Gets this plugin jar location
	 * good to know when having both SketchBook and ArduinoInstallDir plugins at the same time
	 * @return
	 */
	public String GetJarFileDir() {
		try {
			File file = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			return file.getParent();
		}catch (Exception e) { e.printStackTrace(); return ""; }
	}

	/**
	 * Just a simplifier to save files into the sketch folder
	 * @param name
	 * @return
	 */
	public String loadFile(String name) {
		File file = new File(sketch.getFolder(), name);
		boolean exists = file.exists();
		if (exists) {
			
			try {
				String content = new Scanner(file).useDelimiter("\\Z").next();
				return content;
			} catch (Exception e) { e.printStackTrace(); return ""; }
		}
		else {
			System.out.println(name + " file not found!");
			return "";
		}
	}

	/**
	 * Just a simplifier to load files from the sketch folder
	 * @param name
	 * @param contents
	 */
	public void saveFile(String name, String contents) {
		try {
            // Constructs a FileWriter given a file name, using the platform's default charset
            FileWriter file = new FileWriter(sketch.getFolder() + "/" + name);
			file.write(contents);
			file.close();
        } catch (IOException e) { e.printStackTrace(); }
	}

	
}
