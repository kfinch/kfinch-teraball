package mapEditor;

import java.io.Serializable;

/**
 * Why doesn't java support tuples t_t
 * 
 * @author Kelton Finch
 */
public class EntityLink implements Serializable {
	
	private static final long serialVersionUID = 8864383275621292983L;
	
	public String e1, e2;
	
	public EntityLink(String e1, String e2){
		this.e1 = e1;
		this.e2 = e2;
	}
}