package uk.ac.ed.ph.qtiengine.utils;

/**
 * 
 * @author Jonathon Hare
 */
public class ContentPackageException extends RuntimeException {

    private static final long serialVersionUID = -8628944534184533327L;
    
    public ContentPackageException() {
        super();
    }

    public ContentPackageException(String message) {
        super(message);
    }

    public ContentPackageException(Throwable cause) {
        super(cause);
    }

    public ContentPackageException(String message, Throwable cause) {
        super(message, cause);
    }
}
