package pl.ros.keep.api.notes;

import java.util.List;

public record CreateNoteRequest(String title, String content, List<String> images /*base64*/){

}
