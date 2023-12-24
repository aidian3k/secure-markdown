import {
  Avatar,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  IconButton,
} from "@mui/material";
import { FC, useState } from "react";
import DeleteIcon from "@mui/icons-material/Delete";
import ShareIcon from "@mui/icons-material/Share";
import MDEditor from "@uiw/react-md-editor";
import { NoteVisibility } from "../../../core/constants/NoteVisibility";
import { useNavigate } from "react-router";
import rehypeSanitize from "rehype-sanitize";
import { EncryptedNoteModal } from "../EncryptedNoteModal";

export type NoteDTO = {
  id: number;
  title: string;
  content: string;
  updateTimeStamp: Date;
  ownerUsername: string;
  noteVisibility: NoteVisibility;
  owner: boolean;
};

export const MainPageNote: FC<NoteDTO> = (props: NoteDTO) => {
  const navigate = useNavigate();

  function handleNavigationToNote() {
      navigate(`/note/view/${props.id}`);
  }

  return (
    <div className={"transition-all hover:scale-105"}>
      <Card sx={{ maxWidth: 345 }} onClick={() => handleNavigationToNote()}>
        <CardHeader
          avatar={
            <Avatar sx={{ bgcolor: "red" }} aria-label="recipe">
              {props.ownerUsername.charAt(0)}
            </Avatar>
          }
          title={props.title}
          subheader={`${props.ownerUsername} ${new Date(
            props.updateTimeStamp
          ).toDateString()}`}
        />
        <CardContent>
          <MDEditor.Markdown
            source={props.content}
            rehypePlugins={[[rehypeSanitize]]}
            style={{
              whiteSpace: "pre-wrap",
              padding: 1,
              backgroundColor: "white",
              color: "black",
            }}
          />
        </CardContent>
        {props.owner && (
          <CardActions disableSpacing>
            <IconButton aria-label="delete">
              <DeleteIcon />
            </IconButton>
            {props.noteVisibility !== NoteVisibility.PUBLIC && (
              <IconButton aria-label="share">
                <ShareIcon />
              </IconButton>
            )}
          </CardActions>
        )}
      </Card>
    </div>
  );
};
