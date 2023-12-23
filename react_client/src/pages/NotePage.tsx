import { FC, useState } from "react";
import MDEditor from "@uiw/react-md-editor";
import { NoteDTO } from "../components/features/main-page/MainPageNote.component";
import { useParams } from "react-router";
import { NoteVisibility } from "../core/constants/NoteVisibility";
import DeleteIcon from "@mui/icons-material/Delete";
import { IconButton } from "@mui/material";
import ModeEditIcon from "@mui/icons-material/ModeEdit";
import rehypeSanitize from "rehype-sanitize";

export const NotePage: FC = () => {
  const { noteId } = useParams();
  const [note, setNote] = useState<NoteDTO>({
    id: 1,
    noteVisibility: NoteVisibility.PUBLIC,
    content: "adriano",
    ownerUserName: "adrian",
    updateTimeStamp: new Date(),
    title: "Some title",
    isOwner: true,
  });

  return (
    <>
      <p className={"text-center font-bold text-4xl text-white"}>
        {note.title}
      </p>
      <p className={"text-white text-xl text-center"}>
        Author: {note.ownerUserName}
      </p>
      <p className={"text-white text-xl text-center"}>
        Last update: {note.updateTimeStamp.toDateString()}
      </p>
      {note.isOwner && (
        <div className={"flex justify-end"}>
          <div className={"flex gap-2"}>
            <IconButton sx={{ color: "red" }}>
              <DeleteIcon fontSize={"medium"} />
            </IconButton>
            <IconButton sx={{ color: "yellow" }}>
              <ModeEditIcon fontSize={"medium"} />
            </IconButton>
          </div>
        </div>
      )}
      <MDEditor.Markdown
        source={"something"}
        rehypePlugins={[[rehypeSanitize]]}
        style={{
          whiteSpace: "pre-wrap",
          padding: 12,
        }}
      />
    </>
  );
};
