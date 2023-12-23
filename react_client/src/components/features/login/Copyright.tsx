import { Link, Typography } from "@mui/material";
import { FC } from "react";

export const Copyright: FC = () => {
  return (
    <div className={"flex flex-col gap-1 mt-2"}>
      <p className="text-sm font-sans text-center text-gray-500">Copyright ©</p>
      <p className="text-sm font-sans text-center text-gray-500">
        Secure-Markdown Adrian Nowosielski
      </p>
      <p className="text-sm font-sans text-center text-gray-500">
        {new Date().getFullYear()}
      </p>
    </div>
  );
};
