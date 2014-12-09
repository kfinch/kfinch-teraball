package mapEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class MapEditorFrontEnd extends JFrame implements ActionListener {
	
	private static final String NEW_MAP_STRING = "New";
	private static final String OPEN_MAP_STRING = "Open";
	private static final String SAVE_MAP_STRING = "Save";
	private static final String EXIT_EDITOR_STRING = "Exit";
	
	private static final String UNDO_COMMAND_STRING = "Undo";
	private static final String REDO_COMMAND_STRING = "Redo";
	private static final String CUT_COMMAND_STRING = "Cut";
	private static final String COPY_COMMAND_STRING = "Copy";
	private static final String PASTE_COMMAND_STRING = "Paste";
	
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
		setSize(710, 730); //TODO: tweak size
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
		add(toolBar, BorderLayout.SOUTH);
	}
	
	private JMenuBar createMenuBar(){
		JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;
 
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
 
        /*
        //a submenu
        menu.addSeparator();
        submenu = new JMenu("A submenu");
        submenu.setMnemonic(KeyEvent.VK_S);
 
        menuItem = new JMenuItem("An item in the submenu");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        submenu.add(menuItem);
 
        menuItem = new JMenuItem("Another item");
        submenu.add(menuItem);
        menu.add(submenu);
        */
        
        return menuBar;
	}
	
	private void createToolBar(){
		toolBar = new JToolBar();
		
		JButton button;
		
		button = new JButton("Beep");
	    button.setActionCommand("BEEP");
	    button.setToolTipText("Makes a beep");
	    button.addActionListener(this);
	    toolBar.add(button);
	    
	    button = new JButton("Boop");
	    button.setActionCommand("BOOP");
	    button.setToolTipText("Makes a boop");
	    button.addActionListener(this);
	    toolBar.add(button);
	    
	    button = new JButton("Blop");
	    button.setActionCommand("BLOP");
	    button.setToolTipText("Makes a blop");
	    button.addActionListener(this);
	    toolBar.add(button);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(EXIT_EDITOR_STRING)){
			System.exit(0);
		}
		else if(e.getActionCommand().equals(OPEN_MAP_STRING)){
			//editor.openStageFile(new File("mapeditortest.tbstage")); //TODO: remove testing
			int returnVal = fileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION)
	            editor.openStageFile(fileChooser.getSelectedFile());
		}
		else if(e.getActionCommand().equals(SAVE_MAP_STRING)){
			editor.saveStageFile();
		}
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
