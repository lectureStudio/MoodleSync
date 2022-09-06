package moodle.sync.javafx;

import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import moodle.sync.util.syncTableElement;


public class DragAndDropRowFactory implements Callback<TableView<syncTableElement>, TableRow<syncTableElement>> {

    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    @Override
    public TableRow<syncTableElement> call(TableView<syncTableElement> tableView) {

        final TableRow<syncTableElement> row ;
        /**if (baseFactory == null) {
            row = new TableRow<>();
        } else {
            row = baseFactory.call(tableView);
        }*/
        row = new TableRow<>();

        row.setOnDragDetected(event -> {
            /* drag was detected, start drag-and-drop gesture*/
            if (! row.isEmpty()) {
                Integer index = row.getIndex();

                Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                db.setDragView(row.snapshot(null, null));
                ClipboardContent cc = new ClipboardContent();
                cc.put(SERIALIZED_MIME_TYPE, index);
                db.setContent(cc);
                event.consume();
            }
        });

        row.setOnDragOver(event -> {
            /* data is dragged over the target */
            Dragboard db = event.getDragboard();
            if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                if (row.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    event.consume();
                }
            }
        });

        row.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                syncTableElement draggedElement = tableView.getItems().remove(draggedIndex);

                int dropIndex ;


                if (row.isEmpty()) {
                    dropIndex = tableView.getItems().size() ;
                } else {
                    dropIndex = row.getIndex();
                }

                draggedElement.setBeforemod(tableView.getItems().get(dropIndex).getCmid());
                tableView.getItems().add(dropIndex, draggedElement);

                event.setDropCompleted(true);
                tableView.getSelectionModel().select(dropIndex);
                event.consume();
            }
        });



        return row;
    }

}