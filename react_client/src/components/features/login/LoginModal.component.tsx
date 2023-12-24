import { FC, useState } from "react";
import { ModalProperties } from "../../../core/types/Modal.types";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Grid,
  TextField,
} from "@mui/material";
import * as yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import { useForm } from "react-hook-form";
import { axiosApi } from "../../../tools/configuration/axios-config";
import AppSnackbar from "../../AppSnackbar";
import axios, { AxiosError } from "axios";
import { LoadingButton } from "@mui/lab";

const resetPasswordValidator = yup.object().shape({
  email: yup
    .string()
    .required("Email field is required")
    .email("This field has to be an email"),
});
type ResetPasswordRequest = {
  email: string;
};

export const LoginModal: FC<ModalProperties> = (props: ModalProperties) => {
  const {
    setValue,
    handleSubmit,
    formState: { errors },
    reset
  } = useForm({
    resolver: yupResolver(resetPasswordValidator),
  });

  const { isOpen, handleClose } = props;
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  

  function sendPasswordResetRequest(
    resetPasswordRequest: ResetPasswordRequest
  ) {
    setLoading(true);
    
    axiosApi.post("/api/auth/reset-password", resetPasswordRequest)
    .then(result => {
        setSuccess(true);
    }).catch((error) => {
      setError("Error occured when trying to reset password")
    }).finally(() => setLoading(false));
  }

  return (
    <Dialog open={isOpen} onClose={handleClose}>
      <AppSnackbar
        open={!!error}
        message={error}
        onClose={() => setError(null)}
        severity="error"
      />
      <AppSnackbar
        open={!!success}
        message={'Successfuly sent an email with recover link!'}
        onClose={() => {
          setSuccess(false);
          reset();
          handleClose();
        }
        }
        severity="success"
        duration={2000}
      />
      <DialogTitle>Reset password</DialogTitle>
      <DialogContent>
        <DialogContentText>
          To reset youur password, please enter your email address here. We will
          send you the link to reset your password!
        </DialogContentText>
        <TextField
          autoFocus
          onChange={(event) => setValue("email", event.target.value)}
          error={!!errors.email}
          helperText={!!errors.email ? errors.email.message : ""}
          margin="dense"
          id="name"
          label="Email Address"
          type="email"
          fullWidth
          variant="standard"
        />
      </DialogContent>
      <DialogActions sx={{ justifyContent: "center" }}>
        <LoadingButton
          onClick={handleSubmit(sendPasswordResetRequest)}
          color={"success"}
          variant={"contained"}
          loading={loading}
        >
          Reset password
        </LoadingButton>
        <Button onClick={handleClose} color={"error"} variant={"contained"}>
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
  );
};
