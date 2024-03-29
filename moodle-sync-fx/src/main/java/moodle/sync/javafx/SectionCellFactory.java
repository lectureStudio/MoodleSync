package moodle.sync.javafx;

import javafx.scene.control.ListView;
import javafx.util.Callback;
import moodle.sync.web.json.Section;

/**
 * Class implementing Sections as the content of a ComboBox.
 *
 * @author Daniel Schröter
 */
public class SectionCellFactory implements Callback<ListView<Section>, SectionListCell> {

    @Override
    public SectionListCell call(ListView<Section> param) {
        return new SectionListCell();
    }

}

