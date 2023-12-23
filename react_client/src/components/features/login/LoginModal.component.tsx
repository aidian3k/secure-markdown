import { FC } from "react";
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
  } = useForm({
    resolver: yupResolver(resetPasswordValidator),
  });

  const { isOpen, handleClose } = props;

  function sendPasswordResetRequest(
    resetPasswordRequest: ResetPasswordRequest
  ) {
    console.log(resetPasswordRequest);
  }

  return (
    <Dialog open={isOpen} onClose={handleClose}>
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
        <Button
          onClick={handleSubmit(sendPasswordResetRequest)}
          color={"success"}
          variant={"contained"}
        >
          Reset password
        </Button>
        <Button onClick={handleClose} color={"error"} variant={"contained"}>
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
  );
};
