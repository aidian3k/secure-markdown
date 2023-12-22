import { createBrowserRouter } from "react-router-dom";
import { RoutingConstants } from "../../core/constants/RoutingConstants";
import React from "react";
import { LoginPage } from "../../pages/LoginPage";
import { MainNotesPage } from "../../pages/MainNotesPage";
import { NoteAddPage } from "../../pages/NoteAddPage";

export const routes = createBrowserRouter([
  {
    path: RoutingConstants.LOGIN_PAGE,
    element: <LoginPage />,
  },
  {
    path: RoutingConstants.MAIN_PAGE,
    element: <MainNotesPage />,
  },
  {
    path: RoutingConstants.NOTE_ADD,
    element: <NoteAddPage />,
  },
  {
    path: RoutingConstants.NOTE_DETAILS,
  },
]);
