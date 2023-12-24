import {
  createBrowserRouter,
  Link,
  Outlet,
  useNavigate,
} from "react-router-dom";
import { RoutingConstants } from "../../core/constants/RoutingConstants";
import { LoginPage } from "../../pages/LoginPage";

import { FC } from "react";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import LogoutIcon from "@mui/icons-material/Logout";
import PublicIcon from "@mui/icons-material/Public";
import PublicOffIcon from "@mui/icons-material/PublicOff";
import EnhancedEncryptionIcon from "@mui/icons-material/EnhancedEncryption";
import ShieldIcon from "@mui/icons-material/Shield";
import { NoteAddPage } from "../../pages/NoteAddPage";
import { MainNotesPage } from "../../pages/MainNotesPage";
import { RegisterPage } from "../../pages/RegisterPage";
import { NotePage } from "../../pages/NotePage";
import { ResetPasswordPage } from "../../pages/ResetPasswordPage";
import { axiosApi } from "./axios-config";

export const BasicLayout: FC = () => {
  const navigate = useNavigate();

  function handleLogoutCall(): void {
    axiosApi
      .post("/api/auth/logout")
      .then((result) => {
        if (result.status === 200) {
          navigate("/login");
        }
      })
      .catch((error) => {
        console.log(error);
      });
  }

  return (
    <div className="flex bg-gray-800">
      <div className="top-0 left-0 z-40 w-64 h-screen transition-transform -translate-x-full sm:translate-x-0 w-1/10 bg-gray-800">
        <div className="h-full px-3 py-4 bg-gray-800">
          <div className="flex items-center p-2 rounded-lg text-white hover:bg-gray-700 group justify-center gap-2">
            <ShieldIcon />
            <p className={"text-white font-bold text-center"}>
              Secure markdown
            </p>
          </div>
          <ul className="space-y-2 font-medium">
            <li>
              <Link
                to="/note-add"
                className="flex items-center p-2 rounded-lg text-white hover:bg-gray-700 group"
              >
                <AddCircleIcon />
                <span className="ms-3">Add note</span>
              </Link>
            </li>
            <li>
              <div
                className="flex items-center p-2 rounded-lg text-white hover:bg-gray-700 group cursor-pointer"
                onClick={() => handleLogoutCall()}
              >
                <LogoutIcon />
                <span className="ms-3">Logout</span>
              </div>
            </li>
          </ul>
          <ul className="pt-4 mt-4 space-y-2 font-medium border-t border-gray-200 dark:border-gray-700">
            <li>
              <div
                onClick={() => navigate("/note?visibility=public")}
                className="flex items-center p-2 rounded-lg text-white hover:bg-gray-700 group cursor-pointer"
              >
                <PublicIcon />
                <span className="ms-3">Public</span>
              </div>
            </li>
            <li>
              <div
                onClick={() => navigate("/note?visibility=private")}
                className="flex items-center p-2 rounded-lg text-white hover:bg-gray-700 group cursor-pointer"
              >
                <PublicOffIcon />
                <span className="ms-3">Private</span>
              </div>
            </li>
            <li>
              <div
                onClick={() => navigate("/note?visibility=encrypted")}
                className="flex items-center p-2 rounded-lg text-white hover:bg-gray-700 group cursor-pointer"
              >
                <EnhancedEncryptionIcon />
                <span className="ms-3">Encrypted</span>
              </div>
            </li>
          </ul>
        </div>
      </div>
      <div className="w-full h-auto p-2 bg-gray-700">
        <Outlet />
      </div>
    </div>
  );
};

export const routes = createBrowserRouter([
  {
    path: RoutingConstants.LOGIN_PAGE,
    element: <LoginPage />,
  },
  {
    path: "/reset-password",
    element: <ResetPasswordPage />,
  },
  {
    path: RoutingConstants.REGISTER_PAGE,
    element: <RegisterPage />,
  },
  {
    path: RoutingConstants.MAIN_PAGE,
    element: <BasicLayout />,
    children: [
      {
        path: "",
        element: <MainNotesPage />,
      },
      {
        path: RoutingConstants.NOTE_ADD,
        element: <NoteAddPage />,
      },
      {
        path: "note",
        element: <MainNotesPage />,
      },
      {
        path: "note/view/:noteId",
        element: <NotePage />,
      },
    ],
  },
]);
