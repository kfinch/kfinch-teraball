package mapEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class MapEditorFrontEnd extends JFrame implements ActionListener {
	
	private static final String NEW_MAP_STRING = "New...";
	private static final String OPEN_MAP_STRING = "Open...";
	private static final String SAVE_MAP_STRING = "Save";
	private static final String SAVE_AS_MAP_STRING = "Save As...";
	private static final String EXIT_EDITOR_STRING = "Exit";
	
	private static final String UNDO_COMMAND_STRING = "Undo";
	private static final String REDO_COMMAND_STRING = "Redo";
	private static final String CUT_COMMAND_STRING = "Cut";
	private static final String COPY_COMMAND_STRING = "Copy";
	private static final String PASTE_COMMAND_STRING = "Paste";
	
	private static final String MANIPULATOR_TOOL_STRING = "Manipulator Tool";
	private static final String ADD_TERRAIN_STRING = "Add Terrain";
	private static final String ADD_MINE_STRING = "Add Mine";
	
	private static final String SNAP_TO_GRID_OFF_STRING = "snaptogridoff";
	private static final String SNAP_TO_GRID_5_STRING = "snaptogrid5";
	private static final String SNAP_TO_GRID_10_STRING = "snaptogrid10";
	private static final String SNAP_TO_GRID_25_STRING = "snaptogrid25";
	private static final String SNAP_TO_GRID_50_STRING = "snaptogrid50";
	private static final String SNAP_TO_GRID_CUSTOM_STRING = "snaptogridcustom";
	
	private static final String SET_NEXT_STAGE_STRING = "Set Next Stage...";
	private static final String SET_PLAYER_START_STRING = "Set Player Start Location...";
	private static final String SET_STAGE_SIZE_STRING = "Set Stage Size...";
	
	private MapEditorRunner editor;
	private MapEditorPanel editorPanel;
	
	private JToolBar toolBar;
	
	private JFileChooser fileChooser;
	
	public MapEditorFrontEnd(){
		initUI();
	}
	
	private void initUI(){
		//initialize the frame
		setTitle("Teraball Map Editor");
		setSize(1000, 730); //TODO: tweak size
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		//initialize game runner
		editor = new MapEditorRunner();
			
		//initialize various panels
		editorPanel = editor.display;
		add(editorPanel);
		
		//initialize the file chooser (should be in current working directory, which should be where game is)
		fileChooser = new JFileChooser(new File("").getAbsoluteFile());
		fileChooser.setFileFilter(new StageFileFilter());
		
		//initialize menu bar
		this.setJMenuBar(createMenuBar());
		
		//initialize tool bar
		createToolBar();
		add(toolBar, BorderLayout.WEST);
	}
	
	private JMenuBar createMenuBar(){
		JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbMenuItem;
 
        //Create the menu bar.
        menuBar = new JMenuBar();
 
        //Build the file menu.
        menu = new JMenu("File");
        menuBar.add(menu);
 
        //Build the file menu's items
        menuItem = new JMenuItem(NEW_MAP_STRING);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        menuItem = new JMenuItem(OPEN_MAP_STRING);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        menuItem = new JMenuItem(SAVE_MAP_STRING);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        menuItem = new JMenuItem(SAVE_AS_MAP_STRING);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem(EXIT_EDITOR_STRING);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        //Build the edit menu.
        menu = new JMenu("Edit");
        menuBar.add(menu);
        
        //Build the edit menu's items
        menuItem = new JMenuItem(UNDO_COMMAND_STRING);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        menuItem = new JMenuItem(REDO_COMMAND_STRING);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem(CUT_COMMAND_STRING);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        menuItem = new JMenuItem(COPY_COMMAND_STRING);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        menuItem = new JMenuItem(PASTE_COMMAND_STRING);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
        menuItem.addActionListener(this);
 
        //Build the tools menu.
        menu = new JMenu("Tools");
        menuBar.add(menu);
        
        menuItem = new JMenuItem(MANIPULATOR_TOOL_STRING);
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        menuItem = new JMenuItem(ADD_TERRAIN_STRING);
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        menuItem = new JMenuItem(ADD_MINE_STRING);
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        //Build the stage menu.
        menu = new JMenu("Stage");
        menuBar.add(menu);
        
        //Build the stage menu's items
        
        //Build the snap to grid submenu
        submenu = new JMenu("Snap to grid...");
        menu.add(submenu);
        
        ButtonGroup group = new ButtonGroup();
        
        rbMenuItem = new JRadioButtonMenuItem("Off");
        rbMenuItem.setActionCommand(SNAP_TO_GRID_OFF_STRING);
        rbMenuItem.setSelected(true);
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);
        
        rbMenuItem = new JRadioButtonMenuItem("5");
        rbMenuItem.setActionCommand(SNAP_TO_GRID_5_STRING);
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);
        
        rbMenuItem = new JRadioButtonMenuItem("10");
        rbMenuItem.setActionCommand(SNAP_TO_GRID_10_STRING);
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);
        
        rbMenuItem = new JRadioButtonMenuItem("25");
        rbMenuItem.setActionCommand(SNAP_TO_GRID_25_STRING);
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);
        
        rbMenuItem = new JRadioButtonMenuItem("50");
        rbMenuItem.setActionCommand(SNAP_TO_GRID_50_STRING);
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);
        
        rbMenuItem = new JRadioButtonMenuItem("Custom");
        rbMenuItem.setActionCommand(SNAP_TO_GRID_CUSTOM_STRING);
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem(SET_NEXT_STAGE_STRING);
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        menuItem = new JMenuItem(SET_PLAYER_START_STRING);
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        menuItem = new JMenuItem(SET_STAGE_SIZE_STRING);
        menu.add(menuItem);
        menuItem.addActionListener(this);
        
        return menuBar;
	}
	
	private void createToolBar(){
		toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setBackground(Color.decode("#bbbbbb"));
		
		JButton button;
		
		button = new JButton("Beep");
	    button.setActionCommand("beep");
	    button.setToolTipText("Makes a beep");
	    button.addActionListener(this);
	    toolBar.add(button);
	    
	    button = new JButton("Boop");
	    button.setActionCommand("boop");
	    button.setToolTipText("Makes a boop");
	    button.addActionListener(this);
	    toolBar.add(button);
	    
	    button = new JButton("Blop");
	    button.setActionCommand("blop");
	    button.setToolTipText("Makes a blop");
	    button.addActionListener(this);
	    toolBar.add(button);
	    
	    toolBar.addSeparator();
	    
	    button = new JButton("Blop");
	    button.setActionCommand("blop");
	    button.setToolTipText("Makes a blop");
	    button.addActionListener(this);
	    toolBar.add(button);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//file menu
		if(e.getActionCommand().equals(NEW_MAP_STRING)){}
		else if(e.getActionCommand().equals(OPEN_MAP_STRING)){
			int returnVal = fileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION)
	            editor.openStageFile(fileChooser.getSelectedFile());
		}
		else if(e.getActionCommand().equals(SAVE_MAP_STRING)){
			editor.saveStageFile();
		}
		else if(e.getActionCommand().equals(SAVE_AS_MAP_STRING)){
			int returnVal = fileChooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION)
	            editor.saveAsStageFile(fileChooser.getSelectedFile());
		}
		else if(e.getActionCommand().equals(EXIT_EDITOR_STRING)){
			System.exit(0);
		}
		//edit menu
		if(e.getActionCommand().equals(UNDO_COMMAND_STRING)){}
		if(e.getActionCommand().equals(REDO_COMMAND_STRING)){}
		if(e.getActionCommand().equals(CUT_COMMAND_STRING)){}
		if(e.getActionCommand().equals(COPY_COMMAND_STRING)){}
		if(e.getActionCommand().equals(PASTE_COMMAND_STRING)){}
		//tool menu
		if(e.getActionCommand().equals(MANIPULATOR_TOOL_STRING)){
			editor.switchToManipulatorTool();
		}
		if(e.getActionCommand().equals(ADD_TERRAIN_STRING)){}
		if(e.getActionCommand().equals(ADD_MINE_STRING)){
			editor.placeMineEntity();
		}
		//stage menu
		if(e.getActionCommand().equals(SNAP_TO_GRID_OFF_STRING)){
			editor.isSnapToGridOn = false;
		}
		if(e.getActionCommand().equals(SNAP_TO_GRID_5_STRING)){
			editor.snapToGrid = 5;
			editor.isSnapToGridOn = true;
		}
		if(e.getActionCommand().equals(SNAP_TO_GRID_10_STRING)){
			editor.snapToGrid = 10;
			editor.isSnapToGridOn = true;
		}
		if(e.getActionCommand().equals(SNAP_TO_GRID_25_STRING)){
			editor.snapToGrid = 25;
			editor.isSnapToGridOn = true;
		}
		if(e.getActionCommand().equals(SNAP_TO_GRID_50_STRING)){
			editor.snapToGrid = 50;
			editor.isSnapToGridOn = true;
		}
		if(e.getActionCommand().equals(SNAP_TO_GRID_CUSTOM_STRING)){}
		if(e.getActionCommand().equals(SET_NEXT_STAGE_STRING)){
			//TODO: this method of doing it may make it care about the *absolute* path of next stage, which is wrong
			int returnVal = fileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION)
	            editor.stage.nextStage = fileChooser.getSelectedFile();
		}
		if(e.getActionCommand().equals(SET_PLAYER_START_STRING)){}
		if(e.getActionCommand().equals(SET_STAGE_SIZE_STRING)){}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MapEditorFrontEnd frontEnd = new MapEditorFrontEnd();
					frontEnd.setVisible(true);
			}
		});
	}
	
}
