import React, { FC, useEffect, useState } from "react";
import MDEditor from "@uiw/react-md-editor";
import { NoteDTO } from "../components/features/main-page/MainPageNote.component";
import { useNavigate, useParams } from "react-router";
import { axiosApi } from "../tools/configuration/axios-config";
import { NoteVisibility } from "../core/constants/NoteVisibility";
import { EncryptedNoteModal } from "../components/features/EncryptedNoteModal";
import { IconButton } from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import rehypeSanitize from "rehype-sanitize";
import ModeEditIcon from '@mui/icons-material/ModeEdit';

export const NotePage: FC = () => {
  const { noteId } = useParams();
  const [note, setNote] = useState<NoteDTO | null>(null);
  const [passwordModal, setPasswordModal] = useState(false);
  const [isEncrypted, setIsEncrypted] = useState<boolean>(false);

  useEffect(() => {
    axiosApi
      .get<boolean>(`/api/note/encrypted/${noteId}`)
      .then((result) => {
        setIsEncrypted(result.data);
      })
      .catch((error) => {
        console.log(error);
      });
  }, [noteId]);

  useEffect(() => {
    if (isEncrypted) {
      setPasswordModal(true);
      return;
    }

    axiosApi
      .post<NoteDTO>(`/api/note/view/${noteId}`, {password: null})
      .then((result) => {
        setNote(result.data);
      })
      .catch((error) => {
        console.log(error);
      });
  }, [isEncrypted, noteId]);

  return (
    <>
      <EncryptedNoteModal
        isOpen={passwordModal}
        handleClose={() => setPasswordModal(false)}
        // @ts-ignore
        noteId={noteId}
        setNote={setNote}
      />
      <p className={"text-center font-bold text-4xl text-white"}>
        {!!note ? note.title : ''}
      </p>
      <p className={"text-white text-xl text-center"}>
        Author: {!!note ? note.ownerUsername : ''}
      </p>
      <p className={"text-white text-xl text-center"}>
        Last update: {!!note ? new Date(note.updateTimeStamp).toDateString() : ''}
      </p>
      {(!!note ? note.owner : true) && (
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
        source={!!note ? note.content : ''}
        rehypePlugins={[[rehypeSanitize]]}
        style={{
          whiteSpace: "pre-wrap",
          padding: 12,
        }}
      />
    </>
  );
};
