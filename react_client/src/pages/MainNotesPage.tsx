import { FC, useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router";
import {
  MainPageNote,
  NoteDTO,
} from "../components/features/main-page/MainPageNote.component";
import { axiosApi } from "../tools/configuration/axios-config";

export const MainNotesPage: FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [visibility, setVisibility] = useState<string | null>(null);
  const [notes, setNotes] = useState<Array<NoteDTO>>([]);
  console.log(process.env.REACT_APP_BACKEND_URL);

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const visibility = queryParams.get("visibility");
    setVisibility(visibility ?? null);

    if (!visibility) {
      navigate("/note?visibility=public");
      return;
    }

    axiosApi
      .get<Array<NoteDTO>>(`/api/note/${visibility}`)
      .then((result) => {
        setNotes(result.data);
      })
      .catch((error) => {
        console.log(error);
      });
  }, [visibility, navigate, location.search]);

  return (
    <div>
      <p className={"text-center text-3xl text-white font-bold mb-4"}>
        {!!visibility &&
          visibility.charAt(0).toUpperCase() + visibility?.slice(1) + " "}
        Notes
      </p>
      <div className="grid grid-cols-4 justify-center items-center gap-4">
        {notes.map((note, id) => {
          return (
            <MainPageNote
              key={id}
              id={note.id}
              title={note.title}
              content={note.content}
              updateTimeStamp={note.updateTimeStamp}
              ownerUsername={note.ownerUsername}
              noteVisibility={note.noteVisibility}
              owner={note.owner}
            />
          );
        })}
      </div>
    </div>
  );
};
