import React from "react";
import Snackbar from "@mui/material/Snackbar";
import MuiAlert, { AlertColor } from "@mui/material/Alert";
import Alert from "@mui/material/Alert";

interface SnackBarProperties {
  open: boolean;
  message: string | null;
  onClose: () => void;
  severity?: AlertColor,
  duration?: number
}

const AppSnackbar: React.FC<SnackBarProperties> = ({
  open,
  message,
  onClose,
  severity,
  duration
}) => {
  return (
    <Snackbar open={open} autoHideDuration={!!duration ? duration : 5000} onClose={onClose}>
      <Alert onClose={onClose} severity={!!severity ? severity : 'error'}>
        {message}
      </Alert>
    </Snackbar>
  );
};

export default AppSnackbar;
