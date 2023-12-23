import { FC } from "react";
import { Navigate, useLocation } from "react-router";
import { MainPageNote } from "../components/features/main-page/MainPageNote.component";

export const MainNotesPage: FC = () => {
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const visibility = queryParams.get("visibility");

  if (!visibility) {
    return <Navigate to={'/login'}/>
  }

  return (
    <div>
      <p className={"text-center text-3xl text-white font-bold mb-4"}>
        {visibility?.charAt(0).toUpperCase() + visibility?.slice(1)} Notes
      </p>
      <div className="grid grid-cols-4 justify-center items-center gap-4">
        <MainPageNote />
        <MainPageNote />
        <MainPageNote />
        <MainPageNote />
        <MainPageNote />
      </div>
    </div>
  );
};
