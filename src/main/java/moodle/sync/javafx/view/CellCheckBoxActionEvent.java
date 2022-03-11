package moodle.sync.javafx.view;

import javafx.event.ActionEvent;

public class CellCheckBoxActionEvent extends ActionEvent {

    private static final long serialVersionUID = -8113594422881751913L;

    /** The intermediate UI model bound to the source. */
    private final Object item;


    public CellCheckBoxActionEvent(Object source, Object item) {
        super(source, null);

        this.item = item;
    }


    /**
     * Returns the intermediate UI model.
     *
     * @return the UI model item.
     */
    public Object getCellItem() {
        return item;
    }
}
