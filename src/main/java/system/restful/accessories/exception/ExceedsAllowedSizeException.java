package system.restful.accessories.exception;

import java.io.Serializable;

public class ExceedsAllowedSizeException extends Exception implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExceedsAllowedSizeException() {
        super();
    }
}
