package mapEditor;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class StageFileFilter extends FileFilter{

	@Override
	public boolean accept(File f) {
		if(f.isDirectory())
			return true;

		String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }

        if(ext != null && ext.equals("tbstage"))
        	return true;
        	
        return false;
	}

	@Override
	public String getDescription() {
		return ".tbstage files (and directories) only";
	}

}
